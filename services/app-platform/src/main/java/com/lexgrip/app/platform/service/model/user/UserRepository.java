package com.lexgrip.app.platform.service.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> findAllByFirstGenerateRequestAtBeforeAndCardsUsedGreaterThan(OffsetDateTime offsetDateTime, int cardsUsed);
}
