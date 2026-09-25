package com.example.Cortex.components;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.example.Cortex.QuizActivity;
import com.example.Cortex.R;
import com.example.Cortex.components.core.Dialog;
import com.example.Cortex.util.Constants;
import com.google.android.material.button.MaterialButton;

/**
 * Component that manages the Setup screen UI.
 * Step 3 — amount, category, difficulty, type.
 */
public class Setup {

    private final Activity activity;
    private final Dialog dialog;

    private TextView amountValue;
    private TextView categoryValue;
    private TextView easyChip;
    private TextView mediumChip;
    private TextView hardChip;
    private TextView typeValue;
    private MaterialButton startQuizButton;

    private String selectedDifficulty = Constants.DEFAULT_DIFFICULTY;

    public Setup(Activity activity) {
        this.activity = activity;
        this.dialog = new Dialog(activity);
    }

    public void initialize() {
        initializeViews();
        setupDropdowns();
        setupDifficultyChips();
        setupStartButton();
    }

    private void initializeViews() {
        amountValue = activity.findViewById(R.id.amountValueTextView);
        categoryValue = activity.findViewById(R.id.categoryValueTextView);
        easyChip = activity.findViewById(R.id.easyChip);
        mediumChip = activity.findViewById(R.id.mediumChip);
        hardChip = activity.findViewById(R.id.hardChip);
        typeValue = activity.findViewById(R.id.typeValueTextView);
        startQuizButton = activity.findViewById(R.id.startQuizButton);
    }

    private void setupDropdowns() {
        amountValue.setOnClickListener(v ->
            dialog.show(amountValue, R.array.amounts, true));
        categoryValue.setOnClickListener(v ->
            dialog.show(categoryValue, R.array.categories, true));
        typeValue.setOnClickListener(v ->
            dialog.show(typeValue, R.array.question_types, false));
    }

    private void setupDifficultyChips() {
        easyChip.setOnClickListener(v -> toggleDifficulty(Constants.DIFFICULTY_EASY));
        mediumChip.setOnClickListener(v -> toggleDifficulty(Constants.DIFFICULTY_MEDIUM));
        hardChip.setOnClickListener(v -> toggleDifficulty(Constants.DIFFICULTY_HARD));
    }

    private void toggleDifficulty(String difficulty) {
        if (selectedDifficulty.equals(difficulty)) {
            selectedDifficulty = Constants.DEFAULT_DIFFICULTY;
        } else {
            selectedDifficulty = difficulty;
        }
        updateChipVisuals();
    }

    private void updateChipVisuals() {
        int selectedBg = R.drawable.chip_selected;
        int normalBg = R.drawable.chip_outline;

        easyChip.setBackgroundResource(
            selectedDifficulty.equals(Constants.DIFFICULTY_EASY) ? selectedBg : normalBg);
        mediumChip.setBackgroundResource(
            selectedDifficulty.equals(Constants.DIFFICULTY_MEDIUM) ? selectedBg : normalBg);
        hardChip.setBackgroundResource(
            selectedDifficulty.equals(Constants.DIFFICULTY_HARD) ? selectedBg : normalBg);
    }

    private void setupStartButton() {
        startQuizButton.setOnClickListener(v -> launchQuiz());
    }

    private void launchQuiz() {
        Intent intent = new Intent(activity, QuizActivity.class);
        intent.putExtra(Constants.EXTRA_AMOUNT, amountValue.getText().toString());
        intent.putExtra(Constants.EXTRA_CATEGORY, categoryValue.getText().toString());
        intent.putExtra(Constants.EXTRA_DIFFICULTY, selectedDifficulty);
        intent.putExtra(Constants.EXTRA_TYPE, typeValue.getText().toString());
        activity.startActivity(intent);
    }
}