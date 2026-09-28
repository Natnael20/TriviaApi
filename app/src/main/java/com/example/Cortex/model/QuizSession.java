package com.example.Cortex.model;

/**
 * Holds the runtime state of a single quiz session.
 */
public class QuizSession {

    private int currentIndex;
    private int score;
    private int streak;
    private int bestStreak;
    private int correctCount;
    private int wrongCount;
    private int totalQuestions;

    public int getCurrentIndex() { 
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) { 
        this.currentIndex = currentIndex; 
    }

    public int getScore() { 
        return score; 
    }

    public void setScore(int score) { 
        this.score = score; 
    }

    public int getStreak() { 
        return streak; 
    }

    public void setStreak(int streak) { 
        this.streak = streak; 
    }

    public int getBestStreak() { 
        return bestStreak; 
    }

    public void setBestStreak(int bestStreak) { 
        this.bestStreak = bestStreak; 
    }

    public int getCorrectCount() { 
        return correctCount; 
    }

    public void setCorrectCount(int correctCount) { 
        this.correctCount = correctCount; 
    }

    public int getWrongCount() { 
        return wrongCount; 
    }

    public void setWrongCount(int wrongCount) { 
        this.wrongCount = wrongCount; 
    }

    public int getTotalQuestions() { 
        return totalQuestions; 
    }

    public void setTotalQuestions(int totalQuestions) { 
        this.totalQuestions = totalQuestions; 
    }
}