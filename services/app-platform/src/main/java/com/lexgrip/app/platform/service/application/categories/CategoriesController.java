package com.lexgrip.app.platform.service.application.categories;

import com.lexgrip.app.platform.service.model.categories.CategoriesDTO;
import com.lexgrip.app.platform.service.model.user.UserEntity;
import com.lexgrip.app.platform.service.resolver.CurrentUserEntity;
import com.lexgrip.common.api.model.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("app/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;

    public CategoriesController(CategoriesService categoriesService){
        this.categoriesService = categoriesService;
    }

    @GetMapping("/{languageId}")
    public ApiResponse<List<CategoriesDTO>> getCategories(@PathVariable UUID languageId, @CurrentUserEntity UserEntity user){
        return categoriesService.getCategoriesByLanguage(user, languageId);
    }
}
