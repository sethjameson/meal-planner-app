package com.zybooks.mealplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display and set the layout
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about_us);

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Navigate back to the main screen when the button is clicked
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button homeScreenButton = findViewById(R.id.aboutus);
        homeScreenButton.setOnClickListener(view -> {
            Intent intent = new Intent(AboutUs.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
