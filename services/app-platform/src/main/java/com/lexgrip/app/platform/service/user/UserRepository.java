package com.lexgrip.app.platform.service.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UUID, UserEntity> {
}
