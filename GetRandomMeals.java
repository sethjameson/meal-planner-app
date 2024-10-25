package com.zybooks.mealplanner;

import java.util.ArrayList;
import java.util.List;

public class GetRandomMeals {
    private int id;
    private String title;
    private String image;
    private String instructions;
    private List<Ingredient> extendedIngredients; // Nested structure for ingredients
    private List<GetRandomMeals> recipes; // For storing multiple recipes in a response

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getInstructions() {
        return instructions;
    }

    // Convert extendedIngredients to a list of strings
    public List<String> getIngredients() {
        List<String> ingredientNames = new ArrayList<>();
        if (extendedIngredients != null) {
            for (Ingredient ingredient : extendedIngredients) {
                ingredientNames.add(ingredient.getName());
            }
        }
        return ingredientNames;
    }

    public List<GetRandomMeals> getRecipes() {
        return recipes;
    }

    // Nested class to represent each ingredient with additional fields if needed
    public static class Ingredient {
        private String name;

        public String getName() {
            return name;
        }
    }
}
