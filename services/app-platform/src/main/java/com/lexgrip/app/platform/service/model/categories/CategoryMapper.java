package com.lexgrip.app.platform.service.model.categories;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    List<CategoriesDTO> toCategoriesDTO(List<CategoryEntity> categoryEntity);
}
