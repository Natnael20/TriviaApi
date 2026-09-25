package com.example.Cortex.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Persists high score and best streak using SharedPreferences.
 */
public class ScoreManager {

    private static final String PREFS_NAME = "cortex_prefs";
    private static final String KEY_HIGH_SCORE = "high_score";
    private static final String KEY_BEST_STREAK = "best_streak";

    private final SharedPreferences prefs;

    public ScoreManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getHighScore() {
        return prefs.getInt(KEY_HIGH_SCORE, 0);
    }

    public int getBestStreak() {
        return prefs.getInt(KEY_BEST_STREAK, 0);
    }

    /**
     * Attempts to save a new score. Saves only if it's a new best.
     *
     * @param score The score to try
     * @param streak The best streak of the session
     * @return true if a new high score was set
     */
    public boolean trySave(int score, int streak) {
        boolean newHigh = false;

        if (score > getHighScore()) {
            prefs.edit().putInt(KEY_HIGH_SCORE, score).apply();
            newHigh = true;
        }

        if (streak > getBestStreak()) {
            prefs.edit().putInt(KEY_BEST_STREAK, streak).apply();
        }

        return newHigh;
    }
}