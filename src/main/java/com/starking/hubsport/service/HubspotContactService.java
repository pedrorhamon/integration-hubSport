package com.starking.hubsport.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starking.hubsport.model.HubspotContact;
import com.starking.hubsport.model.WebhookEventLog;
import com.starking.hubsport.model.response.HubspotContactResponse;
import com.starking.hubsport.repository.HubspotContactRepository;
import com.starking.hubsport.repository.WebhookEventLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.starking.hubsport.util.ErrorMessages.CONTACT_NOT_FOUND_HUBSPORT;
import static com.starking.hubsport.util.ErrorMessages.ERRO_SEARCH_CONTACT_HUBSPORT;
import static com.starking.hubsport.util.HubspotUrls.CONTACTS_ENDPOINT;


@Slf4j
@Service
@RequiredArgsConstructor
public class HubspotContactService {

    private final HubspotContactRepository repository;
    private final WebhookEventLogRepository eventLogRepository;
    private final TokenStorageService tokenStorageService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public void fetchAndSaveContact(Long objectId) {
        String accessToken = tokenStorageService.getAccessToken();

        List<String> properties = List.of(
                "firstname", "lastname", "email", "lifecyclestage",
                "createdate", "lastmodifieddate", "archived",
                "hs_full_name_or_email", "hs_email_domain"
        );

        String url = UriComponentsBuilder.fromHttpUrl("https://api.hubapi.com/crm/v3/objects/contacts/{id}")
                .queryParam("properties", String.join(",", properties))
                .buildAndExpand(objectId)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            JsonNode body = response.getBody();

            if (body != null) {
                JsonNode props = body.get("properties");

                String created = props.path("createdate").asText(null);
                String modified = props.path("lastmodifieddate").asText(null);

                HubspotContact contact = HubspotContact.builder()
                        .id(Long.parseLong(body.get("id").asText()))
                        .firstname(props.path("firstname").asText(null))
                        .lastname(props.path("lastname").asText(null))
                        .email(props.path("email").asText(null))
                        .fullName(props.path("hs_full_name_or_email").asText(null))
                        .emailDomain(props.path("hs_email_domain").asText(null))
                        .lifecycleStage(props.path("lifecyclestage").asText(null))
                        .createdAt(created != null ? Instant.parse(created) : null)
                        .updatedAt(modified != null ? Instant.parse(modified) : null)
                        .build();

                repository.save(contact);
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn(CONTACT_NOT_FOUND_HUBSPORT, objectId);
        } catch (Exception e) {
            log.error(ERRO_SEARCH_CONTACT_HUBSPORT, e);
        }
    }

    public ResponseEntity<String> createContact(Map<String, String> contactData) {
        String accessToken = tokenStorageService.getAccessToken();

        Map<String, Object> properties = new HashMap<>(contactData);
        Map<String, Object> requestBody = Map.of("properties", properties);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        String url = CONTACTS_ENDPOINT;

        return restTemplate.postForEntity(url, request, String.class);
    }

    public Page<HubspotContactResponse> getAllContacts(Pageable pageable) {
        return repository.findAll(pageable)
                .map(HubspotContactResponse::new);
    }

    public void logWebhookEvent(String eventType, Long objectId, String changeSource, String changeFlag, String rawPayload) {
        WebhookEventLog log = WebhookEventLog.builder()
                .eventType(eventType)
                .objectId(objectId)
                .changeSource(changeSource)
                .changeFlag(changeFlag)
                .rawPayload(rawPayload)
                .receivedAt(Instant.now())
                .build();
        eventLogRepository.save(log);
    }

    public HubspotContactResponse getContactById(Long id) {
        HubspotContact contact = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Contato com ID " + id + " não encontrado"));
        return new HubspotContactResponse(contact);
    }

    public List<WebhookEventLog> getAllWebhookEvents() {
        return eventLogRepository.findAll();
    }

    public ResponseEntity<InputStreamResource> exportWebhookEventsToCsv() {
        List<WebhookEventLog> events = eventLogRepository.findAll();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        writer.println("ID,EventType,ObjectId,ChangeSource,ChangeFlag,ReceivedAt");

        for (WebhookEventLog log : events) {
            writer.printf("%d,%s,%d,%s,%s,%s\n",
                    log.getId(),
                    log.getEventType(),
                    log.getObjectId(),
                    log.getChangeSource(),
                    log.getChangeFlag(),
                    log.getReceivedAt()
            );
        }

        writer.flush();
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=webhook-events.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

}

