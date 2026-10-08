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
import com.example.Cortex.listener.Callback;

import java.util.List;

/**
 * Manages the quiz: setup validation, data fetching, question flow, answer checking.
 */ 
public class QuizManager  {

    private final TriviaApi triviaApi = new TriviaApi();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private List<Question> questions;
    private QuizSession session;
    private QuizListener listener;

    public void setListener(QuizListener listener) {
        this.listener = listener;
    }

    public QuizSession getSession() {
        return session;
    }

    /**
     * Starts the quiz with the given setup values.
     */
    public void start(String amountLabel, String category, String difficulty, String type) {
        if (amountLabel == null) amountLabel = String.valueOf(Constants.MAX_AMOUNT);
        if (category == null)    category = Constants.DEFAULT_CATEGORY;
        if (difficulty == null)  difficulty = Constants.DEFAULT_DIFFICULTY;
        if (type == null)        type = Constants.DEFAULT_TYPE;

        session = new QuizSession();

        int amount = validateAmount(amountLabel);

        triviaApi.fetchQuestions(amount, category, difficulty, type,
            new Callback() {
                @Override
                public void onSuccess(String json) {
                    handler.post(() -> {
                        questions = JsonUtils.parseQuestions(json);
                        session.setTotalQuestions(questions.size());
                        session.setCurrentIndex(0);
                        showCurrentQuestion();
                    });
                }

                @Override
                public void onError(String error) {
                    handler.post(() -> {
                        // Caller decides what to do with empty list
                    });
                }
            });
    }
    
    /**
     * Returns the current question.
     */
    public Question getCurrentQuestion() {
        if (questions == null || questions.isEmpty()) {
            return null;
        } 
        int idx = session.getCurrentIndex();
        if (idx < 0 || idx >= questions.size()) {
            return null;
        } 
        return questions.get(idx);
    }
    
    /**
     * Advances to the next question.
     * @return The next question, or null if the quiz is finished
     */
    public Question nextQuestion() {
        int next = session.getCurrentIndex() + 1;
        session.setCurrentIndex(next);

        if (next >= questions.size()) {
            return null;
        }

        showCurrentQuestion();
        return questions.get(next);
    }

    /**
     * Checks the given answer and updates the session.
     */
    public boolean checkAnswer(String answer) {
        //get the current question
        Question current = getCurrentQuestion();
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

   

    public int validateAmount(String amountLabel) {
        int requested = extractNumber(amountLabel);
        if (requested <= 0) {
             return Constants.MAX_AMOUNT;
        }
        return Math.min(requested, Constants.MAX_AMOUNT);
    }
    
    /** 
    public String questionType(String type) {
        if (type.equalsIgnoreCase("multiple")) {
            return "MULTI";
        } 
        if (type.equalsIgnoreCase("boolean")) {
            return "TRUE / FALSE";
        } 
        return type.toUpperCase();
    }
    */

    public int calculateProgress(int number, int total) {
        return (int) (((number - 1) / (float) total) * 100);
    }

    public int getAccuracy() {
        int total = session.getTotalQuestions();
        return Math.round((session.getCorrectCount() / (float) total) * 100);
    }

    public void shutdown() {
        triviaApi.shutdown();
    }

    private void showCurrentQuestion() {
        if (listener == null || questions == null || questions.isEmpty()) {
            return;
        }

        Question question = getCurrentQuestion();

        int number = session.getCurrentIndex() + 1;
        listener.onQuestionReady(question, number, questions.size());
    }

    private int extractNumber(String text) {
        StringBuilder digits = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) digits.append(c);
        }

        int number = digits.length() > 0
            ? Integer.parseInt(digits.toString())
            : Constants.MAX_AMOUNT;

        return number;
    }
}