package com.zybooks.mealplanner;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SpoonacularApiService {
    @GET("recipes/random")
    Call<RecipeResponse> getRandomMeals(
            @Query("number") int number
    );
}

