package com.example.filesimilarity.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class WordProcessorService {

    private static final Pattern ALPHABETIC_PATTERN = Pattern.compile("^[a-zA-Z]+$");

    public Map<String, Integer> extractWordFrequencies(String content) {
        if (content == null || content.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        return Arrays.stream(content.split("\\s+"))
                .filter(word -> !word.isEmpty())
                .filter(this::isValidWord)
                .map(String::toLowerCase)
                .collect(Collectors.toMap(
                        word -> word,
                        word -> 1,
                        Integer::sum));
    }

    public double calculateSimilarityScore(Map<String, Integer> referenceWords,Map<String, Integer> targetWords) {
        if (referenceWords.isEmpty() && targetWords.isEmpty()) {
            return 100.0;
        }
        if (referenceWords.isEmpty() || targetWords.isEmpty()) {
            return 0.0;
        }

        Set<String> referenceSet = referenceWords.keySet();
        Set<String> targetSet = targetWords.keySet();

        if (referenceSet.equals(targetSet)) {
            return 100.0;
        }

        Set<String> intersection = new HashSet<>(referenceSet);
        intersection.retainAll(targetSet);

        if (intersection.isEmpty()) {
            return 0.0;
        }

        double referenceCoverage = (double) intersection.size() / referenceSet.size();

        Set<String> extraWords = new HashSet<>(targetSet);
        extraWords.removeAll(referenceSet);

        double extraWordsPenalty = extraWords.isEmpty()
                ? 1.0
                : Math.max(0.1, 1.0 - (double) extraWords.size() / targetSet.size());

        return referenceCoverage * extraWordsPenalty * 100.0;
    }

    private boolean isValidWord(String word) {
        return ALPHABETIC_PATTERN.matcher(word).matches();
    }
}
