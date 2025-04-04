package com.starking.hubsport.service;

import com.starking.hubsport.model.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenStorageService {

    @Value("${hubspot.client-id}")
    private String clientId;

    @Value("${hubspot.client-secret}")
    private String clientSecret;

    @Value("${hubspot.redirect-uri}")
    private String redirectUri;

    private static final String AUTH_BASE_URL = "https://app.hubspot.com/oauth/authorize";

    private final RestTemplate restTemplate = new RestTemplate();

    private final AtomicReference<TokenResponse> tokenRef = new AtomicReference<>();
    private final AtomicReference<Instant> expiresAt = new AtomicReference<>();

    public synchronized String getAccessToken() {
        TokenResponse current = tokenRef.get();
        if (current == null || isExpired()) {
            log.info("Token expirado ou ausente. Renovando...");
            refreshToken();
        }
        return tokenRef.get().accessToken();
    }

    public void saveInitialToken(TokenResponse tokenResponse) {
        tokenRef.set(tokenResponse);
        expiresAt.set(Instant.now().plusSeconds(tokenResponse.expiresIn()));
    }

    private void refreshToken() {
        TokenResponse current = tokenRef.get();
        if (current == null || current.refreshToken() == null) {
            throw new IllegalStateException("Nenhum refresh token disponível");
        }

        String url = "https://api.hubapi.com/oauth/v1/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = Map.of(
                "grant_type", "refresh_token",
                "client_id", clientId,
                "client_secret", clientSecret,
                "refresh_token", current.refreshToken()
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);

        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                TokenResponse.class
        );

        TokenResponse newToken = response.getBody();
        if (newToken != null) {
            tokenRef.set(newToken);
            expiresAt.set(Instant.now().plusSeconds(newToken.expiresIn()));
            log.info("Token renovado com sucesso");
        } else {
            throw new IllegalStateException("Erro ao renovar token");
        }
    }

    public void exchangeCodeForToken(String code) {
        String url = "https://api.hubapi.com/oauth/v1/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                TokenResponse.class
        );

        TokenResponse token = response.getBody();
        if (token != null) {
            saveInitialToken(token);
            log.info("Token OAuth salvo via código de autorização");
        } else {
            throw new IllegalStateException("Falha ao trocar o código por token.");
        }
    }


    public String getAuthorizationUrl() {
        String scopes = URLEncoder.encode("crm.objects.contacts.read crm.objects.contacts.write crm.objects.deals.read", StandardCharsets.UTF_8);
        return AUTH_BASE_URL +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=" + scopes +
                "&response_type=code";
    }

    private boolean isExpired() {
        Instant exp = expiresAt.get();
        return exp == null || Instant.now().isAfter(exp.minusSeconds(60));
    }
}
