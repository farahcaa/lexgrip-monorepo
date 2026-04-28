package com.lexgrip.app.platform.service.application.cards;

import com.lexgrip.app.platform.service.model.cards.CardDTO;
import com.lexgrip.app.platform.service.model.dto.PagedResponse;
import com.lexgrip.app.platform.service.model.user.UserEntity;
import com.lexgrip.app.platform.service.resolver.CurrentUserEntity;
import com.lexgrip.common.api.model.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/app")
public class CardsController {

    private final CardsService cardsService;

    public CardsController(CardsService cardsService){
        this.cardsService = cardsService;
    }

    @GetMapping("/language/{id}/cards")
    public PagedResponse<CardDTO> getCards(
            @PathVariable UUID id,
            Pageable pageable,
            @CurrentUserEntity UserEntity user
    ){
        return cardsService.getCards(id, pageable, user);
    }

    @PostMapping("/language/{id}/cards")
    public ApiResponse<String> createCard(
            @PathVariable UUID id,
            @CurrentUserEntity UserEntity user,
            @RequestBody CardDTO cardDTO
    ) {
        return cardsService.createCard(id, user, cardDTO);
    }

    @PostMapping("/language/{id}/cards/generate")
    public ApiResponse<String> generateCard(
            @PathVariable UUID id,
            @CurrentUserEntity UserEntity user,
            @RequestBody GenerateRequest request
    ){
        return cardsService.generateCards(request, user, id);
    }
}
