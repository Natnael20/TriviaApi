package com.example.Cortex.components;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import com.example.Cortex.SetupActivity;
import com.example.Cortex.QuizActivity;
import com.example.Cortex.manager.ScoreManager;
import com.example.Cortex.R;
import com.example.Cortex.model.QuizSession;
import com.example.Cortex.util.Constants;
import com.google.android.material.button.MaterialButton;

/**
 * Component that manages the Result screen UI.
 */
public class Result {

    private final Activity activity;

    private TextView finalScoreTextView;
    private TextView correctCountTextView;
    private TextView wrongCountTextView;
    private TextView accuracyTextView;
    private TextView bestStreakTextView;
    private MaterialButton playAgainButton;
    private MaterialButton setupPageButton;
    private ScoreManager scoreManager;
    private boolean isNewHighScore;

    public Result(Activity activity) {
        this.activity = activity;
    }

    /**
     * Wires the UI, reads session data from the Intent, and shows the results.
     */
    public void initialize() {
        initializeViews();
        scoreManager = new ScoreManager(activity);
        loadSessionFromIntent();
        setupButtons();
    }

    private void initializeViews() {
        finalScoreTextView = activity.findViewById(R.id.finalScoreTextView);
        correctCountTextView = activity.findViewById(R.id.correctCountTextView);
        wrongCountTextView = activity.findViewById(R.id.wrongCountTextView);
        accuracyTextView = activity.findViewById(R.id.accuracyTextView);
        bestStreakTextView = activity.findViewById(R.id.bestStreakTextView);
        playAgainButton = activity.findViewById(R.id.playAgainButton);
        setupPageButton = activity.findViewById(R.id.setupPageButton);
    }

    private void loadSessionFromIntent() {
        Intent intent = activity.getIntent();

        int score = intent.getIntExtra(Constants.EXTRA_SCORE, 0);
        int correct = intent.getIntExtra(Constants.EXTRA_CORRECT, 0);
        int wrong = intent.getIntExtra(Constants.EXTRA_WRONG, 0);
        int bestStreak = intent.getIntExtra(Constants.EXTRA_BEST_STREAK, 0);
        int total = intent.getIntExtra(Constants.EXTRA_TOTAL, 0);

        // Save the score — check if it's a new high score
        isNewHighScore = scoreManager.trySave(score, bestStreak);

        int accuracy = total > 0 ? Math.round((correct / (float) total) * 100) : 0;

        finalScoreTextView.setText(String.valueOf(score));
        correctCountTextView.setText(String.valueOf(correct));
        wrongCountTextView.setText(String.valueOf(wrong));
        accuracyTextView.setText(accuracy + "%");
        bestStreakTextView.setText(" " + bestStreak);

        // Show "NEW HIGH SCORE" tag if applicable
        if (isNewHighScore) {
            // Simple: append to score unit label
            TextView scoreUnit = activity.findViewById(R.id.scoreUnitTextView);
            if (scoreUnit != null) {
                scoreUnit.setText("POINTS · NEW RECORD!");
                scoreUnit.setTextColor(ContextCompat.getColor(activity, R.color.warning));
            }
        }
    }

    private void setupButtons() {
        playAgainButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity, QuizActivity.class);
            // Reuse the same settings from the previous quiz
            intent.putExtras(activity.getIntent());
            activity.startActivity(intent);
            activity.finish();
        });

        setupPageButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity, SetupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish();
        });
    }
}