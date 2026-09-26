# Cortex

A fast-paced Android quiz app that challenges you to think faster and learn more.

## Overview

Cortex is an Android quiz application built with Java that fetches trivia questions from the Open Trivia Database (OpenTDB) API. It supports configurable categories, difficulties, and question types, with a scoring system driven by streak multipliers and a timer per question.

The app was developed as a final project (gesällprov) with a focus on clean modular architecture, layered design, and an enjoyable user experience.

## Features

### Core
- **Fetch questions from OpenTDB** — real, live trivia with no API key required
- **Configurable quiz setup**
  - Category selection (General Knowledge, Science, Computers, History, Sports, Music, or Any)
  - Difficulty (Easy, Medium, Hard, or Any)
  - Number of questions (5 to 50)
  - Question type (Multiple Choice, True/False, or Any)
- **Timer per question** — 15 seconds, with color shifting from white → amber → red
- **Answer feedback** — correct answers glow green, wrong answers shake red
- **Score and streak system**
  - Base points: 100 per correct answer
  - Streak multiplier: +25 per consecutive correct answer
  - Streak resets on a wrong answer or timeout
- **Result screen** — final score, correct/wrong counts, accuracy %, best streak
- **High score persistence** — saves your best score and best streak locally (SharedPreferences)
- **Network status banner** — shows when the device has no internet connection
- **Edge Lightning** — an optional animated border effect that moves around the quiz screen and changes color based on whether your answer was correct (green) or wrong (red)

## Screens

1. **Home** — welcome screen with high score display
2. **Setup** — configure category, difficulty, question count, and type
3. **Quiz** — the question screen with timer, score, streak, and answers
4. **Result** — final stats and play again / home options


### Design principles

- **Activities are shells.** All logic is delegated to components.
- **Components handle UI.** They wire views and react to user input.
- **Managers handle business rules.** No manager knows about views.
- **Models hold pure data.** No logic, no Android imports.
- **Utils hold helpers.** Static methods, no state.

## Setup

### Prerequisites
- Android Studio (or VS Code with the Android extension)
- Android SDK 26 or higher
- An internet connection for the initial fetch

### Build and Run

1. Clone or copy the project folder
2. Open the project in Android Studio
3. Sync Gradle
4. Run on an emulator or physical device

Command-line build:
```bash
./gradlew assembleDebug
./gradlew installDebug
