package com.example.toggleview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.graphics.Color;
import android.os.Bundle;
import com.example.toggle.ToggleView;

public class MainActivity extends AppCompatActivity {
    private ToggleView toggleView;
    private ConstraintLayout bg;
    private int bright = Color.parseColor("#ffd3b6"), dark = Color.parseColor("#8874a3");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bg = findViewById(R.id.myBackGround);
        toggleView = findViewById(R.id.toggle);
        toggleView.setListener(isDay -> {
            if (isDay)
            bg.setBackgroundColor(bright);
            else
                bg.setBackgroundColor(dark);
        });
    }
}