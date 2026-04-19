package com.lexgrip.app.platform.service.model.user;

import com.lexgrip.app.platform.service.application.account.AccountPageDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    AccountPageDTO toAccountPageDTO(UserEntity user);
}
