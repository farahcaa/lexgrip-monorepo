package com.lexgrip.app.platform.service.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface FlashcardAiService {

    @SystemMessage("""
        You generate language-learning flashcards.
        Return only valid structured data.
        Generate up to 3 cards per request.
        Every card must include categoryName.
        All cards in one response must use the same categoryName.
        If you cannot produce 3 good cards, return fewer and stop.
        The category name should be in English.
        Please use proper etiquette when giving flash cards.
        Response format:
        {
          "cards": [
            {
              "categoryName": "string",
              "frontText": "string",
              "backText": "string",
              "exampleText": "string or null"
            }
          ]
        }
        """)
    GeneratedCardResponse generateCard(@UserMessage String prompt);
}
