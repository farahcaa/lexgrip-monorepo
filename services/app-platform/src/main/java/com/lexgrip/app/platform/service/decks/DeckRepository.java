package com.lexgrip.app.platform.service.decks;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeckRepository extends JpaRepository<UUID,DeckEntity> {
}
