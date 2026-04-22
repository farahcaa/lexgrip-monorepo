package com.lexgrip.app.platform.service.model.languages;

import java.util.UUID;

public class LanguageDTO {

    private UUID id;

    private String color;

    private String name;

    private int cardCount;

    public UUID getId() {
        return id;
    }

    public LanguageDTO setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getColor() {
        return color;
    }

    public LanguageDTO setColor(String color) {
        this.color = color;
        return this;
    }

    public String getName() {
        return name;
    }

    public LanguageDTO setName(String name) {
        this.name = name;
        return this;
    }

    public int getCardCount() {
        return cardCount;
    }

    public LanguageDTO setCardCount(int cardCount) {
        this.cardCount = cardCount;
        return this;
    }
}
