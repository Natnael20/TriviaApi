package com.example.Cortex.util;

import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

/**
 * Animation helpers.
 */
public class AnimationUtils {

    private static final long COUNT_DURATION_MS = 400L;

    /**
     * Animates a TextView from its current numeric value to the target value.
     */
    public static void animateCount(TextView textView, int from, int to, String suffix) {
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(COUNT_DURATION_MS);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(a ->
            textView.setText(a.getAnimatedValue() + suffix));
        animator.start();
    }
}