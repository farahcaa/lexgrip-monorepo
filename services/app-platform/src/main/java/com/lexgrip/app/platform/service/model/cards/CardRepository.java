package com.lexgrip.app.platform.service.model.cards;

import com.lexgrip.app.platform.service.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CardRepository extends JpaRepository<CardEntity, UUID> {
    Page<CardEntity> findAllByLanguageIdAndUser(UUID languageId, UserEntity user, Pageable pageable);
}
