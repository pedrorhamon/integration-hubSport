package com.starking.hubsport.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "hubspot_contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubspotContact {

    @Id
    private Long id;

    private String email;
    private String firstname;
    private String lastname;

    private String fullName;
    private String lifecycleStage;
    private String emailDomain;

    private Instant createdAt;
    private Instant updatedAt;
}
