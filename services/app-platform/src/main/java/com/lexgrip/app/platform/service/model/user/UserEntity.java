package com.lexgrip.app.platform.service.model.user;


import com.lexgrip.app.platform.service.model.cards.CardEntity;
import com.lexgrip.app.platform.service.model.categories.CategoryEntity;
import com.lexgrip.app.platform.service.model.feedback.FeedbackEntity;
import com.lexgrip.app.platform.service.model.languages.LanguageEntity;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uq_users_email", columnNames = "email")
        }
)
public class UserEntity implements Persistable<UUID> {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Transient
    private boolean isNew = true;

    @Column(name = "username", nullable = false, length = 64)
    private String username;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "marketing_consent", nullable = false)
    private boolean marketingConsent = false;

    @Column(name = "card_limit", nullable = false)
    private Integer cardLimit = 10;

    @Column(name = "cards_used", nullable = false)
    private Integer cardsUsed = 0;

    @Column(name = "input_tokens", nullable = false)
    private Long inputTokens = 0L;

    @Column(name = "output_tokens", nullable = false)
    private Long outputTokens = 0L;

    @Column(name = "total_tokens", nullable = false)
    private Long totalTokens = 0L;

    @Column(name = "first_generate_request_at", nullable = true)
    private OffsetDateTime firstGenerateRequestAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<CategoryEntity> categories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<LanguageEntity> languages = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<CardEntity> cards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<FeedbackEntity> feedbackEntries = new ArrayList<>();

    public UserEntity() {
    }

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UserEntity setFirstGenerateRequestAt(OffsetDateTime firstGenerateRequestAt){
        this.firstGenerateRequestAt = firstGenerateRequestAt;
        return this;
    }

    public OffsetDateTime getFirstGenerateRequestAt(){
        return firstGenerateRequestAt;
    }

    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isMarketingConsent() {
        return marketingConsent;
    }

    public void setMarketingConsent(boolean marketingConsent) {
        this.marketingConsent = marketingConsent;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public Integer getCardLimit() {
        return cardLimit;
    }

    public void setCardLimit(Integer cardLimit) {
        this.cardLimit = cardLimit;
    }

    public Integer getCardsUsed() {
        return cardsUsed;
    }

    public void setCardsUsed(Integer cardsUsed) {
        this.cardsUsed = cardsUsed;
    }

    public Long getInputTokens() {
        return inputTokens;
    }

    public void setInputTokens(Long inputTokens) {
        this.inputTokens = inputTokens;
    }

    public Long getOutputTokens() {
        return outputTokens;
    }

    public void setOutputTokens(Long outputTokens) {
        this.outputTokens = outputTokens;
    }

    public Long getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(Long totalTokens) {
        this.totalTokens = totalTokens;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    public void markNotNew() {
        this.isNew = false;
    }

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
    }

    public List<LanguageEntity> getLanguages() {
        return languages;
    }

    public void setLanguages(List<LanguageEntity> languages) {
        this.languages = languages;
    }

    public List<CardEntity> getCards() {
        return cards;
    }

    public void setCards(List<CardEntity> cards) {
        this.cards = cards;
    }

    public List<FeedbackEntity> getFeedbackEntries() {
        return feedbackEntries;
    }

    public void setFeedbackEntries(List<FeedbackEntity> feedbackEntries) {
        this.feedbackEntries = feedbackEntries;
    }
}
