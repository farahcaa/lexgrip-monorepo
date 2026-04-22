package com.lexgrip.app.platform.service.model.categories;

public class CategoriesDTO {

    private String name;
    private String color;

    public String getName(){
        return name;
    }

    public CategoriesDTO setName(String name){
        this.name = name;
        return this;
    }

    public String getColor(){
        return color;
    }

    public CategoriesDTO setColor(String color){
        this.color = color;
        return this;
    }
}
