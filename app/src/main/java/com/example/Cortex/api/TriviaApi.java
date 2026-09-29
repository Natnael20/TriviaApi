package com.example.Cortex.api;

import com.example.Cortex.util.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.Cortex.listener.Callback;
import java.util.*;


/**
 * Fetches trivia questions from the Open Trivia DB API.
 * Step 3 — amount, category, difficulty, type.
 */
public class TriviaApi {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Callback callback;

    public void fetchQuestions(int amount, String category, String difficulty,
                               String type, Callback callback) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                String url = buildUrl(amount, category, difficulty, type);

                URL requestUrl =
                 new URL(url);
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
        return CATEGORY_IDS.get(category);
    }

    private static final Map<String, String> CATEGORY_IDS = new HashMap<String, String>() {{
        put("Any Category", "");
        put("General Knowledge", "9");
        put("Books", "10");
        put("Film", "11");
        put("Music", "12");
        put("Musicals & Theatres", "13");
        put("Television", "14");
        put("Video Games", "15");
        put("Board Games", "16");
        put("Science & Nature", "17");
        put("Computers", "18");
        put("Mathematics", "19");
        put("Mythology", "20");
        put("Sports", "21");
        put("Geography", "22");
        put("History", "23");
        put("Politics", "24");
        put("Art", "25");
        put("Celebrities", "26");
        put("Animals", "27");
        put("Vehicles", "28");
        put("Comics", "29");
        put("Gadgets", "30");
        put("Anime & Manga", "31");
        put("Cartoon & Animations", "32");
    }};

    public void shutdown() {
        executor.shutdown();
    }
}