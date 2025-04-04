package com.starking.hubsport.controller;

import com.starking.hubsport.model.request.CreateContactRequest;
import com.starking.hubsport.model.response.HubspotContactResponse;
import com.starking.hubsport.service.HubspotContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/hubspot")
public class HubspotController {

    @Autowired
    private HubspotContactService hubspotContactService;

    @PostMapping("/contact")
    public ResponseEntity<?> createContact(@RequestBody CreateContactRequest contactRequest) {
        try {
            Map<String, String> contactData = Map.of(
                    "firstname", contactRequest.getFirstname(),
                    "lastname", contactRequest.getLastname(),
                    "email", contactRequest.getEmail()
            );
            ResponseEntity<String> response = hubspotContactService.createContact(contactData);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar contato: " + e.getMessage());
        }
    }

    @GetMapping("/contact")
    public ResponseEntity<Page<HubspotContactResponse>> getAllContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<HubspotContactResponse> contacts = hubspotContactService.getAllContacts(pageable);
        return ResponseEntity.ok(contacts);
    }


    @GetMapping("/contact/{id}")
    public ResponseEntity<HubspotContactResponse> getContactById(@PathVariable Long id) {
        return ResponseEntity.ok(hubspotContactService.getContactById(id));
    }
}
