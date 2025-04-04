package com.starking.hubsport.repository;

import com.starking.hubsport.model.WebhookEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookEventLogRepository extends JpaRepository<WebhookEventLog, Long> {
}