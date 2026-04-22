package com.lexgrip.app.platform.service.application.account;

import com.lexgrip.app.platform.service.model.user.UserEntity;
import com.lexgrip.app.platform.service.model.user.UserMapper;
import com.lexgrip.app.platform.service.model.user.UserRepository;
import com.lexgrip.app.platform.service.model.user.dtos.MeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final UserMapper userMapper;

    public AccountService(UserMapper userMapper, UserRepository userRepository){
        this.userMapper = userMapper;
    }

    public AccountPageDTO getAccountPage(UserEntity user){

        return userMapper.toAccountPageDTO(user);

    }

    public MeDTO getMe(UserEntity user) {
        return userMapper.toMeDTO(user);
    }
}
