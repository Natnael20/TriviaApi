package com.example.Cortex.listener;

import com.example.Cortex.model.Question;

/**
 * Listener for quiz events.
 */
public interface QuizListener {
    void onQuestionReady(Question question, int number, int total);
}