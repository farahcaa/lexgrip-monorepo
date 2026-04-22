package com.lexgrip.app.platform.service.model.user;

import com.lexgrip.app.platform.service.application.account.AccountPageDTO;
import com.lexgrip.app.platform.service.model.user.dtos.MeDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    AccountPageDTO toAccountPageDTO(UserEntity user);

    MeDTO toMeDTO(UserEntity user);
}
