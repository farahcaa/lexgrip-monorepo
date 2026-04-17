package com.lexgrip.app.platform.service.feedback;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedBackRepository extends JpaRepository<UUID, FeedbackEntity> {

}
