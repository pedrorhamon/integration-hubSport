package com.starking.hubsport.controller;

import com.starking.hubsport.service.TokenStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @Autowired
    private TokenStorageService tokenStorageService;

    @GetMapping("/authorize-url")
    public ResponseEntity<String> getAuthorizationUrl() {
        return ResponseEntity.ok(tokenStorageService.getAuthorizationUrl());
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code) {
        try {
            tokenStorageService.exchangeCodeForToken(code);
            return ResponseEntity.ok("Token salvo com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o callback: " + e.getMessage());
        }
    }

}
