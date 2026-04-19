package com.lexgrip.app.platform.service.model.decks;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeckRepository extends JpaRepository<DeckEntity, UUID> {
}
