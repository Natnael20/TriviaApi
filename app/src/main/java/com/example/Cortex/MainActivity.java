package com.example.Cortex;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Cortex.components.core.HighScore;
import com.example.Cortex.components.core.NetworkBanner;
import com.example.Cortex.manager.ScoreManager;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private NetworkBanner networkBanner;
    private ScoreManager scoreManager;
    private HighScore highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkBanner = new NetworkBanner(this);
        scoreManager = new ScoreManager(this);
        highScore = new HighScore(this, scoreManager);
        highScore.initialize();

        MaterialButton playSoloButton = findViewById(R.id.playSoloButton);
        playSoloButton.setOnClickListener(v ->
            startActivity(new Intent(MainActivity.this, SetupActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (networkBanner != null) networkBanner.start();
        if (highScore != null) highScore.refresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (networkBanner != null) networkBanner.stop();
    }
}