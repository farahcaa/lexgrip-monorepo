package com.lexgrip.app.platform.service.application.Cards;

import com.lexgrip.app.platform.service.model.cards.CardDTO;
import com.lexgrip.app.platform.service.model.cards.CardMapper;
import com.lexgrip.app.platform.service.model.cards.CardRepository;
import com.lexgrip.app.platform.service.model.user.UserEntity;
import com.lexgrip.common.api.model.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CardsService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    public CardsService(CardRepository cardRepository, CardMapper cardMapper){
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
    }

    public ApiResponse<Page<CardDTO>> getCards(UUID id, Pageable pageable, UserEntity user) {
        Page<CardDTO> cardsPage = cardRepository
                .findAllByLanguageIdAndUser(id, user, pageable)
                .map(cardMapper::toCardDTO);

        return ApiResponse.success(cardsPage);
    }
}
