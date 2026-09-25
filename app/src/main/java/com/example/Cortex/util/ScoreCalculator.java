package com.example.Cortex.util;

/**
 * Calculates score for correct answers based on the current streak.
 *
 * Formula:
 *   points = BASE + (streak - 1) * STREAK_BONUS
 *
 * Examples (BASE = 100, STREAK_BONUS = 25):
 *   streak 1 → 100
 *   streak 2 → 125
 *   streak 3 → 150
 *   streak 4 → 175
 */
public class ScoreCalculator {

    private static final int BASE = 100;
    private static final int STREAK_BONUS = 25;

    /**
     * Calculates points earned for a correct answer.
     *
     * @param streakAfterAnswer The streak value AFTER this correct answer
     * @return Points earned
     */
    public static int pointsFor(int streakAfterAnswer) {
        if (streakAfterAnswer < 1) return 0;
        return BASE + (streakAfterAnswer - 1) * STREAK_BONUS;
    }

    /**
     * Returns the multiplier label for the current streak.
     * Useful for showing "x2" style bonus badges on the UI.
     *
     * @param streak The current streak
     * @return A short label like "1x", "2x", "3x"
     */
    public static String multiplierLabel(int streak) {
        if (streak <= 0) return "1x";
        return streak + "x";
    }
}