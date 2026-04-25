package com.lexgrip.app.platform.service.model.user;

import com.lexgrip.app.platform.service.application.account.AccountPageDTO;
import com.lexgrip.app.platform.service.model.user.dtos.MeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(
            target = "cardsLeft",
            expression = "java((user.getCardLimit() == null ? 0 : user.getCardLimit()) - (user.getCardsUsed() == null ? 0 : user.getCardsUsed()))"
    )
    AccountPageDTO toAccountPageDTO(UserEntity user);

    MeDTO toMeDTO(UserEntity user);
}
