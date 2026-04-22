package com.lexgrip.app.platform.service.application.categories;

import com.lexgrip.app.platform.service.model.categories.CategoriesDTO;
import com.lexgrip.app.platform.service.model.user.UserEntity;
import com.lexgrip.app.platform.service.resolver.CurrentUserEntity;
import com.lexgrip.common.api.model.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("app/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;

    public CategoriesController(CategoriesService categoriesService){
        this.categoriesService = categoriesService;
    }

    @GetMapping()
    public ApiResponse<List<CategoriesDTO>> getCategories(@CurrentUserEntity UserEntity user){
        return categoriesService.getCategories(user);
    }
}
