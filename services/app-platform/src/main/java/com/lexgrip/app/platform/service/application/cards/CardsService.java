package com.lexgrip.app.platform.service.application.cards;

import com.lexgrip.app.platform.service.ai.FlashcardAiService;
import com.lexgrip.app.platform.service.ai.GeneratedCardResponse;
import com.lexgrip.app.platform.service.model.cards.CardDTO;
import com.lexgrip.app.platform.service.model.cards.CardEntity;
import com.lexgrip.app.platform.service.model.cards.CardMapper;
import com.lexgrip.app.platform.service.model.cards.CardRepository;
import com.lexgrip.app.platform.service.model.categories.CategoryEntity;
import com.lexgrip.app.platform.service.model.categories.CategoryRepository;
import com.lexgrip.app.platform.service.model.common.Colors;
import com.lexgrip.app.platform.service.model.dto.PagedResponse;
import com.lexgrip.app.platform.service.model.languages.LanguageEntity;
import com.lexgrip.app.platform.service.model.languages.LanguageRepository;
import com.lexgrip.app.platform.service.model.user.UserEntity;
import com.lexgrip.app.platform.service.model.user.UserRepository;
import com.lexgrip.common.api.model.ApiError;
import com.lexgrip.common.api.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

@Service
public class CardsService {
    private static final int MAX_GENERATED_CARDS_PER_REQUEST = 3;
    private static final String DEFAULT_GENERATED_CATEGORY_NAME = "Generated";
    private static final int MAX_AVOID_WORDS = 200;
    private static final Logger LOGGER = LoggerFactory.getLogger(CardsService.class);

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final LanguageRepository languageRepository;
    private final CategoryRepository categoryRepository;
    private final FlashcardAiService flashcardAiService;
    private final UserRepository userRepository;

    public CardsService(
            CardRepository cardRepository,
            CardMapper cardMapper,
            LanguageRepository languageRepository,
            CategoryRepository categoryRepository,
            FlashcardAiService flashcardAiService,
            UserRepository userRepository
    ){
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
        this.languageRepository = languageRepository;
        this.categoryRepository = categoryRepository;
        this.flashcardAiService = flashcardAiService;
        this.userRepository = userRepository;
    }

    public ApiResponse<PagedResponse<CardDTO>> getCards(UUID id, Pageable pageable, UserEntity user) {
        Page<CardDTO> cardsPage = cardRepository
                .findAllByLanguageIdAndUser(id, user, pageable)
                .map(cardMapper::toCardDTO);

        return ApiResponse.success(PagedResponse.from(cardsPage));
    }

