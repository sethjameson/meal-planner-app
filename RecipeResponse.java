package com.zybooks.mealplanner;

import java.util.List;

public class RecipeResponse {
    private List<GetRandomMeals> recipes;

    public List<GetRandomMeals> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<GetRandomMeals> recipes) {
        this.recipes = recipes;
    }
}

