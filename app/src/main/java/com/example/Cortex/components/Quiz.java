package com.example.Cortex.components;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.Cortex.R;
import com.example.Cortex.ResultActivity;
import com.example.Cortex.components.core.EdgeLightningView;
import com.example.Cortex.components.core.Menu;
import com.example.Cortex.listener.QuizListener;
import com.example.Cortex.manager.QuizManager;
import com.example.Cortex.model.Question;
import com.example.Cortex.model.QuizSession;
import com.example.Cortex.util.AnimationUtils;
import com.example.Cortex.util.Constants;
import com.example.Cortex.util.ScoreCalculator;

import java.util.List;

/**
 * Component that manages the Quiz screen UI.
 */
public class Quiz implements QuizListener {

    private final Activity activity;
    private final QuizManager quizManager = new QuizManager();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private TextView scoreTextView;
    private TextView timerTextView;
    private TextView streakTextView;
    private ProgressBar progressBar;
    private TextView progressTextView;
    private TextView categoryTextView;
    private TextView difficultyTextView;
    private TextView typeTextView;
    private TextView questionTextView;
    private LinearLayout answersContainer;
    private TextView nextButton;
    private EdgeLightningView edgeLightningView;

    private boolean answered = false;
    private int secondsLeft = Constants.TIME_PER_QUESTION_SECONDS;
    private int lastDisplayedScore = 0;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            secondsLeft--;
            updateTimerDisplay();

