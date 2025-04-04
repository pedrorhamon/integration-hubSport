package com.starking.hubsport.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starking.hubsport.model.WebhookEventLog;
import com.starking.hubsport.service.HubspotContactService;
import com.starking.hubsport.service.TokenStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    private final HubspotContactService hubspotContactService;

    @PostMapping(value = "/hubspot", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> receiveWebhook(
            HttpServletRequest request,
            @RequestHeader(value = "X-HubSpot-Signature", required = false) String signature
    ) throws IOException {

        String rawPayload = new BufferedReader(new InputStreamReader(request.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

        logger.info("Webhook recebido do HubSpot!");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> events = objectMapper.readValue(rawPayload, new TypeReference<>() {
            });

            for (Map<String, Object> event : events) {
                String subscriptionType = (String) event.get("subscriptionType");
                Long objectId = Long.parseLong(event.get("objectId").toString());
                String changeSource = (String) event.get("changeSource");
                String changeFlag = (String) event.get("changeFlag");

                logger.info("Evento: {}", subscriptionType);
                logger.info("ID do objeto: {}", objectId);
                logger.info("Fonte: {}, Flag: {}", changeSource, changeFlag);

                if ("contact.creation".equals(subscriptionType)) {
                    hubspotContactService.fetchAndSaveContact(objectId);
                }
            }
            return ResponseEntity.ok("Recebido com sucesso!");

        } catch (Exception e) {
            logger.error("Erro ao processar eventos do webhook", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao processar eventos");
        }
    }

    @GetMapping
    public ResponseEntity<List<WebhookEventLog>> getAllEvents() {
        List<WebhookEventLog> events = hubspotContactService.getAllWebhookEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/webhook/events/export")
    public ResponseEntity<InputStreamResource> exportWebhookEvents() {
        return hubspotContactService.exportWebhookEventsToCsv();
    }
}