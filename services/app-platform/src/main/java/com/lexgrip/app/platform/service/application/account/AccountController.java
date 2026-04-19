package com.lexgrip.app.platform.service.application.account;

import com.lexgrip.app.platform.service.model.user.UserEntity;
import com.lexgrip.app.platform.service.resolver.CurrentUserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }
    @GetMapping("")
    public ResponseEntity<AccountPageDTO> getAccountInfo(@CurrentUserEntity UserEntity user) {
        return ResponseEntity.ok(accountService.getAccountPage(user));
    }


}
