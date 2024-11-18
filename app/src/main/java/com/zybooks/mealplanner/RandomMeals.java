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

    //create variables
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

        // Link xml elements to code variables
        food_images = findViewById(R.id.output);
        user_num_meals = findViewById(R.id.user_num_meals);
        user_budget = findViewById(R.id.user_budget);
        get_random_meals = findViewById(R.id.get_meals);
        main_activity_button = findViewById(R.id.main_button);

        // Button to go back to the main activity screen
        main_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RandomMeals.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Button to get random meals based on user's input
        get_random_meals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mealCountString = user_num_meals.getText().toString();
                String priceLimitString = user_budget.getText().toString();

                // Check if user entered values for both meal count and price limit
                if (!mealCountString.isEmpty() && !priceLimitString.isEmpty()){
                    int mealCount = Integer.parseInt(mealCountString);
                    double priceLimit = Double.parseDouble(priceLimitString);
                    getRandomMeals(mealCount, priceLimit); // Fetch meals that fit within user's price range
                } else {
                    Toast.makeText(RandomMeals.this, "Please enter both meal count and price limit", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to get and display recipe details (the ingredients and instructions)
    private void getRecipeDetails(int recipeId, final TextView ingredientsTextView, final TextView instructionsTextView) {
        SpoonacularApiService apiService = ApiClient.getClient().create(SpoonacularApiService.class);
        Call<GetRandomMeals> call = apiService.getRecipeDetails(recipeId);

        call.enqueue(new Callback<GetRandomMeals>() {
            @Override
            public void onResponse(Call<GetRandomMeals> call, Response<GetRandomMeals> response) {
                // If the call is successful and we have recipe data
                if (response.isSuccessful() && response.body() != null) {
                    GetRandomMeals recipe = response.body();

                    // Show the recipe title
                    ingredientsTextView.setText("- " + recipe.getTitle() + "\n");

                    // Display ingredients, if available
                    StringBuilder ingredientsBuilder = new StringBuilder();
                    if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                        for (String ingredient : recipe.getIngredients()) {
                            ingredientsBuilder.append("- ").append(ingredient).append("\n");
                        }
                    } else {
                        ingredientsBuilder.append("No ingredients available.");
                    }
                    ingredientsTextView.setText(ingredientsBuilder.toString());

                    // Show instructions, if available
                    String instructions = recipe.getInstructions();
                    if (instructions != null && !instructions.isEmpty()) {
                        instructionsTextView.setText(Html.fromHtml(instructions));
                    } else {
                        instructionsTextView.setText("No instructions available.");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetRandomMeals> call, Throwable t) {
                // Handle any failure in getting recipe details
                Log.e(call.toString(), t.toString());
                Toast.makeText(RandomMeals.this, "There has been an error.", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Method to fetch random meals based on user input for count and budget
    private void getRandomMeals(int number, final double priceLimit) {
        SpoonacularApiService apiService = ApiClient.getClient().create(SpoonacularApiService.class);
        Call<GetRandomMeals> call = apiService.getRandomMeals(number    );

        call.enqueue(new Callback<GetRandomMeals>() {
            @Override
            public void onResponse(Call<GetRandomMeals> call, Response<GetRandomMeals> response) {

                // If the response is successful and contains recipes
                if (response.isSuccessful() && response.body() != null) {
                    food_images.removeAllViews(); // Clear previous results

                    List<GetRandomMeals> recipes = response.body().getRecipes(); // Get recipes from response
                    if (recipes != null) {
                        for (GetRandomMeals recipe : recipes) {
                            if (recipe == null) continue;

                            //Pull the price estimate and filter based on the user's budget
                            double estimatedPrice = recipe.getPrice() / 10;
                            if (estimatedPrice > priceLimit) {
                                continue; // Skip recipes over budget
                            }

                            //Set up the layout for each recipe to display its details
                            LinearLayout recipeLayout = new LinearLayout(RandomMeals.this);
                            recipeLayout.setOrientation(LinearLayout.VERTICAL);
                            recipeLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            recipeLayout.setPadding(16, 16, 16, 16);
                            recipeLayout.setGravity(Gravity.CENTER_HORIZONTAL);

                            // Recipe title with estimated price
                            TextView recipeTitleTextView = new TextView(RandomMeals.this);
                            recipeTitleTextView.setText(recipe.getTitle() + " ($" + String.format("%.2f", estimatedPrice) + ")");
                            recipeTitleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            recipeTitleTextView.setPadding(16, 0, 16, 0);
                            recipeTitleTextView.setGravity(Gravity.CENTER);
                            recipeTitleTextView.setTypeface(null, Typeface.BOLD);
                            recipeTitleTextView.setTextSize(18);

                            // Divider line for visual separation
                            View dividerView = new View(RandomMeals.this);
                            dividerView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    2));
                            dividerView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

                            // Recipe image
                            ImageView recipeImageView = new ImageView(RandomMeals.this);
                            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                                    750, 750);
                            imageLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                            recipeImageView.setLayoutParams(imageLayoutParams);
                            recipeImageView.setPadding(16, 16, 16, 16);
                            Picasso.get().load(recipe.getImage()).into(recipeImageView);

                            // Button to toggle ingredients display
                            Button showIngredientsButton = new Button(RandomMeals.this);
                            showIngredientsButton.setText("Show Ingredients");
                            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            buttonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                            buttonLayoutParams.setMargins(0, 16, 0, 16);
                            showIngredientsButton.setLayoutParams(buttonLayoutParams);

                            // Button to toggle recipe instructions display
                            Button showRecipeButton = new Button(RandomMeals.this);
                            showRecipeButton.setText("Show Recipe");
                            showRecipeButton.setLayoutParams(buttonLayoutParams);

                            // Ingredients TextView (hidden by default)
                            TextView ingredientsTextView = new TextView(RandomMeals.this);
                            ingredientsTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            ingredientsTextView.setVisibility(View.GONE);

                            // Instructions TextView (hidden by default)
                            TextView instructionsTextView = new TextView(RandomMeals.this);
                            instructionsTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            instructionsTextView.setVisibility(View.GONE);

                            // Toggle ingredients visibility when button is clicked
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

                                        // Fetch ingredients if they aren’t already fetched
                                        getRecipeDetails(recipe.getId(), ingredientsTextView, instructionsTextView);
                                    }
                                    isVisible = !isVisible;
                                }
                            });

                            // Toggle recipe instructions visibility when button is clicked
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

                                        // Fetch instructions if they aren’t already fetched
                                        getRecipeDetails(recipe.getId(), ingredientsTextView, instructionsTextView);
                                    }
                                    isVisible = !isVisible;
                                }
                            });

                            // Add everything to the recipe layout
                            recipeLayout.addView(recipeTitleTextView);
                            recipeLayout.addView(dividerView);
                            recipeLayout.addView(recipeImageView);
                            recipeLayout.addView(showIngredientsButton);
                            recipeLayout.addView(ingredientsTextView);
                            recipeLayout.addView(showRecipeButton);
                            recipeLayout.addView(instructionsTextView);

                            // Finally, add this recipe layout to the main container
                            food_images.addView(recipeLayout);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GetRandomMeals> call, Throwable t) {
                // Handle any failure in fetching meals
                Log.e(call.toString(), t.toString());
                Toast.makeText(RandomMeals.this, "There has been an error.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
