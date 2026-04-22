package com.lexgrip.app.platform.service.application.languages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateLanguageRequest {

    @NotBlank(message = "languageName must not be blank")
    @Size(max = 100, message = "languageName must be at most 100 characters")
    private String languageName;

    public String getLanguageName() {
        return languageName;
    }

    public CreateLanguageRequest setLanguageName(String languageName) {
        this.languageName = languageName;
        return this;
    }
}
