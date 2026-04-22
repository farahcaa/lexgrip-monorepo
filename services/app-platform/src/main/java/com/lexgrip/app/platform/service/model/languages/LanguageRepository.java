package com.lexgrip.app.platform.service.model.languages;

import com.lexgrip.app.platform.service.model.user.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LanguageRepository extends JpaRepository<LanguageEntity, UUID> {
    @EntityGraph(attributePaths = "cards")
    List<LanguageEntity> findAllByUser(UserEntity user);

    LanguageEntity findByIdAndUser(UUID id, UserEntity user);
}
