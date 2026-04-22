package com.lexgrip.app.platform.service.model.categories;

import com.lexgrip.app.platform.service.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    List<CategoryEntity> findByUserOrIsSystemTrue(UserEntity user);
}
