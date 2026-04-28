package com.lexgrip.app.platform.service.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface FlashcardAiService {

    @SystemMessage("""
You generate language-learning flashcards.

Return ONLY valid JSON matching this exact structure:
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

Rules:
- Generate 1 to 3 cards.
- All cards must use the exact same categoryName.
- categoryName must ALWAYS be English.
- frontText must be exactly one single word in the target language.
- frontText must NOT be a phrase.
- backText must be the most common English translation of frontText, ideally a single word.
- Do not write a definition, description, or explanation. Just the translation itself.
- Only use multiple words if no single English word captures the meaning (e.g., compound concepts, idioms).
- exampleText must be a full sentence in the target language.
- exampleText must contain the exact frontText word.
- Do not mix languages inside frontText.
- Do not translate categoryName into the target language.
- Do not include markdown, explanations, comments, or extra text.
- Use the most common modern meaning of the word, not a literal or outdated translation.
- If unsure, return fewer cards.
""")
    GeneratedCardResponse generateCard(@UserMessage String prompt);
}
