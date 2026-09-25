package com.example.Cortex;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Cortex.components.Quiz;

import com.example.Cortex.util.Constants;

import com.example.Cortex.components.core.NetworkBanner;

/**
 * Quiz screen — reads extras and delegates everything to the Quiz component.
 */
public class QuizActivity extends AppCompatActivity {

    private Quiz quiz;
    private NetworkBanner networkBanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        networkBanner = new NetworkBanner(this);

        String amountLabel = getIntent().getStringExtra(Constants.EXTRA_AMOUNT);
        String category    = getIntent().getStringExtra(Constants.EXTRA_CATEGORY);
        String difficulty  = getIntent().getStringExtra(Constants.EXTRA_DIFFICULTY);
        String type        = getIntent().getStringExtra(Constants.EXTRA_TYPE);

        quiz = new Quiz(this);
        quiz.start(amountLabel, category, difficulty, type);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (networkBanner != null) networkBanner.refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (quiz != null) quiz.shutdown();
    }
}