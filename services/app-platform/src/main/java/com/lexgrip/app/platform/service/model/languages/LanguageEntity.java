package com.lexgrip.app.platform.service.model.languages;

import com.lexgrip.app.platform.service.model.categories.CategoryEntity;
import com.lexgrip.app.platform.service.model.cards.CardEntity;
import com.lexgrip.app.platform.service.model.common.Colors;
import com.lexgrip.app.platform.service.model.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "languages",
        uniqueConstraints = @UniqueConstraint(name = "uq_languages_user_name", columnNames = {"user_id", "name"})
)
public class LanguageEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "color", nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private Colors color;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<CategoryEntity> categories = new ArrayList<>();

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<CardEntity> cards = new ArrayList<>();

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

    public LanguageEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public Colors getColor(){
        return color;
    }

    public LanguageEntity setColor(Colors color){
        this.color = color;
        return this;
    }

    public String getName() {
        return name;
    }

    public LanguageEntity setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public LanguageEntity setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
        return this;
    }

    public List<CardEntity> getCards() {
        return cards;
    }

    public LanguageEntity setCards(List<CardEntity> cards) {
        this.cards = cards;
        return this;
    }
}
