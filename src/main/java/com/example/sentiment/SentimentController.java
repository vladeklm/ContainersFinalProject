package com.example.sentiment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
public class SentimentController {

    // Базы слов для анализа
    private static final List<String> POSITIVE_WORDS = List.of("good", "great", "awesome", "love", "excellent", "happy", "cool", "super", "best");
    private static final List<String> NEGATIVE_WORDS = List.of("bad", "terrible", "hate", "sad", "awful", "error", "poor", "worst", "fail");

    @GetMapping("/api/sentiment")
    public Map<String, Object> analyze(@RequestParam String text) {
        if (text == null || text.isBlank()) {
            return Map.of("error", "Text cannot be empty");
        }

        // 1. Анализ текста
        double score = 0;
        String lowerText = text.toLowerCase();
        boolean isCapsLock = !lowerText.equals(text) && text.equals(text.toUpperCase()) && text.length() > 1;
        long exclamationCount = text.chars().filter(ch -> ch == '!').count();

        // Подсчет очков за слова
        for (String word : POSITIVE_WORDS) {
            if (lowerText.contains(word)) score += 1.0;
        }
        for (String word : NEGATIVE_WORDS) {
            if (lowerText.contains(word)) score -= 1.0;
        }

        // Бонус за восклицательные знаки
        score += (exclamationCount * 0.5);

        // 2. Определение тональности (Label)
        String sentiment = "neutral";

        if (isCapsLock && score == 0) {
            sentiment = "shouting"; // Просто крик
        } else if (score > 1.0) {
            sentiment = isCapsLock ? "excited" : "positive";
        } else if (score < -0.5) {
            sentiment = isCapsLock ? "angry" : "negative";
        }

        // 3. Генерация случайной уверенности (как в реальных моделях)
        double confidence = 0.65 + (Math.random() * 0.35); // От 0.65 до 1.0

        // 4. Формирование ответа
        return Map.of(
                "text", text,
                "sentiment", sentiment,
                "score", score,
                "confidence", Math.round(confidence * 100.0) / 100.0, // Округление
                "model", "enhanced-nlp-v2",
                "caps_detected", isCapsLock,
                "exclamation_count", exclamationCount
        );
    }
}