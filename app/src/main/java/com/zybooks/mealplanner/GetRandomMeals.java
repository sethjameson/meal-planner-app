package com.zybooks.mealplanner;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetRandomMeals {
    private int id;
    private String title;
    private String image;
    private String instructions;
    private List<Ingredient> extendedIngredients;
    private List<GetRandomMeals> recipes;

    @SerializedName("pricePerServing")
    private double price;

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

    public double getPrice() {
        return price;
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