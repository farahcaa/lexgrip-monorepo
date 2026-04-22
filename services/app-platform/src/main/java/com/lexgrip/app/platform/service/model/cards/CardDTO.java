package com.lexgrip.app.platform.service.model.cards;

import com.lexgrip.app.platform.service.model.categories.CategoryEntity;
import com.lexgrip.app.platform.service.model.languages.LanguageEntity;
import com.lexgrip.app.platform.service.model.user.UserEntity;


import java.util.UUID;

public class CardDTO {

    private UUID id;

    private CategoryEntity category;

    private String frontText;

    private String backText;

    private String exampleText;

    // Getters
    public UUID getId() {
        return id;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public String getFrontText() {
        return frontText;
    }

    public String getBackText() {
        return backText;
    }

    public String getExampleText() {
        return exampleText;
    }

    // Fluent Setters
    public CardDTO setId(UUID id) {
        this.id = id;
        return this;
    }

    public CardDTO setCategory(CategoryEntity category) {
        this.category = category;
        return this;
    }

    public CardDTO setFrontText(String frontText) {
        this.frontText = frontText;
        return this;
    }

    public CardDTO setBackText(String backText) {
        this.backText = backText;
        return this;
    }

    public CardDTO setExampleText(String exampleText) {
        this.exampleText = exampleText;
        return this;
    }
}