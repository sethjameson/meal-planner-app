package com.zybooks.mealplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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
    private Button help_button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_meals);

        // Initialize bottom navigation and handle item selection
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.randomMeals);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            @SuppressLint("NonConstantResourceId")
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.randomMeals) {
                    return true;
                } else if (itemId == R.id.help) {
                    startActivity(new Intent(getApplicationContext(), help_activity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

        // Link XML elements to code variables
        food_images = findViewById(R.id.output);
        user_num_meals = findViewById(R.id.user_num_meals);
        user_budget = findViewById(R.id.user_budget);
        get_random_meals = findViewById(R.id.get_meals);

        // Handle button click to fetch random meals
        get_random_meals.setOnClickListener(view -> {
            String mealCountString = user_num_meals.getText().toString();
            String priceLimitString = user_budget.getText().toString();

            if (!mealCountString.isEmpty() && !priceLimitString.isEmpty()) {
                int mealCount = Integer.parseInt(mealCountString);
                double priceLimit = Double.parseDouble(priceLimitString);
                getRandomMeals(mealCount, priceLimit);
            } else {
                Toast.makeText(RandomMeals.this, "Please enter both meal count and price limit", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to start fetching random meals using the API
    private void getRandomMeals(int desiredMealCount, final double priceLimit) {
        SpoonacularApiService apiService = ApiClient.getClient().create(SpoonacularApiService.class);
        fetchMeals(apiService, desiredMealCount, priceLimit, new ArrayList<>());
    }

    // Recursively fetch meals until the desired number is met and within the budget
    private void fetchMeals(SpoonacularApiService apiService, final int remainingCount, final double priceLimit, final List<GetRandomMeals> validMeals) {
        int overFetchCount = Math.max(remainingCount * 2, 10);
        Call<GetRandomMeals> call = apiService.getRandomMeals(overFetchCount);

        call.enqueue(new Callback<GetRandomMeals>() {
            @Override
            public void onResponse(Call<GetRandomMeals> call, Response<GetRandomMeals> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GetRandomMeals> recipes = response.body().getRecipes();
                    if (recipes != null) {
                        for (GetRandomMeals recipe : recipes) {
                            if (recipe == null) continue;

                            double estimatedPrice = recipe.getPrice() / 10;
                            if (estimatedPrice <= priceLimit) {
                                validMeals.add(recipe);
                                if (validMeals.size() >= remainingCount) break;
                            }
                        }

                        if (validMeals.size() >= remainingCount) {
                            displayMeals(validMeals.subList(0, remainingCount), priceLimit);
                        } else {
                            fetchMeals(apiService, remainingCount - validMeals.size(), priceLimit, validMeals);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GetRandomMeals> call, Throwable t) {
                Toast.makeText(RandomMeals.this, "Failed to fetch meals. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Display the fetched meals in the UI
    private void displayMeals(List<GetRandomMeals> meals, double priceLimit) {
        food_images.removeAllViews();

        for (GetRandomMeals recipe : meals) {
            if (recipe == null) continue;

            double estimatedPrice = recipe.getPrice() / 10;

            LinearLayout recipeLayout = new LinearLayout(RandomMeals.this);
            recipeLayout.setOrientation(LinearLayout.VERTICAL);
            recipeLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            recipeLayout.setPadding(16, 16, 16, 16);
            recipeLayout.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView recipeTitleTextView = new TextView(RandomMeals.this);
            recipeTitleTextView.setText(recipe.getTitle() + " ($" + String.format("%.2f", estimatedPrice) + ")");
            recipeTitleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            recipeTitleTextView.setPadding(16, 0, 16, 0);
            recipeTitleTextView.setGravity(Gravity.CENTER);
            recipeTitleTextView.setTypeface(null, Typeface.BOLD);
            recipeTitleTextView.setTextSize(18);

            View dividerView = new View(RandomMeals.this);
            dividerView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2));
            dividerView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

            ImageView recipeImageView = new ImageView(RandomMeals.this);
            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            imageLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            recipeImageView.setLayoutParams(imageLayoutParams);
            recipeImageView.setPadding(16, 16, 16, 16);
            Picasso.get().load(recipe.getImage()).into(recipeImageView);

            TextView ingredientsTextView = new TextView(RandomMeals.this);
            ingredientsTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            ingredientsTextView.setVisibility(View.GONE);

            Button showIngredientsButton = new Button(RandomMeals.this);
            showIngredientsButton.setText("Show Ingredients");
            showIngredientsButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
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
                        getRecipeDetails(recipe.getId(), ingredientsTextView, null);
                    }
                    isVisible = !isVisible;
                }
            });

            TextView instructionsTextView = new TextView(RandomMeals.this);
            instructionsTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            instructionsTextView.setVisibility(View.GONE);

            Button showRecipeButton = new Button(RandomMeals.this);
            showRecipeButton.setText("Show Recipe");
            showRecipeButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
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
                        getRecipeDetails(recipe.getId(), null, instructionsTextView);
                    }
                    isVisible = !isVisible;
                }
            });

            View buttonSpacer = new View(RandomMeals.this);
            LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    16
            );
            buttonSpacer.setLayoutParams(spacerParams);

            recipeLayout.addView(recipeTitleTextView);
            recipeLayout.addView(dividerView);
            recipeLayout.addView(recipeImageView);
            recipeLayout.addView(showIngredientsButton);
            recipeLayout.addView(ingredientsTextView);
            recipeLayout.addView(buttonSpacer);
            recipeLayout.addView(showRecipeButton);
            recipeLayout.addView(instructionsTextView);

            food_images.addView(recipeLayout);
        }
    }

    // Fetch additional recipe details for ingredients or instructions
    private void getRecipeDetails(int recipeId, final TextView ingredientsTextView, final TextView instructionsTextView) {
        SpoonacularApiService apiService = ApiClient.getClient().create(SpoonacularApiService.class);
        Call<GetRandomMeals> call = apiService.getRecipeDetails(recipeId);

        call.enqueue(new Callback<GetRandomMeals>() {
            @Override
            public void onResponse(Call<GetRandomMeals> call, Response<GetRandomMeals> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetRandomMeals recipe = response.body();

                    if (ingredientsTextView != null) {
                        StringBuilder ingredientsBuilder = new StringBuilder();
                        if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                            for (String ingredient : recipe.getIngredients()) {
                                ingredientsBuilder.append("- ").append(ingredient).append("\n");
                            }
                        } else {
                            ingredientsBuilder.append("No ingredients available.");
                        }
                        ingredientsTextView.setText(ingredientsBuilder.toString());
                    }

                    if (instructionsTextView != null) {
                        String instructions = recipe.getInstructions();
                        if (instructions != null && !instructions.isEmpty()) {
                            instructionsTextView.setText(Html.fromHtml(instructions));
                        } else {
                            instructionsTextView.setText("No instructions available.");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GetRandomMeals> call, Throwable t) {
                Toast.makeText(RandomMeals.this, "Failed to fetch recipe details.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}