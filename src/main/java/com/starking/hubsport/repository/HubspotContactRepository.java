package com.starking.hubsport.repository;

import com.starking.hubsport.model.HubspotContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HubspotContactRepository extends JpaRepository<HubspotContact, Long> {
}