            if (secondsLeft <= 0) {
                onTimeUp();
            } else {
                handler.postDelayed(this, Constants.TIMER_TICK_MS);
            }
        }
    };

    public Quiz(Activity activity) {
        this.activity = activity;
    }

    public void start(String amountLabel, String category, String difficulty, String type) {
        lastDisplayedScore = 0;
        initializeViews();
        quizManager.setListener(this);
        quizManager.start(amountLabel, category, difficulty, type);
    }

    public void shutdown() {
        stopTimer();
        quizManager.shutdown();
    }

    private void initializeViews() {
        scoreTextView = activity.findViewById(R.id.scoreTextView);
        timerTextView = activity.findViewById(R.id.timerTextView);
        streakTextView = activity.findViewById(R.id.streakTextView);
        progressBar = activity.findViewById(R.id.progressBar);
        progressTextView = activity.findViewById(R.id.progressTextView);
        categoryTextView = activity.findViewById(R.id.categoryTextView);
        difficultyTextView = activity.findViewById(R.id.difficultyTextView);
        typeTextView = activity.findViewById(R.id.typeTextView);
        questionTextView = activity.findViewById(R.id.questionTextView);
        answersContainer = activity.findViewById(R.id.answersContainer);
        nextButton = activity.findViewById(R.id.nextButton);
        edgeLightningView = activity.findViewById(R.id.edgeLightningView);

        nextButton.setOnClickListener(v -> goToNextQuestion());

        // Enable Edge Lightning if the toggle is on
        if (edgeLightningView != null && Menu.isEdgeLightningEnabled()) {
            edgeLightningView.setVisibility(View.VISIBLE);
            edgeLightningView.setGlowColor(0xFF00D9FF);
            edgeLightningView.setIntensity(0.6f);
        }
    }

    @Override
    public void onQuestionReady(Question question, int number, int total) {
        progressTextView.setText("Q " + number + " / " + total);
        categoryTextView.setText(question.getCategory().toUpperCase());
        difficultyTextView.setText(question.getDifficulty().toUpperCase());
        typeTextView.setText(questionType(question.getType()));
        questionTextView.setText(question.getQuestion());

        int progress = (int) (((number - 1) / (float) total) * 100);
        progressBar.setProgress(progress);

        updateScoreAndStreak();

        answered = false;
        buildAnswerButtons(question);
        startTimer();

        // Reset Edge Lightning to accent color
        if (edgeLightningView != null && edgeLightningView.getVisibility() == View.VISIBLE) {
            edgeLightningView.setGlowColor(0xFF00D9FF);
            edgeLightningView.setIntensity(0.6f);
        }
    }

    // ============ TIMER ============

    private void startTimer() {
        stopTimer();
        secondsLeft = Constants.TIME_PER_QUESTION_SECONDS;
        updateTimerDisplay();
        handler.postDelayed(timerRunnable, Constants.TIMER_TICK_MS);
    }

    private void stopTimer() {
        handler.removeCallbacks(timerRunnable);
    }

    private void updateTimerDisplay() {
        timerTextView.setText(String.valueOf(secondsLeft));

        int color;
        if (secondsLeft <= Constants.TIMER_DANGER_SECONDS) {
            color = ContextCompat.getColor(activity, R.color.error);
        } else if (secondsLeft <= Constants.TIMER_WARNING_SECONDS) {
            color = ContextCompat.getColor(activity, R.color.warning);
        } else {
            color = ContextCompat.getColor(activity, R.color.text_primary);
        }
        timerTextView.setTextColor(color);
    }

    private void onTimeUp() {
        if (answered) return;
        answered = true;

        quizManager.checkAnswer("");
        updateScoreAndStreak();

        Question current = quizManager.getCurrentQuestion();
        if (current != null) highlightAnswers(current, null);

        // Red Edge Lightning on timeout
        if (edgeLightningView != null && edgeLightningView.getVisibility() == View.VISIBLE) {
            edgeLightningView.setGlowColor(0xFFFF4757);
            edgeLightningView.setIntensity(1f);
        }

        Toast.makeText(activity, "Time's up!", Toast.LENGTH_SHORT).show();
    }

    // ============ ANSWERS ============

    private void buildAnswerButtons(Question question) {
        answersContainer.removeAllViews();

        List<String> answers = question.getAllAnswers();
        if (answers == null) return;

        for (String answer : answers) {
            answersContainer.addView(createAnswerView(answer, question));
        }
    }

    private TextView createAnswerView(String answer, Question question) {
        TextView tv = new TextView(activity);
        tv.setText(answer);
        tv.setTextColor(ContextCompat.getColor(activity, R.color.text_primary));
        tv.setTextSize(16f);
        tv.setPadding(48, 32, 48, 32);
        tv.setBackgroundResource(R.drawable.answer_option_bg);
        tv.setClickable(true);
        tv.setFocusable(true);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 12);
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER_VERTICAL);

        tv.setOnClickListener(v -> handleAnswerClick(tv, answer, question));
        return tv;
    }

    private void handleAnswerClick(TextView clicked, String answer, Question question) {
        if (answered) return;
        answered = true;
        stopTimer();

        boolean isCorrect = quizManager.checkAnswer(answer);
        updateScoreAndStreak();
        highlightAnswers(question, clicked);

        // Edge Lightning reaction: green if correct, red if wrong
        if (edgeLightningView != null && edgeLightningView.getVisibility() == View.VISIBLE) {
            edgeLightningView.setGlowColor(isCorrect ? 0xFF00E676 : 0xFFFF4757);
            edgeLightningView.setIntensity(1f);
        }
    }

    private void highlightAnswers(Question question, TextView clicked) {
        for (int i = 0; i < answersContainer.getChildCount(); i++) {
            TextView child = (TextView) answersContainer.getChildAt(i);
            String text = child.getText().toString();

            if (text.equals(question.getCorrectAnswer())) {
                child.setBackgroundResource(R.drawable.answer_correct_bg);
            } else if (child == clicked) {
                child.setBackgroundResource(R.drawable.answer_wrong_bg);
            }

            child.setClickable(false);
            child.setFocusable(false);
        }
    }

    // ============ NAVIGATION ============

    private void goToNextQuestion() {
        stopTimer();
        Question next = quizManager.nextQuestion();

        if (next == null) {
            QuizSession session = quizManager.getSession();

            Intent intent = new Intent(activity, ResultActivity.class);
            intent.putExtra(Constants.EXTRA_SCORE, session.getScore());
            intent.putExtra(Constants.EXTRA_CORRECT, session.getCorrectCount());
            intent.putExtra(Constants.EXTRA_WRONG, session.getWrongCount());
            intent.putExtra(Constants.EXTRA_BEST_STREAK, session.getBestStreak());
            intent.putExtra(Constants.EXTRA_TOTAL, session.getTotalQuestions());

            intent.putExtra(Constants.EXTRA_AMOUNT,
                activity.getIntent().getStringExtra(Constants.EXTRA_AMOUNT));
            intent.putExtra(Constants.EXTRA_CATEGORY,
                activity.getIntent().getStringExtra(Constants.EXTRA_CATEGORY));
            intent.putExtra(Constants.EXTRA_DIFFICULTY,
                activity.getIntent().getStringExtra(Constants.EXTRA_DIFFICULTY));
            intent.putExtra(Constants.EXTRA_TYPE,
                activity.getIntent().getStringExtra(Constants.EXTRA_TYPE));

            activity.startActivity(intent);
            activity.finish();
        }
    }

    // ============ HELPERS ============

    private void updateScoreAndStreak() {
        QuizSession session = quizManager.getSession();
        if (session == null) return;

        int target = session.getScore();

        if (target != lastDisplayedScore) {
            AnimationUtils.animateCount(scoreTextView, lastDisplayedScore, target, " pts");
            lastDisplayedScore = target;
        }

        int streak = session.getStreak();
        String multiplier = ScoreCalculator.multiplierLabel(streak);
        streakTextView.setText("" + multiplier);

        if (streak > 0) {
            streakTextView.setTextColor(ContextCompat.getColor(activity, R.color.warning));
        } else {
            streakTextView.setTextColor(ContextCompat.getColor(activity, R.color.text_hint));
        }
    }

    private String questionType(String type) {
        if (type == null) return "";
        if (type.equalsIgnoreCase("multiple")) return "MULTI";
        if (type.equalsIgnoreCase("boolean"))  return "TRUE / FALSE";
        return type.toUpperCase();
    }
}