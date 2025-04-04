package com.starking.hubsport.model.response;

import com.starking.hubsport.model.HubspotContact;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HubspotContactResponse {

    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private String fullName;
    private String lifecycleStage;
    private String emailDomain;
    private Instant createdAt;
    private Instant updatedAt;

    public HubspotContactResponse(HubspotContact entity) {
        this.id = entity.getId();
        this.email = entity.getEmail();
        this.firstname = entity.getFirstname();
        this.lastname = entity.getLastname();
        this.fullName = entity.getFullName();
        this.lifecycleStage = entity.getLifecycleStage();
        this.emailDomain = entity.getEmailDomain();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }
}