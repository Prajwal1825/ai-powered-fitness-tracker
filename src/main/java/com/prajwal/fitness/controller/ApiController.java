package com.prajwal.fitness.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prajwal.fitness.model.*;
import com.prajwal.fitness.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiController {
    private final UserRepository users;
    private final GoalRepository goals;
    private final FoodLogRepository foods;
    private final WorkoutLogRepository workouts;

    @Value("${app.gemini.apiKey:}")
    private String geminiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    public ApiController(UserRepository users, GoalRepository goals, FoodLogRepository foods, WorkoutLogRepository workouts) {
        this.users = users;
        this.goals = goals;
        this.foods = foods;
        this.workouts = workouts;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User u) {
        if (users.findByEmail(u.getEmail()).isPresent()) {
            return Map.of("success", false, "message", "Email already registered");
        }
        User saved = users.save(u);
        return Map.of("success", true, "message", "Registered successfully", "user", saved);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        return users.findByEmailAndPassword(body.get("email"), body.get("password"))
                .<Map<String, Object>>map(u -> Map.of("success", true, "message", "Login success", "user", u))
                .orElse(Map.of("success", false, "message", "Invalid email or password"));
    }

    @GetMapping("/dashboard/{userId}")
    public Map<String, Object> dashboard(@PathVariable Long userId) {
        DashboardData data = buildDashboardData(userId);
        return Map.of(
                "target", data.target,
                "consumed", data.consumed,
                "burned", data.burned,
                "remaining", data.remaining,
                "goal", data.goal == null ? new Goal() : data.goal,
                "foods", data.foods,
                "workouts", data.workouts,
                "tips", generateHealthTips(data)
        );
    }

    @GetMapping("/health-tips/{userId}")
    public Map<String, Object> healthTips(@PathVariable Long userId) {
        DashboardData data = buildDashboardData(userId);
        List<String> tips = generateHealthTips(data);
        String mode = (geminiKey == null || geminiKey.isBlank() || geminiKey.contains("YOUR_")) ? "Smart Rule-Based Demo" : "Gemini Ready";
        return Map.of(
                "success", true,
                "mode", mode,
                "summary", "Personalized health tips generated from your calories, food logs, workout logs, and daily goal.",
                "tips", tips
        );
    }

    @PostMapping("/goals")
    public Goal saveGoal(@RequestBody Goal g) {
        return goals.save(g);
    }

    @PostMapping("/foods")
    public FoodLog saveFood(@RequestBody FoodLog f) {
        if (f.getSource() == null) f.setSource("Manual");
        return foods.save(f);
    }

    @PostMapping("/workouts")
    public WorkoutLog saveWorkout(@RequestBody WorkoutLog w) {
        return workouts.save(w);
    }

    @PostMapping("/ai/analyze")
    public Map<String, Object> analyze(@RequestParam Long userId,
                                       @RequestParam("image") MultipartFile image) {
        try {
            if (geminiKey == null || geminiKey.isBlank() || geminiKey.contains("YOUR_")) {
                return Map.of(
                        "success", false,
                        "message", "Gemini API key missing. Add app.gemini.apiKey in application.properties"
                );
            }

            String mimeType = image.getContentType();
            if (mimeType == null || mimeType.isBlank()) {
                mimeType = "image/jpeg";
            }

            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

            // String prompt = """
            //         Analyze this food image and estimate nutrition.
            //         Return ONLY valid JSON. No markdown. No explanation.
            //         Format:
            //         {
            //           "foodName": "actual food name",
            //           "quantity": "estimated quantity",
            //           "calories": 0,
            //           "protein": 0,
            //           "fat": 0,
            //           "tip": "short health tip"
            //         }
            //         """;
            String prompt = """
Analyze this food image and estimate nutrition.

Important rules:
1. Estimate calories only for the visible food quantity.
2. Do not overestimate.
3. If multiple bowls/items are visible, calculate total calories for all visible items.
4. If quantity is unclear, assume small home serving.
5. Return realistic Indian food calories.

Return ONLY valid JSON:
{
  "foodName": "actual food name",
  "quantity": "visible quantity",
  "calories": 0,
  "protein": 0,
  "fat": 0,
  "tip": "short health tip"
}
""";

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt),
                                    Map.of("inline_data", Map.of(
                                            "mime_type", mimeType,
                                            "data", base64Image
                                    ))
                            ))
                    )
            );

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            JsonNode response = restTemplate.postForObject(url, entity, JsonNode.class);

            String aiText = response
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

            aiText = cleanJson(aiText);

            JsonNode aiJson = mapper.readTree(aiText);

            FoodLog f = new FoodLog();
            f.setUserId(userId);
            f.setFoodName(getText(aiJson, "foodName", "Unknown Food"));
            f.setQuantity(getText(aiJson, "quantity", "1 plate"));
            f.setCalories(getInt(aiJson, "calories", 0));
            f.setProtein(getDouble(aiJson, "protein", 0.0));
            f.setFat(getDouble(aiJson, "fat", 0.0));
            f.setSource("Gemini AI");

            foods.save(f);

            return Map.of(
                    "success", true,
                    "message", "Food image analyzed successfully using Gemini AI",
                    "food", f,
                    "tip", getText(aiJson, "tip", "Balance this meal with water and light activity if needed.")
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "Gemini AI error: " + e.getMessage()
            );
        }
    }

    private String cleanJson(String text) {
        return text.replace("```json", "")
                .replace("```", "")
                .trim();
    }

    private String getText(JsonNode node, String field, String defaultValue) {
        return node.has(field) ? node.get(field).asText() : defaultValue;
    }

    private int getInt(JsonNode node, String field, int defaultValue) {
        return node.has(field) ? node.get(field).asInt() : defaultValue;
    }

    private double getDouble(JsonNode node, String field, double defaultValue) {
        return node.has(field) ? node.get(field).asDouble() : defaultValue;
    }

    private DashboardData buildDashboardData(Long userId) {
        List<FoodLog> fs = foods.findByUserIdOrderByIdDesc(userId);
        List<WorkoutLog> ws = workouts.findByUserIdOrderByIdDesc(userId);
        Goal g = goals.findTopByUserIdOrderByIdDesc(userId).orElse(null);
        int consumed = fs.stream().mapToInt(f -> f.getCalories() == null ? 0 : f.getCalories()).sum();
        int burned = ws.stream().mapToInt(w -> w.getCaloriesBurned() == null ? 0 : w.getCaloriesBurned()).sum();
        int target = g == null || g.getCaloriesGoal() == null ? 2000 : g.getCaloriesGoal();
        int remaining = target - consumed + burned;
        return new DashboardData(fs, ws, g, consumed, burned, target, remaining);
    }

    private List<String> generateHealthTips(DashboardData data) {
        List<String> tips = new ArrayList<>();
        if (data.consumed == 0) {
            tips.add("Start by adding your breakfast or first meal so the dashboard can track your calorie intake accurately.");
        } else if (data.consumed > data.target) {
            tips.add("You crossed your daily calorie target. Prefer a lighter next meal with vegetables, fruits, and enough water.");
        } else if (data.consumed < data.target * 0.5) {
            tips.add("Your calorie intake is still low compared to your goal. Add a balanced meal with protein and healthy carbs.");
        } else {
            tips.add("You are progressing well. Keep your next meal balanced with protein, fiber, and moderate carbs.");
        }

        if (data.burned == 0) {
            tips.add("No workout is logged today. Even a 20-minute walk can improve your daily activity score.");
        } else if (data.burned >= 300) {
            tips.add("Great workout progress. Do a short cool-down and drink water to support recovery.");
        } else {
            tips.add("Good start with exercise. Try adding 10–15 more minutes of movement if you have time.");
        }

        if (data.remaining < 0) {
            tips.add("Your remaining calories are negative, so focus on portion control and avoid high-sugar snacks today.");
        } else if (data.remaining > 700) {
            tips.add("You still have enough calories left. Plan a healthy meal instead of skipping food completely.");
        } else {
            tips.add("Your remaining calories are in a controlled range. Maintain consistency for better long-term results.");
        }

        tips.add("AI reminder: This app gives general wellness suggestions only. For medical diet advice, consult a qualified professional.");
        return tips;
    }

    private static class DashboardData {
        List<FoodLog> foods;
        List<WorkoutLog> workouts;
        Goal goal;
        int consumed;
        int burned;
        int target;
        int remaining;

        DashboardData(List<FoodLog> foods, List<WorkoutLog> workouts, Goal goal, int consumed, int burned, int target, int remaining) {
            this.foods = foods;
            this.workouts = workouts;
            this.goal = goal;
            this.consumed = consumed;
            this.burned = burned;
            this.target = target;
            this.remaining = remaining;
        }
    }
}