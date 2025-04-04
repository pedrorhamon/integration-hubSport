package com.starking.hubsport.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "webhook_event_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventType;
    private String subscriptionType;
    private Long objectId;
    private String changeSource;
    private String changeFlag;

    @Lob
    private String rawPayload;

    private Instant receivedAt;
}