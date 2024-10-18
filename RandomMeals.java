package com.zybooks.mealplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RandomMeals extends AppCompatActivity {

    private TextView mealTextView;
    private EditText mealCountEditText;
    private Button getMealsButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_random_meals);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button main = findViewById(R.id.main_button);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RandomMeals.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mealTextView = findViewById(R.id.mealTextView);
        mealCountEditText = findViewById(R.id.mealCount);
        getMealsButton = findViewById(R.id.getMealsButton);

        getMealsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mealCountString = mealCountEditText.getText().toString();
                if (!mealCountString.isEmpty()){
                    int mealCount = Integer.parseInt(mealCountString);
                    getRandomMeals(mealCount);
                }
                else {
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
                if (response.isSuccessful() && response.body() != null){
                    StringBuilder mealInfo = new StringBuilder();
                    for (Recipe recipe : response.body().getRecipes()){
                        mealInfo.append("Name: ").append(recipe.getTitle()).append("\n");
                        mealInfo.append("Instructions: ").append(recipe.getInstructions()).append("\n\n");
                    }
                    mealTextView.setText(mealInfo.toString());
                }
                else {
                    Log.e("API Error", "Error in API Response");
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Log.e("API Failure", t.getMessage());
            }
        });
    }

}
