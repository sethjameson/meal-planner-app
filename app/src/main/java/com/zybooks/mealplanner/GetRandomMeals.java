package com.zybooks.mealplanner;

import java.util.List;

public class GetRandomMeals {
    private int id;
    private String title;
    private String image;
    private String instructions;
    private List<String> ingredients; // Each string represents an ingredient
    private List<GetRandomMeals> recipes; // To store a list of recipes for a multi-meal response

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<GetRandomMeals> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<GetRandomMeals> recipes) {
        this.recipes = recipes;
    }
}
