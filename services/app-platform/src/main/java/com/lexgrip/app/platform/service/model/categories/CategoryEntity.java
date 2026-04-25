package com.lexgrip.app.platform.service.model.categories;

import com.lexgrip.app.platform.service.model.cards.CardEntity;
import com.lexgrip.app.platform.service.model.common.Colors;
import com.lexgrip.app.platform.service.model.languages.LanguageEntity;
import com.lexgrip.app.platform.service.model.user.UserEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = @UniqueConstraint(name = "uq_categories_language_name", columnNames = {"language_id", "name"})
)
public class CategoryEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "language_id", nullable = true)
    private LanguageEntity language;


    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "color", length = 100)
    @Enumerated(EnumType.STRING)
    private Colors color;

    @Column(name = "system", nullable = false)
    private boolean isSystem;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = false)
    @OrderBy("sortOrder ASC, id ASC")
    private List<CardEntity> cards = new ArrayList<>();

    public CategoryEntity() {
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public CategoryEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public LanguageEntity getLanguage() {
        return language;
    }

    public CategoryEntity setLanguage(LanguageEntity language) {
        this.language = language;
        return this;
    }

    public String getName() {
        return name;
    }

    public CategoryEntity setName(String name) {
        this.name = name;
        return this;
    }

    public Colors getColor() {
        return color;
    }

    public CategoryEntity setColor(Colors color) {
        this.color = color;
        return this;
    }

    public boolean isSystem(){
        return isSystem;
    }

    public CategoryEntity setIsSystem(boolean isSystem){
        this.isSystem = isSystem;
        return this;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<CardEntity> getCards() {
        return cards;
    }

    public CategoryEntity setCards(List<CardEntity> cards) {
        this.cards = cards;
        return this;
    }
}
