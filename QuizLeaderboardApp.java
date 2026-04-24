import java.net.URI;
import java.net.http.*;
import java.util.*;
import java.util.regex.*;

public class QuizLeaderboardApp {

    static final String REG_NO = "AP23110010242";
    static final String BASE_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";

    public static void main(String[] args) throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        Set<String> processed = new HashSet<>();
        Map<String, Integer> scores = new HashMap<>();

        for (int poll = 0; poll < 10; poll++) {

            String url = BASE_URL + "/quiz/messages?regNo=" + REG_NO + "&poll=" + poll;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            String json = response.body();

            System.out.println("Poll " + poll + " response:");
            System.out.println(json);

            Pattern pattern = Pattern.compile(
                    "\\{\\s*\"roundId\"\\s*:\\s*\"(.*?)\"\\s*,\\s*\"participant\"\\s*:\\s*\"(.*?)\"\\s*,\\s*\"score\"\\s*:\\s*(\\d+)\\s*\\}"
            );

            Matcher matcher = pattern.matcher(json);

            while (matcher.find()) {
                String roundId = matcher.group(1);
                String participant = matcher.group(2);
                int score = Integer.parseInt(matcher.group(3));

                String key = roundId + "_" + participant;

                if (!processed.contains(key)) {
                    processed.add(key);
                    scores.put(participant, scores.getOrDefault(participant, 0) + score);
                }
            }

            if (poll < 9) {
                Thread.sleep(5000);
            }
        }

        List<Map.Entry<String, Integer>> leaderboard = new ArrayList<>(scores.entrySet());

        leaderboard.sort((a, b) -> b.getValue() - a.getValue());

        int totalScore = 0;

        StringBuilder leaderboardJson = new StringBuilder();
        leaderboardJson.append("[");

        for (int i = 0; i < leaderboard.size(); i++) {
            String participant = leaderboard.get(i).getKey();
            int score = leaderboard.get(i).getValue();

            totalScore += score;

            leaderboardJson.append("{\"participant\":\"")
                    .append(participant)
                    .append("\",\"totalScore\":")
                    .append(score)
                    .append("}");

            if (i < leaderboard.size() - 1) {
                leaderboardJson.append(",");
            }
        }

        leaderboardJson.append("]");

        String submitJson = "{"
                + "\"regNo\":\"" + REG_NO + "\","
                + "\"leaderboard\":" + leaderboardJson
                + "}";

        System.out.println("\nFinal Leaderboard:");
        System.out.println(leaderboardJson);

        System.out.println("\nTotal Score:");
        System.out.println(totalScore);

        System.out.println("\nSubmitting final answer...");

        HttpRequest submitRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/quiz/submit"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(submitJson))
                .build();

        HttpResponse<String> submitResponse =
                client.send(submitRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println("\nSubmit Response:");
        System.out.println(submitResponse.body());
    }
}