    public ApiResponse<String> createCard(UUID languageId, UserEntity user, CardDTO cardDTO) {
        if (cardDTO == null) {
            return ApiResponse.error(new ApiError("400", "Request body is required", HttpStatus.BAD_REQUEST));
        }

        LanguageEntity language = languageRepository.findByIdAndUser(languageId, user);
        if (language == null) {
            return ApiResponse.error(new ApiError("404", "Language not found", HttpStatus.NOT_FOUND));
        }

        CategoryEntity category;
        try {
            category = resolveCategory(language, user, cardDTO);
        } catch (IllegalArgumentException ex) {
            LOGGER.warn(
                    "Category validation failed while creating card. userId={}, languageId={}, categoryId={}",
                    user.getId(),
                    languageId,
                    cardDTO.getCategory() != null ? cardDTO.getCategory().getId() : null,
                    ex
            );
            return ApiResponse.error(new ApiError("400", ex.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (SecurityException ex) {
            LOGGER.warn(
                    "Category access denied while creating card. userId={}, languageId={}, categoryId={}",
                    user.getId(),
                    languageId,
                    cardDTO.getCategory() != null ? cardDTO.getCategory().getId() : null,
                    ex
            );
            return ApiResponse.error(new ApiError("403", ex.getMessage(), HttpStatus.FORBIDDEN));
        } catch (IllegalStateException ex) {
            LOGGER.warn(
                    "Category not found while creating card. userId={}, languageId={}, categoryId={}",
                    user.getId(),
                    languageId,
                    cardDTO.getCategory() != null ? cardDTO.getCategory().getId() : null,
                    ex
            );
            return ApiResponse.error(new ApiError("404", ex.getMessage(), HttpStatus.NOT_FOUND));
        } catch (RuntimeException ex) {
            LOGGER.error(
                    "Unexpected error resolving category while creating card. userId={}, languageId={}",
                    user.getId(),
                    languageId,
                    ex
            );
            return ApiResponse.error(new ApiError("500", "Failed to resolve category", HttpStatus.INTERNAL_SERVER_ERROR));
        }

        String frontText = sanitize(cardDTO.getFrontText());
        if (frontText == null) {
            return ApiResponse.error(new ApiError("400", "Front text is required", HttpStatus.BAD_REQUEST));
        }
        if (frontText.length() > 500) {
            return ApiResponse.error(new ApiError("400", "Front text must be at most 500 characters", HttpStatus.BAD_REQUEST));
        }

        String backText = sanitize(cardDTO.getBackText());
        if (backText == null) {
            return ApiResponse.error(new ApiError("400", "Back text is required", HttpStatus.BAD_REQUEST));
        }

        String exampleText = sanitize(cardDTO.getExampleText());

        CardEntity cardEntity = new CardEntity();
        cardEntity.setUser(user);
        cardEntity.setLanguage(language);
        cardEntity.setCategory(category);
        cardEntity.setFrontText(frontText);
        cardEntity.setBackText(backText);
        cardEntity.setExampleText(exampleText);

        try {
            cardRepository.save(cardEntity);
        } catch (RuntimeException ex) {
            LOGGER.error(
                    "Failed to persist card. userId={}, languageId={}, categoryId={}",
                    user.getId(),
                    languageId,
                    category != null ? category.getId() : null,
                    ex
            );
            return ApiResponse.error(new ApiError("500", "Failed to create card", HttpStatus.INTERNAL_SERVER_ERROR));
        }

        return ApiResponse.success("success");
    }

    private String sanitize(String text) {
        if (text == null) {
            return null;
        }
        String trimmed = text.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private CategoryEntity resolveCategory(LanguageEntity language, UserEntity user, CardDTO cardDTO) {
        if (cardDTO.getCategory() == null) {
            return null;
        }

        UUID categoryId = cardDTO.getCategory().getId();
        String categoryName = sanitize(cardDTO.getCategory().getName());

        if (categoryId != null) {
            CategoryEntity category = categoryRepository.findById(categoryId).orElse(null);
            if (category != null) {
                validateCategoryAccess(language, user, category);
                return category;
            }
            if (categoryName == null) {
                throw new IllegalStateException("Category not found");
            }
            return findOrCreateCategoryByName(language, user, categoryName);
        }

        if (categoryName == null) {
            return null;
        }

        return findOrCreateCategoryByName(language, user, categoryName);
    }

    private void validateCategoryAccess(LanguageEntity language, UserEntity user, CategoryEntity category) {
        if (category.getUser() != null && !user.getId().equals(category.getUser().getId())) {
            throw new SecurityException("Category is not accessible for this user");
        }

        if (category.getLanguage() == null || !language.getId().equals(category.getLanguage().getId())) {
            throw new IllegalArgumentException("Category does not belong to this language");
        }
    }

    private CategoryEntity findOrCreateCategoryByName(LanguageEntity language, UserEntity user, String categoryName) {
        String normalizedCategoryName = normalizeCategoryName(categoryName);
        CategoryEntity existing = categoryRepository
                .findByLanguageAndNameIgnoreCase(language, normalizedCategoryName)
                .orElse(null);
        if (existing != null) {
            return existing;
        }

        try {
            return categoryRepository.save(
                    new CategoryEntity()
                            .setUser(user)
                            .setLanguage(language)
                            .setName(normalizedCategoryName)
                            .setColor(randomColor())
                            .setIsSystem(false)
            );
        } catch (DataIntegrityViolationException ex) {
            LOGGER.warn(
                    "Category create race detected, reusing existing category. languageId={}, categoryName={}",
                    language.getId(),
                    normalizedCategoryName,
                    ex
            );
            CategoryEntity fallback = categoryRepository
                    .findByLanguageAndNameIgnoreCase(language, normalizedCategoryName)
                    .orElse(null);
            if (fallback != null) {
                return fallback;
            }
            throw ex;
        }
    }

    private Colors randomColor() {
        Colors[] colors = Colors.values();
        return colors[ThreadLocalRandom.current().nextInt(colors.length)];
    }

    public ApiResponse<String> generateCards(GenerateRequest userMessage, UserEntity user, UUID languageId) {
        if (userMessage == null) {
            return ApiResponse.error(new ApiError("400", "Request body is required", HttpStatus.BAD_REQUEST));
        }

        String sanitizedUserMessage = sanitize(userMessage.getUserMessage());
        if (sanitizedUserMessage == null) {
            return ApiResponse.error(new ApiError("400", "User message is required", HttpStatus.BAD_REQUEST));
        }

        LanguageEntity language = languageRepository.findByIdAndUser(languageId, user);
        if (language == null) {
            return ApiResponse.error(new ApiError("404", "Language not found", HttpStatus.NOT_FOUND));
        }

        int remainingCards = getRemainingCards(user);
        if (remainingCards <= 0) {
            return ApiResponse.error(new ApiError("403", "Card generation limit reached", HttpStatus.FORBIDDEN));
        }

        int maxCardsToGenerate = Math.min(MAX_GENERATED_CARDS_PER_REQUEST, remainingCards);
        CategoryEntity requestedCategory = categoryRepository
                .findByLanguageAndNameIgnoreCase(language, sanitizedUserMessage)
                .orElse(null);
        Set<String> wordsToAvoid = requestedCategory == null
                ? Collections.emptySet()
                : collectExistingCategoryWords(language, user, requestedCategory);

        String languageName = language.getName();
        String prompt = buildGenerationPrompt(
                languageName,
                sanitizedUserMessage,
                maxCardsToGenerate,
                wordsToAvoid,
                requestedCategory != null
        );

        GeneratedCardResponse generated;
        try {
            generated = flashcardAiService.generateCard(prompt);
        } catch (RuntimeException ex) {
            LOGGER.error(
                    "AI generation failed. userId={}, languageId={}, requestedMaxCards={}",
                    user.getId(),
                    languageId,
                    maxCardsToGenerate,
                    ex
            );
            return ApiResponse.error(new ApiError("500", "Failed to generate cards", HttpStatus.INTERNAL_SERVER_ERROR));
        }

        if (generated == null || generated.getCards() == null || generated.getCards().isEmpty()) {
            return ApiResponse.error(new ApiError("500", "AI returned no cards", HttpStatus.INTERNAL_SERVER_ERROR));
        }

        CategoryEntity category = requestedCategory != null
                ? requestedCategory
                : findOrCreateCategoryByName(language, user, resolveGeneratedCategoryName(generated, sanitizedUserMessage));

        List<CardEntity> cardsToSave = new ArrayList<>();
        for (GeneratedCardResponse.GeneratedCard generatedCard : generated.getCards()) {
            if (cardsToSave.size() >= maxCardsToGenerate) {
                break;
            }

            if (generatedCard == null) {
                continue;
            }

            String frontText = sanitize(generatedCard.getFrontText());
            String backText = sanitize(generatedCard.getBackText());
            String exampleText = sanitize(generatedCard.getExampleText());

            if (frontText == null || backText == null) {
                continue;
            }

            if (!wordsToAvoid.isEmpty() && containsAnyWord(frontText, backText, exampleText, wordsToAvoid)) {
                continue;
            }

            if (frontText.length() > 500) {
                frontText = frontText.substring(0, 500);
            }

            CardEntity cardEntity = new CardEntity();
            cardEntity.setUser(user);
            cardEntity.setLanguage(language);
            cardEntity.setCategory(category);
            cardEntity.setFrontText(frontText);
            cardEntity.setBackText(backText);
            cardEntity.setExampleText(exampleText);
            cardsToSave.add(cardEntity);
        }

        if (cardsToSave.isEmpty()) {
            return ApiResponse.error(new ApiError("500", "AI returned no valid cards", HttpStatus.INTERNAL_SERVER_ERROR));
        }

        try {
            cardRepository.saveAll(cardsToSave);

            if (!wordsToAvoid.isEmpty()) {
                List<CardEntity> violatingCards = cardsToSave.stream()
                        .filter(card -> containsAnyWord(
                                card.getFrontText(),
                                card.getBackText(),
                                card.getExampleText(),
                                wordsToAvoid
                        ))
                        .toList();
                if (!violatingCards.isEmpty()) {
                    LOGGER.warn(
                            "Removing generated cards due to forbidden category words. userId={}, languageId={}, removedCount={}",
                            user.getId(),
                            languageId,
                            violatingCards.size()
                    );
                    cardRepository.deleteAll(violatingCards);
                    cardsToSave = cardsToSave.stream()
                            .filter(card -> !violatingCards.contains(card))
                            .toList();
                }
            }

            if (cardsToSave.isEmpty()) {
                return ApiResponse.error(new ApiError("500", "Generated cards violated category word rules", HttpStatus.INTERNAL_SERVER_ERROR));
            }

            incrementCardsUsed(user, cardsToSave.size());
            userRepository.save(user);
        } catch (RuntimeException ex) {
            LOGGER.error(
                    "Failed saving generated cards or updating usage. userId={}, languageId={}, cardsAttempted={}",
                    user.getId(),
                    languageId,
                    cardsToSave.size(),
                    ex
            );
            return ApiResponse.error(new ApiError("500", "Failed to save generated cards", HttpStatus.INTERNAL_SERVER_ERROR));
        }

        return ApiResponse.success("success");
    }

    private String buildGenerationPrompt(
            String languageName,
            String userMessage,
            int maxCardsToGenerate,
            Set<String> wordsToAvoid,
            boolean userInputMatchedCategory
    ) {
        String avoidanceSection = "";
        if (userInputMatchedCategory && !wordsToAvoid.isEmpty()) {
            avoidanceSection = """

                    The user's request matches an existing category.
                    Avoid reusing these existing words from that category:
                    %s
                    """.formatted(String.join(", ", wordsToAvoid));
        }

        return """
                Language: %s
                User request: %s

                The user request can be either:
                - a category name, or
                - a description of what flashcards they want.

                Generate up to %d cards.
                Include categoryName on every card.
                Use the same categoryName for all returned cards in this response.
                If you cannot generate %d good cards, return fewer cards and stop.
                %s
                """.formatted(languageName, userMessage, maxCardsToGenerate, maxCardsToGenerate, avoidanceSection);
    }

    private String resolveGeneratedCategoryName(GeneratedCardResponse generated, String userMessage) {
        for (GeneratedCardResponse.GeneratedCard generatedCard : generated.getCards()) {
            if (generatedCard == null) {
                continue;
            }
            String categoryName = sanitize(generatedCard.getCategoryName());
            if (categoryName != null) {
                return categoryName;
            }
        }
        String fallback = sanitize(userMessage);
        return fallback != null ? fallback : DEFAULT_GENERATED_CATEGORY_NAME;
    }

    private String normalizeCategoryName(String categoryName) {
        String sanitized = sanitize(categoryName);
        if (sanitized == null) {
            return DEFAULT_GENERATED_CATEGORY_NAME;
        }
        sanitized = sanitized.replaceAll("\\s+", " ");
        if (sanitized.length() > 120) {
            return sanitized.substring(0, 120);
        }
        return sanitized;
    }

    private int getRemainingCards(UserEntity user) {
        int cardLimit = user.getCardLimit() == null ? 0 : Math.max(user.getCardLimit(), 0);
        int cardsUsed = user.getCardsUsed() == null ? 0 : Math.max(user.getCardsUsed(), 0);
        return Math.max(cardLimit - cardsUsed, 0);
    }

    private void incrementCardsUsed(UserEntity user, int generatedCount) {
        int currentCardsUsed = user.getCardsUsed() == null ? 0 : Math.max(user.getCardsUsed(), 0);
        user.setCardsUsed(currentCardsUsed + Math.max(generatedCount, 0));
    }

    private Set<String> collectExistingCategoryWords(LanguageEntity language, UserEntity user, CategoryEntity category) {
        if (category.getId() == null) {
            return Collections.emptySet();
        }

        List<CardEntity> existingCards = cardRepository.findAllByLanguageIdAndUserAndCategoryId(
                language.getId(),
                user,
                category.getId()
        );
        if (existingCards.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> words = new LinkedHashSet<>();
        for (CardEntity card : existingCards) {
            addWords(words, card.getFrontText());
            addWords(words, card.getBackText());
            addWords(words, card.getExampleText());
            if (words.size() >= MAX_AVOID_WORDS) {
                break;
            }
        }

        return words;
    }

    private void addWords(Set<String> target, String text) {
        String sanitized = sanitize(text);
        if (sanitized == null) {
            return;
        }

        String[] rawWords = sanitized.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}]+");
        for (String rawWord : rawWords) {
            if (rawWord.isBlank()) {
                continue;
            }
            target.add(rawWord);
            if (target.size() >= MAX_AVOID_WORDS) {
                return;
            }
        }
    }

    private boolean containsAnyWord(String frontText, String backText, String exampleText, Set<String> wordsToAvoid) {
        if (wordsToAvoid.isEmpty()) {
            return false;
        }

        Set<String> generatedWords = new LinkedHashSet<>();
        addWords(generatedWords, frontText);
        addWords(generatedWords, backText);
        addWords(generatedWords, exampleText);

        for (String word : generatedWords) {
            if (wordsToAvoid.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
