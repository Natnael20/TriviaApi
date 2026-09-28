package com.example.Cortex.components;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.Cortex.R;
import com.example.Cortex.ResultActivity;
import com.example.Cortex.components.core.EdgeLightningView;
import com.example.Cortex.listener.QuizListener;
import com.example.Cortex.manager.EdgeLightningManager;
import com.example.Cortex.manager.QuizManager;
import com.example.Cortex.model.Question;
import com.example.Cortex.model.QuizSession;
import com.example.Cortex.util.AnimationUtils;
import com.example.Cortex.util.Constants;
import com.example.Cortex.util.ScoreCalculator;
import com.example.Cortex.Enum.Event;
import java.util.Locale;
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
    private EdgeLightningManager lightning;

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

    /**
     * Starts the quiz with the given setup values.
     */
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
        
        lightning = EdgeLightningManager.getInstance(activity);
        nextButton.setOnClickListener(v -> goToNextQuestion());
        lightning.applyEvent(edgeLightningView, Event.NEUTRAL);
    }

    @Override 
    public void onQuestionReady(Question question, int number, int total) {
        progressTextView.setText(number + " / " + total);
        categoryTextView.setText(question.getCategory().toUpperCase());
        difficultyTextView.setText(applyDifficultyColor(question.getDifficulty()));
        typeTextView.setText(quizManager.questionType(question.getType()));
        questionTextView.setText(question.getQuestion());
        progressBar.setProgress(quizManager.calculateProgress(number, total));
        updateScoreAndStreak();

        answered = false;
        buildAnswerButtons(question);
        startTimer();
        lightning.applyEvent(edgeLightningView, Event.NEW_QUESTION);
    }

    private String applyDifficultyColor(String difficulty) {
        String key = difficulty.trim().toLowerCase();
        int color;
        switch (key) {
            case Constants.DIFFICULTY_EASY:
                color = ContextCompat.getColor(activity, R.color.success);
                break;
            case Constants.DIFFICULTY_MEDIUM:
                color = ContextCompat.getColor(activity, R.color.warning);
                break;
            case Constants.DIFFICULTY_HARD:
                color = ContextCompat.getColor(activity, R.color.error);
                break;
            default:
                color = ContextCompat.getColor(activity, R.color.text_secondary);
                break;
        }

        difficultyTextView.setTextColor(color);
        return key.toUpperCase();
    }

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

        lightning.applyEvent(edgeLightningView, Event.TIME_UP);
    }

    // ============ Answer Buttons ============

    private void buildAnswerButtons(Question question) {
        answersContainer.removeAllViews();

        List<String> answers = question.getAllAnswers();
        
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
        tv.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 12);
        tv.setLayoutParams(params);

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

        lightning.applyEvent(edgeLightningView,
            isCorrect ? Event.CORRECT : Event.WRONG);
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

    // ============ Navigation ============

    private void goToNextQuestion() {
        stopTimer();
        Question next = quizManager.nextQuestion();

        if (next == null) {
            openResultScreen();
        }
    }

    private void openResultScreen() {
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

    // ============ Score + Streak ============

    private void updateScoreAndStreak() {
        QuizSession session = quizManager.getSession();
        if (session == null) return;

        int target = session.getScore();
        if (target != lastDisplayedScore) {
            AnimationUtils.animateCount(scoreTextView, lastDisplayedScore, target, " pts");
            lastDisplayedScore = target;
        }

        int streak = session.getStreak();
        streakTextView.setText(ScoreCalculator.multiplierLabel(streak));

        if (streak > 0) {
            streakTextView.setTextColor(ContextCompat.getColor(activity, R.color.warning));
        } else {
            streakTextView.setTextColor(ContextCompat.getColor(activity, R.color.text_secondary));
        }
    }
}