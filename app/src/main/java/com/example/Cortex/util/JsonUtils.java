package com.example.Cortex.util;

import com.example.Cortex.model.Question;

import org.json.JSONArray;
import org.json.JSONObject;
import android.util.Base64;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for parsing JSON responses from OpenTDB.
 * Reads question, category, difficulty, type, correct answer, and all answers.
 */
public class JsonUtils {

    private static final String KEY_RESPONSE_CODE = "response_code";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_QUESTION = "question";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_DIFFICULTY = "difficulty";
    private static final String KEY_TYPE = "type";
    private static final String KEY_CORRECT = "correct_answer";
    private static final String KEY_INCORRECT = "incorrect_answers";

    /**
     * Parses the raw JSON string from the API into a list of Question objects.
     *
     * @param json The raw JSON response
     * @return A list of parsed questions
     */
    public static List<Question> parseQuestions(String json) {
        List<Question> questions = new ArrayList<>();

        if (json == null || json.isEmpty()) return questions;

        try {
            JSONObject root = new JSONObject(json);
            int responseCode = root.optInt(KEY_RESPONSE_CODE, -1);
            if (responseCode != 0) return questions;

            JSONArray results = root.getJSONArray(KEY_RESULTS);

            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);

                String questionText  = decode(item.getString(KEY_QUESTION));
                String category = decode(item.getString(KEY_CATEGORY));
                String difficulty = decode(item.getString(KEY_DIFFICULTY));
                String type = decode(item.getString(KEY_TYPE));
                String correctAnswer = decode(item.getString(KEY_CORRECT));

                List<String> allAnswers = new ArrayList<>();
                allAnswers.add(correctAnswer);

                JSONArray incorrectArray = item.getJSONArray(KEY_INCORRECT);
                for (int j = 0; j < incorrectArray.length(); j++) {
                    allAnswers.add(decode(incorrectArray.getString(j)));
                }

                Collections.shuffle(allAnswers);

                questions.add(new Question(
                    questionText, category, difficulty, type, correctAnswer, allAnswers
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return questions;
    }

    private static String decode(String text) {
        if (text == null || text.isEmpty()) return "";
        try {
            byte[] decoded = Base64.decode(text, Base64.DEFAULT);
            return new String(decoded, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}