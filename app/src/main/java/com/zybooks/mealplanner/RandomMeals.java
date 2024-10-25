package com.zybooks.mealplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
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

    private LinearLayout food_images;
    private EditText user_num_meals;
    private EditText user_budget;
    private Button get_random_meals;
    private Button main_activity_button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_meals);

        food_images = findViewById(R.id.output);
        user_num_meals = findViewById(R.id.user_num_meals);
        user_budget = findViewById(R.id.user_budget);
        get_random_meals = findViewById(R.id.get_meals);
        main_activity_button = findViewById(R.id.main_button);

        main_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RandomMeals.this, MainActivity.class);
                startActivity(intent);
            }
        });

        get_random_meals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mealCountString = user_num_meals.getText().toString();
                String priceLimitString = user_budget.getText().toString(); // Get price limit

                if (!mealCountString.isEmpty() && !priceLimitString.isEmpty()){
                    int mealCount = Integer.parseInt(mealCountString);
                    double priceLimit = Double.parseDouble(priceLimitString); // Convert to double
                    getRandomMeals(mealCount, priceLimit); // Pass price limit to method
                } else {
                    Toast.makeText(RandomMeals.this, "Please enter both meal count and price limit", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getRecipeDetails(int recipeId, final TextView ingredientsTextView, final TextView instructionsTextView) {
        SpoonacularApiService apiService = ApiClient.getClient().create(SpoonacularApiService.class);
        Call<GetRandomMeals> call = apiService.getRecipeDetails(recipeId);

        call.enqueue(new Callback<GetRandomMeals>() {
            @Override
            public void onResponse(Call<GetRandomMeals> call, Response<GetRandomMeals> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetRandomMeals recipe = response.body();

                    // Display recipe title
                    ingredientsTextView.setText("- " + recipe.getTitle() + "\n");

                    // Display ingredients if available
                    StringBuilder ingredientsBuilder = new StringBuilder();
                    if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                        for (String ingredient : recipe.getIngredients()) {
                            ingredientsBuilder.append("- ").append(ingredient).append("\n");
                        }
                    } else {
                        ingredientsBuilder.append("No ingredients available.");
                    }
                    ingredientsTextView.setText(ingredientsBuilder.toString());

                    // Display instructions
                    String instructions = recipe.getInstructions();
                    if (instructions != null && !instructions.isEmpty()) {
                        instructionsTextView.setText(Html.fromHtml(instructions));
                    } else {
                        instructionsTextView.setText("No instructions available.");
                    }
                } else {
                    Log.e("API Error", "Error fetching recipe details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GetRandomMeals> call, Throwable t) {
                Log.e("API Failure", "Failed to fetch recipe details: " + t.getMessage());
            }
        });
    }




    private void getRandomMeals(int number, final double priceLimit) {
        SpoonacularApiService apiService = ApiClient.getClient().create(SpoonacularApiService.class);
        Call<GetRandomMeals> call = apiService.getRandomMeals(number);

        call.enqueue(new Callback<GetRandomMeals>() {
            @Override
            public void onResponse(Call<GetRandomMeals> call, Response<GetRandomMeals> response) {
                if (response.isSuccessful() && response.body() != null) {
                    food_images.removeAllViews(); // Clear any existing views

                    List<GetRandomMeals> recipes = response.body().getRecipes(); // Get the list of recipes
                    if (recipes != null) {
                        for (GetRandomMeals recipe : recipes) {
                            if (recipe == null) continue; // Skip null recipes

                            // Simulate price calculation based on ingredients or other factors
                            double estimatedPrice = calculateMealPrice(recipe); // Simulate a price
                            if (estimatedPrice > priceLimit) {
                                continue; // Skip recipes that exceed the price limit
                            }

                            // Create a new LinearLayout to hold each title, image, buttons, and ingredients
                            LinearLayout recipeLayout = new LinearLayout(RandomMeals.this);
                            recipeLayout.setOrientation(LinearLayout.VERTICAL);
                            recipeLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            recipeLayout.setPadding(16, 16, 16, 16);
                            recipeLayout.setGravity(Gravity.CENTER_HORIZONTAL);

                            // Create TextView for the recipe title
                            TextView recipeTitleTextView = new TextView(RandomMeals.this);
                            recipeTitleTextView.setText(recipe.getTitle() + " ($" + String.format("%.2f", estimatedPrice) + ")");
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
                            buttonLayoutParams.setMargins(0, 16, 0, 16); // Add margin between buttons
                            showIngredientsButton.setLayoutParams(buttonLayoutParams);

                            // Create Button for Recipe toggle
                            Button showRecipeButton = new Button(RandomMeals.this);
                            showRecipeButton.setText("Show Recipe");
                            showRecipeButton.setLayoutParams(buttonLayoutParams);

                            // Create TextView for Ingredients (initially hidden)
                            TextView ingredientsTextView = new TextView(RandomMeals.this);
                            ingredientsTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            ingredientsTextView.setVisibility(View.GONE); // Hide ingredients initially

                            // Create TextView for Recipe Instructions (initially hidden)
                            TextView instructionsTextView = new TextView(RandomMeals.this);
                            instructionsTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            instructionsTextView.setVisibility(View.GONE); // Hide recipe initially

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
                                        getRecipeDetails(recipe.getId(), ingredientsTextView, instructionsTextView);
                                    }
                                    isVisible = !isVisible;
                                }
                            });

                            // Toggle recipe instructions visibility
                            showRecipeButton.setOnClickListener(new View.OnClickListener() {
                                boolean isVisible = false;

                                @Override
                                public void onClick(View v) {
                                    if (isVisible) {
                                        instructionsTextView.setVisibility(View.GONE);
                                        showRecipeButton.setText("Show Recipe");
                                    } else {
                                        instructionsTextView.setVisibility(View.VISIBLE);
                                        showRecipeButton.setText("Hide Recipe");

                                        // Fetch the instructions (if not already fetched)
                                        getRecipeDetails(recipe.getId(), ingredientsTextView, instructionsTextView);
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
                            recipeLayout.addView(showRecipeButton); // Add the "Show Recipe" button
                            recipeLayout.addView(instructionsTextView); // Add the recipe instructions TextView

                            food_images.addView(recipeLayout);
                        }
                    } else {
                        Log.e("API Error", "No recipes found in API response.");
                    }
                } else {
                    Log.e("API Error", "Error in API Response: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GetRandomMeals> call, Throwable t) {
                Log.e("API Failure", t.getMessage());
            }
        });
    }


    // Simulate a method to calculate meal price
    private double calculateMealPrice(GetRandomMeals recipe) {
        // Here, you can simulate a price calculation based on the recipe
        // For example, a random price generator can be used for now
        return Math.random() * 30; // Generate a random price between $0 and $30
    }
}
