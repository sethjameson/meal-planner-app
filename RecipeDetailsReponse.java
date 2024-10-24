package com.zybooks.mealplanner;

import java.util.List;

public class RecipeDetailsReponse {
    private List<Ingredient> extendedIngredients;

    public List<Ingredient> getIngredients() {
        return extendedIngredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.extendedIngredients = ingredients;
    }
}

