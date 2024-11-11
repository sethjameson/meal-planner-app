package com.zybooks.mealplanner;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpoonacularApiService {
    @GET("recipes/random")
    Call<GetRandomMeals> getRandomMeals(@Query("number") int number);

    @GET("recipes/{id}/information")
    Call<GetRandomMeals> getRecipeDetails(@Path("id") int recipeId);
}