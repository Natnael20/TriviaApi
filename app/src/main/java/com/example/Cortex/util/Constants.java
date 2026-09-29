package com.example.Cortex.util;

/**
 * App-wide constants.
 */
public class Constants {

    // API
    public static final String BASE_URL = "https://opentdb.com/api.php";

    // Intent extras — solo
    public static final String EXTRA_AMOUNT = "extra_amount";
    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_DIFFICULTY = "extra_difficulty";
    public static final String EXTRA_TYPE = "extra_type";

    // Intent extras — result
    public static final String EXTRA_SCORE = "extra_score";
    public static final String EXTRA_CORRECT = "extra_correct";
    public static final String EXTRA_WRONG = "extra_wrong";
    public static final String EXTRA_BEST_STREAK = "extra_best_streak";
    public static final String EXTRA_ACCURACY = "extra_accuracy";
    public static final String EXTRA_TOTAL = "extra_total";

    // Timer
    public static final int TIME_PER_QUESTION_SECONDS = 15;
    public static final int TIMER_TICK_MS = 1000;
    public static final int TIMER_WARNING_SECONDS = 10;
    public static final int TIMER_DANGER_SECONDS = 5;

    // Defaults
    public static final int MAX_AMOUNT = 50;
    public static final String DEFAULT_CATEGORY = "Any Category";
    public static final String DEFAULT_DIFFICULTY = "Any Difficulty";
    public static final String DEFAULT_TYPE = "Any Type";

    // Difficulty values
    public static final String DIFFICULTY_EASY = "easy";
    public static final String DIFFICULTY_MEDIUM = "medium";
    public static final String DIFFICULTY_HARD = "hard";

    // Type values
    public static final String TYPE_MULTIPLE = "multiple";
    public static final String TYPE_TRUE_FALSE = "boolean";

    // Type labels
    public static final String LABEL_MULTIPLE = "Multiple Choice";
    public static final String LABEL_TRUE_FALSE = "True / False";
}