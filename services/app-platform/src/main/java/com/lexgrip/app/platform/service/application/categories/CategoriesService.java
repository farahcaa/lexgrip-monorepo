package com.lexgrip.app.platform.service.application.categories;

import com.lexgrip.app.platform.service.model.categories.CategoriesDTO;
import com.lexgrip.app.platform.service.model.categories.CategoryEntity;
import com.lexgrip.app.platform.service.model.categories.CategoryMapper;
import com.lexgrip.app.platform.service.model.categories.CategoryRepository;
import com.lexgrip.app.platform.service.model.user.UserEntity;
import com.lexgrip.common.api.model.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class CategoriesService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    public CategoriesService(CategoryMapper categoryMapper,CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }
    public ApiResponse<List<CategoriesDTO>> getCategoriesByLanguage(UserEntity user, UUID languageId
    ) {
        List<CategoryEntity> categoryEntityList = categoryRepository.findByUserAndLanguageIdOrIsSystemTrue(user,languageId);
        categoryEntityList.sort(
                Comparator
                        .comparing((CategoryEntity category) -> category.getUser() == null || !user.getId().equals(category.getUser().getId()))
                        .thenComparing(CategoryEntity::getName, String.CASE_INSENSITIVE_ORDER)
        );
        return new ApiResponse<>(true,categoryMapper.toCategoriesDTO(categoryEntityList), null);
    }
}
