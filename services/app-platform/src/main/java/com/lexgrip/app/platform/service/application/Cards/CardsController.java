package com.lexgrip.app.platform.service.application.Cards;

import com.lexgrip.app.platform.service.model.cards.CardDTO;
import com.lexgrip.app.platform.service.model.user.UserEntity;
import com.lexgrip.app.platform.service.resolver.CurrentUserEntity;
import com.lexgrip.common.api.model.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/app")
public class CardsController {

    private final CardsService cardsService;

    public CardsController(CardsService cardsService){
        this.cardsService = cardsService;
    }

    @GetMapping("/language/{id}/cards")
    public ApiResponse<Page<CardDTO>> getCards(
            @PathVariable UUID id,
            Pageable pageable,
            @CurrentUserEntity UserEntity user
    ){
        return cardsService.getCards(id, pageable, user);
    }
}
