package com.lexgrip.app.platform.service.model.cards;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardMapper {
    CardDTO toCardDTO(CardEntity cardEntity);
}
