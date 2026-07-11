package com.cvoptimizer.cv_backend.service;

import com.cvoptimizer.cv_backend.interview.dto.JobDescriptionAnalysis;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class JobSkillExtractorService {

    private static final Map<String, List<String>> WORD_BANK = new LinkedHashMap<>();
    private static final List<String> MUST_HAVE_HINTS = List.of(
            "must have", "required", "requirements", "you have", " חובה ", "נדרש", "required qualifications"
    );
    private static final List<String> NICE_TO_HAVE_HINTS = List.of(
            "nice to have", "preferred", "bonus", "advantage", "יתרון", "preferred qualifications"
    );

    static {
        WORD_BANK.put("Java", List.of("java"));
        WORD_BANK.put("Spring Boot", List.of("spring boot", "springboot"));
        WORD_BANK.put("SQL", List.of("sql", "postgresql", "mysql", "relational database"));
        WORD_BANK.put("PostgreSQL", List.of("postgresql", "postgres"));
        WORD_BANK.put("Docker", List.of("docker", "containers", "containerization"));
        WORD_BANK.put("Redis", List.of("redis", "cache", "caching"));
        WORD_BANK.put("REST", List.of("rest", "rest api", "restful", "api"));
        WORD_BANK.put("Terraform", List.of("terraform"));
        WORD_BANK.put("IaC", List.of("infrastructure as code", "iac", "cloudformation", "aws cdk"));
        WORD_BANK.put("Microservices", List.of("microservices", "distributed systems"));
        WORD_BANK.put("AWS", List.of("aws", "amazon web services", "ec2", "s3", "lambda"));
        WORD_BANK.put("Concurrency", List.of("concurrency", "multithreading", "thread safety", "parallelism"));
        WORD_BANK.put("System Design", List.of("system design", "scalability", "high availability"));
        WORD_BANK.put("Security", List.of("security", "authentication", "authorization", "oauth", "jwt"));
        WORD_BANK.put("Testing", List.of("unit testing", "integration testing", "junit", "testing"));
        WORD_BANK.put("React", List.of("react", "react.js", "frontend"));
        WORD_BANK.put("JavaScript", List.of("javascript", "js", "typescript", "ts"));
        WORD_BANK.put("CI/CD", List.of("ci/cd", "ci cd", "pipeline", "github actions", "jenkins"));
        WORD_BANK.put("Kubernetes", List.of("kubernetes", "k8s"));
        WORD_BANK.put("Message Queues", List.of("kafka", "rabbitmq", "message queue", "messaging"));
        WORD_BANK.put("Python", List.of("python"));
        WORD_BANK.put("ETL", List.of("etl", "elt", "data pipeline", "data pipelines"));
        WORD_BANK.put("Airflow", List.of("airflow", "dag", "dags"));
        WORD_BANK.put("dbt", List.of("dbt", "data build tool"));
        WORD_BANK.put("Data Warehousing", List.of("snowflake", "bigquery", "redshift", "data warehouse"));
        WORD_BANK.put("Data Analytics", List.of("pandas", "a/b testing", "tableau", "power bi", "data analysis",
                "causal inference", "statistical analysis"));
        WORD_BANK.put("Machine Learning", List.of("machine learning", "ml algorithms", "ml models", "deep learning"));
        WORD_BANK.put("NLP", List.of("nlp", "natural language processing"));
        WORD_BANK.put("Generative AI", List.of("generative ai", "genai", "gen ai", "llm", "llms",
                "large language model", "large language models", "prompt engineering",
                "retrieval augmented generation", "vector database", "vector databases",
                "langchain", "agentic", "mlops"));
        WORD_BANK.put("Node.js", List.of("nodejs", "node.js", "node js"));
        WORD_BANK.put("Scala", List.of("scala"));
        WORD_BANK.put("Go", List.of("golang"));
        WORD_BANK.put("Git", List.of("git", "version control"));
        WORD_BANK.put("GitHub", List.of("github"));
        WORD_BANK.put("Scrum", List.of("scrum", "sprint planning", "daily standup", "sprint retrospective"));
        WORD_BANK.put("Kanban", List.of("kanban"));
        // Bare "safe" is far too common a word to match directly (false-positive risk) —
        // only match the specific multi-word phrases that actually signal the framework.
        WORD_BANK.put("SAFe", List.of("scaled agile framework", "scaled agile", "agile release train"));
        WORD_BANK.put("Jira", List.of("jira"));
    }

    public Set<String> extractSkills(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptySet();
        }

        String normalized = normalize(text);
        Set<String> detected = new LinkedHashSet<>();

        for (Map.Entry<String, List<String>> entry : WORD_BANK.entrySet()) {
            String canonical = entry.getKey();
            for (String variant : entry.getValue()) {
                if (containsPhrase(normalized, variant)) {
                    detected.add(canonical);
                    break;
                }
            }
        }

        return detected;
    }

    public JobDescriptionAnalysis analyzeJobDescription(String text) {
        JobDescriptionAnalysis analysis = new JobDescriptionAnalysis();

        if (text == null || text.isBlank()) {
            analysis.setNormalizedJobText("");
            analysis.setRoleHint("general software engineer");
            analysis.setSeniorityHint("mid");
            analysis.setDomainHint("general backend");
            analysis.setPlanningNotes(List.of("Empty job description received."));
            return analysis;
        }

        String normalized = normalize(text);
        analysis.setNormalizedJobText(normalized);

        Set<String> extracted = extractSkills(text);
        analysis.setExtractedSkills(new ArrayList<>(extracted));

        List<String> mustHave = extractSkillsFromSectionHints(normalized, MUST_HAVE_HINTS);
        List<String> niceToHave = extractSkillsFromSectionHints(normalized, NICE_TO_HAVE_HINTS);

        if (mustHave.isEmpty()) {
            mustHave = new ArrayList<>(extracted.stream().limit(5).toList());
        }

        List<String> remainingNice = new ArrayList<>(niceToHave);
        remainingNice.removeAll(mustHave);

        analysis.setMustHaveSkills(mustHave);
        analysis.setNiceToHaveSkills(remainingNice);
        analysis.setRoleHint(extractRoleHint(normalized));
        analysis.setSeniorityHint(extractSeniorityHint(normalized));
        analysis.setDomainHint(extractDomainHint(normalized));
        analysis.setPlanningNotes(buildPlanningNotes(analysis));

        return analysis;
    }

    public String extractRoleHint(String normalizedText) {
        if (normalizedText.contains("backend")) return "backend engineer";
        if (normalizedText.contains("full stack") || normalizedText.contains("fullstack")) return "fullstack engineer";
        if (normalizedText.contains("frontend") || normalizedText.contains("front end")) return "frontend engineer";
        if (normalizedText.contains("platform")) return "platform engineer";
        if (normalizedText.contains("devops")) return "devops engineer";
        if (normalizedText.contains("data engineer")) return "data engineer";
        if (normalizedText.contains("data analyst") || normalizedText.contains("data science")) return "data analyst";
        return "software engineer";
    }

    public String extractSeniorityHint(String normalizedText) {
        if (normalizedText.contains("senior") || normalizedText.contains("lead") || normalizedText.contains("staff")) {
            return "senior";
        }
        if (normalizedText.contains("junior") || normalizedText.contains("entry level") || normalizedText.contains("graduate")) {
            return "junior";
        }
        return "mid";
    }

    public String extractDomainHint(String normalizedText) {
        if (normalizedText.contains("distributed") || normalizedText.contains("scale") || normalizedText.contains("high throughput")) {
            return "distributed systems";
        }
        if (normalizedText.contains("cloud") || normalizedText.contains("aws") || normalizedText.contains("kubernetes")) {
            return "cloud infrastructure";
        }
        if (normalizedText.contains("payments") || normalizedText.contains("transactions")) {
            return "payments";
        }
        if (normalizedText.contains("data pipeline") || normalizedText.contains("etl")) {
            return "data systems";
        }
        return "general backend";
    }

    private List<String> extractSkillsFromSectionHints(String normalized, List<String> hints) {
        boolean containsHint = hints.stream().anyMatch(normalized::contains);
        if (!containsHint) {
            return new ArrayList<>();
        }

        return extractSkills(normalized).stream().limit(5).collect(Collectors.toCollection(ArrayList::new));
    }

    private List<String> buildPlanningNotes(JobDescriptionAnalysis analysis) {
        List<String> notes = new ArrayList<>();
        notes.add("Role hint: " + analysis.getRoleHint());
        notes.add("Seniority hint: " + analysis.getSeniorityHint());
        notes.add("Domain hint: " + analysis.getDomainHint());

        if (!analysis.getMustHaveSkills().isEmpty()) {
            notes.add("Must-have skills detected: " + String.join(", ", analysis.getMustHaveSkills()));
        }

        if (!analysis.getNiceToHaveSkills().isEmpty()) {
            notes.add("Nice-to-have skills detected: " + String.join(", ", analysis.getNiceToHaveSkills()));
        }

        return notes;
    }

    private String normalize(String text) {
        return text.toLowerCase()
                .replace("\n", " ")
                .replace("\r", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean containsPhrase(String text, String phrase) {
        String escaped = Pattern.quote(phrase.toLowerCase());
        return Pattern.compile("\\b" + escaped + "\\b").matcher(text).find();
    }
}