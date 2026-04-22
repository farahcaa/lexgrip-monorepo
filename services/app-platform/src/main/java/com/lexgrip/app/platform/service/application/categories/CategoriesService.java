package com.lexgrip.app.platform.service.application.categories;

import com.lexgrip.app.platform.service.model.categories.CategoriesDTO;
import com.lexgrip.app.platform.service.model.categories.CategoryEntity;
import com.lexgrip.app.platform.service.model.categories.CategoryMapper;
import com.lexgrip.app.platform.service.model.categories.CategoryRepository;
import com.lexgrip.app.platform.service.model.user.UserEntity;
import com.lexgrip.common.api.model.ApiResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    public CategoriesService(CategoryMapper categoryMapper,CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }
    public ApiResponse<List<CategoriesDTO>> getCategories(UserEntity user) {
        List<CategoryEntity> categoryEntityList = categoryRepository.findByUserOrIsSystemTrue(user);
        return new ApiResponse<>(true,categoryMapper.toCategoriesDTO(categoryEntityList), null);
    }
}
