package com.starking.hubsport.services;

import com.starking.hubsport.model.response.TokenResponse;
import com.starking.hubsport.service.TokenStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TokenStorageServiceTest {

    private TokenStorageService tokenStorageService;

    @BeforeEach
    void setUp() {
        tokenStorageService = new TokenStorageService();
    }

    @Test
    void shouldSaveAndRetrieveToken() {
        TokenResponse token = new TokenResponse("access", "refresh", 3600, "bearer", "scope");
        tokenStorageService.saveInitialToken(token);
        assertNotNull(tokenStorageService.getAccessToken());
        assertEquals("access", tokenStorageService.getAccessToken());
    }
}
