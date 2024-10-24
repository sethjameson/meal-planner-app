package com.zybooks.mealplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RandomMeals extends AppCompatActivity {

    private LinearLayout imageLayout;
    private EditText mealCountEditText;
    private Button getMealsButton;
    private Button main_activity_button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_meals);

        imageLayout = findViewById(R.id.imageLayout); // The LinearLayout where we'll add images and titles
        mealCountEditText = findViewById(R.id.mealCount);
        getMealsButton = findViewById(R.id.getMealsButton);
        main_activity_button = findViewById(R.id.main_button);

        main_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RandomMeals.this, MainActivity.class);
                startActivity(intent);
            }
        });

        getMealsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mealCountString = mealCountEditText.getText().toString();
                if (!mealCountString.isEmpty()){
                    int mealCount = Integer.parseInt(mealCountString);
                    getRandomMeals(mealCount);
                } else {
                    Toast.makeText(RandomMeals.this, "Please enter a number of meals", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getRandomMeals(int number) {
        SpoonacularApiService apiService = ApiClient.getClient().create(SpoonacularApiService.class);
        Call<RecipeResponse> call = apiService.getRandomMeals(number);

        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    imageLayout.removeAllViews(); // Clear any existing views

                    for (GetRandomMeals recipe : response.body().getRecipes()) {
                        if (recipe == null) continue; // Skip null recipes

                        // Create a new LinearLayout to hold each title, image, button, and ingredients
                        LinearLayout recipeLayout = new LinearLayout(RandomMeals.this);
                        recipeLayout.setOrientation(LinearLayout.VERTICAL);
                        recipeLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        recipeLayout.setPadding(16, 16, 16, 16);
                        recipeLayout.setGravity(Gravity.CENTER_HORIZONTAL);

                        // Create TextView for the recipe title
                        TextView recipeTitleTextView = new TextView(RandomMeals.this);
                        recipeTitleTextView.setText(recipe.getTitle());
                        recipeTitleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        recipeTitleTextView.setPadding(16, 0, 16, 0);
                        recipeTitleTextView.setGravity(Gravity.CENTER);
                        recipeTitleTextView.setTypeface(null, Typeface.BOLD);
                        recipeTitleTextView.setTextSize(18);

                        // Create a View to act as a divider (line)
                        View dividerView = new View(RandomMeals.this);
                        dividerView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                2));
                        dividerView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

                        // Create ImageView for the recipe image
                        ImageView recipeImageView = new ImageView(RandomMeals.this);
                        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                                750, 750);
                        imageLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                        recipeImageView.setLayoutParams(imageLayoutParams);
                        recipeImageView.setPadding(16, 16, 16, 16);
                        Picasso.get().load(recipe.getImage()).into(recipeImageView);

                        // Create Button for Ingredients toggle
                        Button showIngredientsButton = new Button(RandomMeals.this);
                        showIngredientsButton.setText("Show Ingredients");
                        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        buttonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                        showIngredientsButton.setLayoutParams(buttonLayoutParams);

                        // Create TextView for Ingredients (initially hidden)
                        TextView ingredientsTextView = new TextView(RandomMeals.this);
                        ingredientsTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        ingredientsTextView.setVisibility(View.GONE); // Hide ingredients initially

                        // Toggle ingredients visibility and fetch detailed ingredients on button click
                        showIngredientsButton.setOnClickListener(new View.OnClickListener() {
                            boolean isVisible = false;

                            @Override
                            public void onClick(View v) {
                                if (isVisible) {
                                    ingredientsTextView.setVisibility(View.GONE);
                                    showIngredientsButton.setText("Show Ingredients");
                                } else {
                                    ingredientsTextView.setVisibility(View.VISIBLE);
                                    showIngredientsButton.setText("Hide Ingredients");

                                    // Fetch detailed recipe information
                                    getRecipeDetails(recipe.getId(), ingredientsTextView);
                                }
                                isVisible = !isVisible;
                            }
                        });

                        // Add the views to the layout
                        recipeLayout.addView(recipeTitleTextView);
                        recipeLayout.addView(dividerView);
                        recipeLayout.addView(recipeImageView);
                        recipeLayout.addView(showIngredientsButton);
                        recipeLayout.addView(ingredientsTextView);

                        imageLayout.addView(recipeLayout);
                    }
                } else {
                    Log.e("API Error", "Error in API Response: " + response.message());
                }
            }


            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Log.e("API Failure", t.getMessage());
            }
        });
    }

    private void getRecipeDetails(int recipeId, final TextView ingredientsTextView) {
        SpoonacularApiService apiService = ApiClient.getClient().create(SpoonacularApiService.class);
        Call<RecipeDetailsReponse> call = apiService.getRecipeDetails(recipeId);

        call.enqueue(new Callback<RecipeDetailsReponse>() {
            @Override
            public void onResponse(Call<RecipeDetailsReponse> call, Response<RecipeDetailsReponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Populate the ingredientsTextView
                    StringBuilder ingredientsBuilder = new StringBuilder();
                    for (Ingredient ingredient : response.body().getIngredients()) {
                        ingredientsBuilder.append("- ").append(ingredient.getName()).append("\n");
                    }
                    ingredientsTextView.setText(ingredientsBuilder.toString());
                } else {
                    Log.e("API Error", "Error fetching recipe details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RecipeDetailsReponse> call, Throwable t) {
                Log.e("API Failure", "Failed to fetch recipe details: " + t.getMessage());
            }
        });
    }

}




