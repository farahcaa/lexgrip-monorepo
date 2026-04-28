package com.lexgrip.app.platform.service.cron;

import com.lexgrip.app.platform.service.model.user.UserEntity;
import com.lexgrip.app.platform.service.model.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class ResetUserLimits {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResetUserLimits.class);
    private static final int DEFAULT_CARD_LIMIT = 10;
    private static final int RESET_WINDOW_MINUTES = 3;

    private final UserRepository userRepository;

    public ResetUserLimits(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void resetUserLimits() {
        OffsetDateTime cutoff = OffsetDateTime.now().minusMinutes(RESET_WINDOW_MINUTES);
        List<UserEntity> usersToReset =
                userRepository.findAllByFirstGenerateRequestAtBeforeAndCardsUsedGreaterThan(cutoff, 0
                );

        if (usersToReset.isEmpty()) {
            return;
        }

        for (UserEntity user : usersToReset) {
            user.setCardLimit(DEFAULT_CARD_LIMIT);
            user.setCardsUsed(0);
            user.setFirstGenerateRequestAt(null);
        }

        userRepository.saveAll(usersToReset);
        LOGGER.info("Reset usage limits for {} users", usersToReset.size());
    }
}
