package com.example.toggleview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.toggle.ToggleView;

public class MainActivity extends AppCompatActivity {
    private ToggleView toggleView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleView = findViewById(R.id.toggle);
    }
}