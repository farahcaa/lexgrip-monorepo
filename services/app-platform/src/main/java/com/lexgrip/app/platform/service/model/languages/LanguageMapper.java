package com.lexgrip.app.platform.service.model.languages;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LanguageMapper {

    @Mapping(target = "cardCount", expression = "java(languageEntity.getCards() == null ? 0 : languageEntity.getCards().size())")
    LanguageDTO toLanguageDTO(LanguageEntity languageEntity);
    List<LanguageDTO> toListLanguageDTO(List<LanguageEntity> languageEntities);

}
