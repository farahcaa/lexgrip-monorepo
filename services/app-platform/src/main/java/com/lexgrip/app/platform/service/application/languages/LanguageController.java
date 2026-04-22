package com.lexgrip.app.platform.service.application.languages;

import com.lexgrip.app.platform.service.model.languages.LanguageDTO;
import com.lexgrip.app.platform.service.model.user.UserEntity;
import com.lexgrip.app.platform.service.resolver.CurrentUserEntity;
import com.lexgrip.common.api.model.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/app/language")
public class LanguageController {

    private final LanguageService languageService;

    public LanguageController(LanguageService languageService){
        this.languageService = languageService;
    }

    @GetMapping()
    public ApiResponse<List<LanguageDTO>> getLanguages(@CurrentUserEntity UserEntity user){
        return languageService.getLanguages(user);
    }

    @PostMapping()
    public ApiResponse<String> createLanguage(
            @CurrentUserEntity UserEntity user,
            @Valid @RequestBody CreateLanguageRequest request
    ){
        return languageService.createLanguage(request.getLanguageName().trim(), user);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteLanguage(@PathVariable UUID id, @CurrentUserEntity UserEntity user){
        return languageService.deleteLanguage(id, user);
    }
}
