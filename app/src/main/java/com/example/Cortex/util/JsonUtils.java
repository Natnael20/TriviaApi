package com.example.Cortex.util;

import com.example.Cortex.model.Question;

import org.json.JSONArray;
import org.json.JSONObject;

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

        if (json == null || json.isEmpty()) {
            return questions;
        }

        try {
            JSONObject root = new JSONObject(json);

            int responseCode = root.optInt(KEY_RESPONSE_CODE, -1);
            if (responseCode != 0) {
                return questions;
            }

            JSONArray results = root.getJSONArray(KEY_RESULTS);

            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);

                String questionText  = decodeHtml(item.getString(KEY_QUESTION));
                String category      = decodeHtml(item.getString(KEY_CATEGORY));
                String difficulty    = decodeHtml(item.getString(KEY_DIFFICULTY));
                String type          = decodeHtml(item.getString(KEY_TYPE));
                String correctAnswer = decodeHtml(item.getString(KEY_CORRECT));

                // Build the shuffled answers list: correct + incorrect
                List<String> allAnswers = new ArrayList<>();
                allAnswers.add(correctAnswer);

                JSONArray incorrectArray = item.getJSONArray(KEY_INCORRECT);
                for (int j = 0; j < incorrectArray.length(); j++) {
                    allAnswers.add(decodeHtml(incorrectArray.getString(j)));
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

    /**
     * Decodes common HTML entities used in the OpenTDB responses.
     * e.g. "&quot;" → "\"", "&#039;" → "'", "&amp;" → "&"
     *
     * @param text The raw text possibly containing HTML entities
     * @return The decoded text
     */
    private static String decodeHtml(String text) {
        if (text == null) return "";

        return text
            .replace("&quot;", "\"")
            .replace("&#039;", "'")
            .replace("&apos;", "'")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&ldquo;", "\u201C")   // "
            .replace("&rdquo;", "\u201D")   // "
            .replace("&hellip;", "\u2026")  // …
            .replace("&eacute;", "é");
    }
}