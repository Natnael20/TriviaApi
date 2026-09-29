package com.example.Cortex.components.core;

import android.app.Activity;
import android.widget.TextView;

import com.example.Cortex.R;
import com.example.Cortex.manager.ScoreManager;

/**
 * Component that displays the high score and best streak on the home screen.
 */
public class HighScore {

    private final Activity activity;
    private final ScoreManager scoreManager;

    private TextView highScoreValue;
    private TextView bestStreakValue;

    public HighScore(Activity activity, ScoreManager scoreManager) {
        this.activity = activity;
        this.scoreManager = scoreManager;
    }

    /**
     * Wires the views and displays the current high score / best streak.
     */
    public void initialize() {
        highScoreValue = activity.findViewById(R.id.highScoreValueTextView);
        bestStreakValue = activity.findViewById(R.id.bestStreakValueTextView);
        refresh();
    }

    /**
     * Refreshes the displayed values from the ScoreManager.
     */
    public void refresh() {
        if (highScoreValue == null || bestStreakValue == null) return;

        highScoreValue.setText(String.valueOf(scoreManager.getHighScore()));
        bestStreakValue.setText(String.valueOf(scoreManager.getBestStreak()));
    }
}