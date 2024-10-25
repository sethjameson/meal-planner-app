package com.zybooks.mealplanner;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpoonacularApiService {
    @GET("recipes/random")
    Call<RecipeResponse> getRandomMeals(@Query("number") int number);

    // Add a new method to fetch detailed recipe information by ID
    @GET("recipes/{id}/information")
    Call<RecipeDetailsResponse> getRecipeDetails(@Path("id") int recipeId);
}


