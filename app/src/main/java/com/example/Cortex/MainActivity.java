package com.example.Cortex;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Cortex.components.core.NetworkBanner;
import com.example.Cortex.manager.ScoreManager;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private NetworkBanner networkBanner;
    private ScoreManager scoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkBanner = new NetworkBanner(this);
        scoreManager = new ScoreManager(this);

        MaterialButton playSoloButton = findViewById(R.id.playSoloButton);
        playSoloButton.setOnClickListener(v ->
            startActivity(new Intent(MainActivity.this, SetupActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (networkBanner != null) networkBanner.start();
        refreshHighScore();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (networkBanner != null) networkBanner.stop();
    }

    private void refreshHighScore() {
        TextView highScoreValue = findViewById(R.id.highScoreValueTextView);
        TextView bestStreakValue = findViewById(R.id.bestStreakValueTextView);

        highScoreValue.setText(String.valueOf(scoreManager.getHighScore()));
        bestStreakValue.setText(String.valueOf(scoreManager.getBestStreak()));
    }
}