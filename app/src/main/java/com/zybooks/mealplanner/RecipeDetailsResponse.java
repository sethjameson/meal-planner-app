package com.zybooks.mealplanner;

import java.util.List;

public class RecipeDetailsResponse {
    private List<Ingredient> extendedIngredients;
    private String instructions; // Add instructions for the recipe

    public List<Ingredient> getIngredients() {
        return extendedIngredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.extendedIngredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}


