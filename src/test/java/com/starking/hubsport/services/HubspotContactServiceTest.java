package com.starking.hubsport.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starking.hubsport.model.HubspotContact;
import com.starking.hubsport.model.WebhookEventLog;
import com.starking.hubsport.model.response.HubspotContactResponse;
import com.starking.hubsport.repository.HubspotContactRepository;
import com.starking.hubsport.repository.WebhookEventLogRepository;
import com.starking.hubsport.service.HubspotContactService;
import com.starking.hubsport.service.TokenStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HubspotContactServiceTest {

    @InjectMocks
    private HubspotContactService service;

    @Mock
    private HubspotContactRepository repository;

    @Mock
    private WebhookEventLogRepository logRepository;

    @Mock
    private TokenStorageService tokenStorageService;

    @Mock
    private WebhookEventLogRepository eventLogRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllContacts_deveRetornarListaDeContatos() {
        when(repository.findAll()).thenReturn(List.of(new HubspotContact()));

        Pageable pageable = PageRequest.of(0, 10);
        Page<HubspotContactResponse> result = service.getAllContacts(pageable);

        assertEquals(1, result);
        verify(repository, times(1)).findAll();
    }

    @Test
    void getContactById_existente_deveRetornarContato() {
        Long id = 1L;
        HubspotContact mockContact = HubspotContact.builder().id(id).email("teste@email.com").build();
        when(repository.findById(id)).thenReturn(Optional.of(mockContact));

        HubspotContactResponse response = service.getContactById(id);


        assertEquals(200, response);
        assertThat(response).isInstanceOf(HubspotContactResponse.class);
    }

    @Test
    void getContactById_naoEncontrado_deveLancarExcecao() {
        Long id = 2L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> service.getContactById(id));
        assertTrue(exception.getMessage().contains("Contato com ID " + id + " não encontrado"));
    }

    @Test
    void shouldReturnContactById() {
        HubspotContact contact = HubspotContact.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(contact));

        HubspotContactResponse response = service.getContactById(1L);

        assertEquals("John", response.getFirstname());
        assertEquals("Doe", response.getLastname());
        assertEquals("john@example.com", response.getEmail());
    }

    @Test
    void shouldThrowWhenContactNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.getContactById(999L));
    }

    @Test
    void shouldLogWebhookEvent() {
        service.logWebhookEvent("contact.creation", 123L, "CRM", "NEW", "{}{}");

        verify(eventLogRepository, times(1)).save(any(WebhookEventLog.class));
    }

    @Test
    void shouldExportWebhookEventsToCsv() throws Exception {
        WebhookEventLog event = WebhookEventLog.builder()
                .id(1L)
                .eventType("contact.creation")
                .objectId(123L)
                .changeSource("CRM")
                .changeFlag("NEW")
                .receivedAt(Instant.now())
                .build();

        when(eventLogRepository.findAll()).thenReturn(List.of(event));

        ResponseEntity<InputStreamResource> response = service.exportWebhookEventsToCsv();

        assertEquals("attachment; filename=webhook-events.csv", response.getHeaders().getFirst("Content-Disposition"));
        assertNotNull(response.getBody());

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().getInputStream(), StandardCharsets.UTF_8));
        String headerLine = reader.readLine();
        String dataLine = reader.readLine();

        assertEquals("ID,EventType,ObjectId,ChangeSource,ChangeFlag,ReceivedAt", headerLine);
        assertTrue(dataLine.contains("contact.creation"));
    }
}
