package com.lexgrip.app.platform.service.ai;

import java.util.ArrayList;
import java.util.List;

public class GeneratedCardResponse {

    private List<GeneratedCard> cards = new ArrayList<>();

    public List<GeneratedCard> getCards() {
        return cards;
    }

    public GeneratedCardResponse setCards(List<GeneratedCard> cards) {
        this.cards = cards == null ? new ArrayList<>() : cards;
        return this;
    }

    public static class GeneratedCard {
        private String categoryName;
        private String frontText;
        private String backText;
        private String exampleText;

        public String getCategoryName() {
            return categoryName;
        }

        public GeneratedCard setCategoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public String getFrontText() {
            return frontText;
        }

        public GeneratedCard setFrontText(String frontText) {
            this.frontText = frontText;
            return this;
        }

        public String getBackText() {
            return backText;
        }

        public GeneratedCard setBackText(String backText) {
            this.backText = backText;
            return this;
        }

        public String getExampleText() {
            return exampleText;
        }

        public GeneratedCard setExampleText(String exampleText) {
            this.exampleText = exampleText;
            return this;
        }
    }
}
