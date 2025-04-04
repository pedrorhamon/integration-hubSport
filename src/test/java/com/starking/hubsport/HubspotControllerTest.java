package com.starking.hubsport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starking.hubsport.controller.HubspotController;
import com.starking.hubsport.model.request.CreateContactRequest;
import com.starking.hubsport.service.HubspotContactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HubspotController.class)
public class HubspotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HubspotContactService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateContact() throws Exception {
        CreateContactRequest request = new CreateContactRequest("john", "doe", "john@example.com");

        mockMvc.perform(post("/hubspot/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetAllContacts() throws Exception {
        mockMvc.perform(get("/hubspot/contact"))
                .andExpect(status().isOk());
    }
}
