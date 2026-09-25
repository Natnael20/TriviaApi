package com.example.Cortex.model;

import java.util.List;

/**
 * Represents a single trivia question.
 */
public class Question {

    private final String question;
    private final String category;
    private final String difficulty;
    private final String type;
    private final String correctAnswer; 
    private final List<String> allAnswers;

    public Question(String question, String category, String difficulty, String type,
                    String correctAnswer, List<String> allAnswers) {
        this.question = question;
        this.category = category;
        this.difficulty = difficulty;
        this.type = type;
        this.correctAnswer = correctAnswer;
        this.allAnswers = allAnswers;
    }

    public String getQuestion() {
        return question;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getType() {
        return type;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getAllAnswers() {
        return allAnswers;
    }
}