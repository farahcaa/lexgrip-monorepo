package com.lexgrip.app.platform.service.model.feedback;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedBackRepository extends JpaRepository<FeedbackEntity, UUID> {

}
