package com.example.Cortex.api;

import com.example.Cortex.util.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fetches trivia questions from the Open Trivia DB API.
 * Step 3 — amount, category, difficulty, type.
 */
public class TriviaApi {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface Callback {
        void onSuccess(String json);
        void onError(String error);
    }

    public void fetchQuestions(int amount, String category, String difficulty,
                               String type, Callback callback) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                String url = buildUrl(amount, category, difficulty, type);

                URL requestUrl = new URL(url);
                connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    callback.onSuccess(sb.toString());
                } else {
                    callback.onError("Server returned code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError("Network error: " + e.getMessage());
            } finally {
                if (connection != null) connection.disconnect();
            }
        });
    }

    /**
     * Builds the full API URL from amount + category + difficulty + type.
     */
    private String buildUrl(int amount, String category, String difficulty, String type) {
        StringBuilder url = new StringBuilder(Constants.BASE_URL);
        url.append("?amount=").append(amount);

        // Category
        if (category != null && !category.equals(Constants.DEFAULT_CATEGORY)) {
            String id = getCategoryId(category);
            if (!id.isEmpty()) {
                url.append("&category=").append(id);
            }
        }

        // Difficulty
        if (difficulty != null && !difficulty.equals(Constants.DEFAULT_DIFFICULTY)) {
            String diff = difficulty.toLowerCase();
            if (diff.equals("easy") || diff.equals("medium") || diff.equals("hard")) {
                url.append("&difficulty=").append(diff);
            }
        }

        // Type
        if (type != null && !type.equals(Constants.DEFAULT_TYPE)) {
            if (type.equals(Constants.LABEL_MULTIPLE)) {
                url.append("&type=").append(Constants.TYPE_MULTIPLE);
            } else if (type.equals(Constants.LABEL_TRUE_FALSE)) {
                url.append("&type=").append(Constants.TYPE_TRUE_FALSE);
            }
        }

        return url.toString();
    }

    private String getCategoryId(String category) {
        switch (category) {
            case "Any Category":       return "";
            case "General Knowledge":  return "9";
            case "Science & Nature":   return "17";
            case "Computers":          return "18";
            case "History":            return "23";
            case "Sports":             return "21";
            case "Music":              return "12";
            default:                   return "";
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}