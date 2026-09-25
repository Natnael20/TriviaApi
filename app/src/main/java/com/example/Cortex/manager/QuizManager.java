package com.example.Cortex.manager;

import android.os.Handler;
import android.os.Looper;

import com.example.Cortex.api.TriviaApi;
import com.example.Cortex.listener.QuizListener;
import com.example.Cortex.model.Question;
import com.example.Cortex.model.QuizSession;
import com.example.Cortex.util.Constants;
import com.example.Cortex.util.JsonUtils;
import com.example.Cortex.util.ScoreCalculator;

import java.util.List;

/**
 * Manages the quiz: setup validation, data fetching, question flow, answer checking.
 */
public class QuizManager {

    private final TriviaApi triviaApi = new TriviaApi();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private List<Question> questions;
    private int currentIndex;
    private QuizSession session;
    private QuizListener listener;

    public void setListener(QuizListener listener) {
        this.listener = listener;
    }

    /**
     * Starts the quiz with the given setup values.
     */
    public void start(String amountLabel, String category, String difficulty, String type) {
        if (amountLabel == null) amountLabel = String.valueOf(Constants.MAX_AMOUNT);
        if (category == null)    category = Constants.DEFAULT_CATEGORY;
        if (difficulty == null)  difficulty = Constants.DEFAULT_DIFFICULTY;
        if (type == null)        type = Constants.DEFAULT_TYPE;

        // Reset session state
        session = new QuizSession();

        int amount = validateAmount(amountLabel);

        triviaApi.fetchQuestions(amount, category, difficulty, type,
            new TriviaApi.Callback() {
                @Override
                public void onSuccess(String json) {
                    handler.post(() -> {
                        questions = JsonUtils.parseQuestions(json);
                        currentIndex = 0;
                        // Record total for the session
                        session.setTotalQuestions(questions.size());
                        showCurrentQuestion();
                    });
                }

                @Override
                public void onError(String error) {
                    handler.post(() -> {
                    });
                }
            });
    }

    /**
     * Checks the given answer and updates the session.
     */
    public boolean checkAnswer(String answer) {
        if (questions == null || questions.isEmpty()) return false;

        Question current = questions.get(currentIndex);
        if (current == null) return false;

        boolean isCorrect = current.getCorrectAnswer().equals(answer);

        if (isCorrect) {
            int newStreak = session.getStreak() + 1;
            session.setStreak(newStreak);

            if (newStreak > session.getBestStreak()) {
                session.setBestStreak(newStreak);
            }

            int points = ScoreCalculator.pointsFor(newStreak);
            session.setScore(session.getScore() + points);
            session.setCorrectCount(session.getCorrectCount() + 1);
        } else {
            session.setStreak(0);
            session.setWrongCount(session.getWrongCount() + 1);
        }

        return isCorrect;
    }

    /**
     * Advances to the next question.
     */
    public Question nextQuestion() {
        if (questions == null) return null;

        currentIndex++;

        if (currentIndex >= questions.size()) {
            return null;
        }
        showCurrentQuestion();
        return questions.get(currentIndex);
    }

    public QuizSession getSession() {
        return session;
    }

    public int getCurrentNumber() {
        return currentIndex + 1;
    }

    public int getTotalQuestions() {
        return questions == null ? 0 : questions.size();
    }

    public int validateAmount(String amountLabel) {
        int requested = extractNumber(amountLabel);
        if (requested <= 0) return Constants.MAX_AMOUNT;
        return Math.min(requested, Constants.MAX_AMOUNT);
    }

    public Question getCurrentQuestion() {
        if (questions == null || questions.isEmpty()) return null;
        return questions.get(currentIndex);
    }

    public void shutdown() {
        triviaApi.shutdown();
    }

    // ============ Internal ============

    private void showCurrentQuestion() {
        if (listener == null || questions == null || questions.isEmpty()) return;

        Question q = questions.get(currentIndex);
        listener.onQuestionReady(q, currentIndex + 1, questions.size());
    }

    private int extractNumber(String text) {
        if (text == null) return Constants.MAX_AMOUNT;

        StringBuilder digits = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) digits.append(c);
        }
        return digits.length() > 0
            ? Integer.parseInt(digits.toString())
            : Constants.MAX_AMOUNT;
    }
}