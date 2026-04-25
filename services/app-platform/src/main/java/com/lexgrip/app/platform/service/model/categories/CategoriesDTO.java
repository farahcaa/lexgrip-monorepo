package com.lexgrip.app.platform.service.model.categories;

import com.lexgrip.app.platform.service.model.common.Colors;

import java.util.UUID;

public class CategoriesDTO {
    private UUID id;
    private String name;
    private Colors color;

    public String getName(){
        return name;
    }

    public CategoriesDTO setName(String name){
        this.name = name;
        return this;
    }

    public UUID getId(){
        return id;
    }

    public CategoriesDTO setId(UUID id){
        this.id = id;
        return this;
    }

    public Colors getColor(){
        return color;
    }

    public CategoriesDTO setColor(Colors color){
        this.color = color;
        return this;
    }


}
