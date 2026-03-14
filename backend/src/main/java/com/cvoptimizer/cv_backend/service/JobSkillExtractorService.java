package com.cvoptimizer.cv_backend.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class JobSkillExtractorService {

    private static final Map<String, String> SYNONYMS = Map.ofEntries(
            Map.entry("js", "javascript"),
            Map.entry("ts", "typescript"),
            Map.entry("golang", "go"),
            Map.entry("pgsql", "postgresql"),
            Map.entry("postgres", "postgresql"),
            Map.entry("restful", "rest"),
            Map.entry("rest api", "rest"),
            Map.entry("micro-service", "microservices"),
            Map.entry("micro-services", "microservices"),
            Map.entry("ci", "ci/cd"),
            Map.entry("cd", "ci/cd")
    );

    private static final Set<String> WORD_BANK = new HashSet<>(List.of(
            // Backend core
            "java", "spring", "spring boot", "hibernate",
            "rest", "graphql", "api",
            "microservices", "oop", "design patterns",

            // Databases
            "sql", "postgresql", "mysql", "mongodb", "redis",
            "nosql", "elasticsearch",

            // DevOps
            "docker", "kubernetes",
            "aws", "azure", "gcp",
            "ci/cd", "jenkins",

            // Backend languages
            "python", "go", "c#", ".net",

            // Messaging / streaming
            "kafka", "rabbitmq",

            // Testing
            "testing", "junit", "mockito", "selenium", "playwright",

            // OS / tooling
            "linux", "bash", "git", "github",

            // Security / architecture
            "security", "oauth", "jwt", "concurrency"
    ));

    public Set<String> extractKeywords(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }

        String normalized = normalize(text);

        Set<String> result = new HashSet<>();
        for (String phrase : WORD_BANK) {
            if (containsPhrase(normalized, phrase)) {
                result.add(phrase);
            }
        }

        return result;
    }

    private String normalize(String text) {
        String normalized = text.toLowerCase(Locale.ROOT);

        for (Map.Entry<String, String> e : SYNONYMS.entrySet()) {
            normalized = normalized.replace(e.getKey(), e.getValue());
        }

        normalized = normalized
                .replaceAll("[^a-z0-9\\s/#+.-]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        return normalized;
    }

    private boolean containsPhrase(String text, String phrase) {
        String escaped = Pattern.quote(phrase.toLowerCase(Locale.ROOT));
        String regex = "(^|\\s)" + escaped + "(\\s|$)";
        return Pattern.compile(regex).matcher(text).find() || text.contains(phrase);
    }
}