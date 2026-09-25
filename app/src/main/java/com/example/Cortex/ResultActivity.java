package com.example.Cortex;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Cortex.components.Result;

/**
 * Result screen — shown after the quiz finishes.
 */
public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Result result = new Result(this);
        result.initialize();
    }
}