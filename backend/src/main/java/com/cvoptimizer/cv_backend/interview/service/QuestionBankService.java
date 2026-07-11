package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.model.InterviewQuestion;
import com.cvoptimizer.cv_backend.interview.model.QuestionType;
import com.cvoptimizer.cv_backend.interview.persistence.entity.QuestionEntity;
import com.cvoptimizer.cv_backend.interview.persistence.repo.QuestionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class QuestionBankService {

    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper;

    public QuestionBankService(QuestionRepository questionRepository, ObjectMapper objectMapper) {
        this.questionRepository = questionRepository;
        this.objectMapper = objectMapper;
    }

    public List<InterviewQuestion> getAllFromDatabase() {
        List<QuestionEntity> entities = questionRepository.findByStatus("ACTIVE");
        List<InterviewQuestion> result = new ArrayList<>(entities.size());
        for (QuestionEntity e : entities) {
            try {
                result.add(toInterviewQuestion(e));
            } catch (Exception ex) {
                System.err.println("[QuestionBankService] Skipping question " + e.getId() + ": " + ex.getMessage());
            }
        }
        return result;
    }

    private InterviewQuestion toInterviewQuestion(QuestionEntity e) throws Exception {
        QuestionType type = QuestionType.valueOf(e.getType());
        Set<String> tags = e.parsedTags();
        Set<String> keywords = e.parsedKeywords();

        InterviewQuestion question = switch (type) {
            case MCQ -> {
                List<String> options = e.getOptions() != null
                        ? objectMapper.readValue(e.getOptions(), new TypeReference<List<String>>() {})
                        : List.of();
                yield new InterviewQuestion(e.getId(), e.getText(), tags, e.isCritical(),
                        options, e.getCorrectOptionIndex() != null ? e.getCorrectOptionIndex() : 0);
            }
            case CODE -> new InterviewQuestion(e.getId(), e.getText(), tags, e.isCritical(),
                    e.getStarterCode() != null ? e.getStarterCode() : "", keywords);
            default -> new InterviewQuestion(e.getId(), e.getText(), tags, e.isCritical(), keywords);
        };
        if (e.getDifficulty() != null) {
            question.setDifficulty(e.getDifficulty());
        }
        return question;
    }

    // Used only by QuestionDbSeeder on first startup to populate the DB.
    // After that, use getAllFromDatabase() for all runtime calls.
    public List<InterviewQuestion> getBackendJuniorBank() {
        List<InterviewQuestion> q = new ArrayList<>();

        // =========================
        // REST / API / HTTP / Security
        // =========================
        q.add(new InterviewQuestion("Q1",
                "Explain what an HTTP REST API is. What are common HTTP methods and what do they mean?",
                Set.of("rest", "api"), true, Set.of("get", "post", "put", "delete")));

        q.add(new InterviewQuestion("Q2",
                "What is the difference between PUT and PATCH? When would you use each?",
                Set.of("rest", "api"), true, Set.of("put", "patch")));

        q.add(new InterviewQuestion("Q3",
                "What does it mean for an HTTP method to be idempotent? Give examples.",
                Set.of("rest", "api"), true, Set.of("idempotent", "same")));

        q.add(new InterviewQuestion("Q4",
                "Explain HTTP status codes: 200, 201, 204, 400, 401, 403, 404, 409, 500.",
                Set.of("rest", "api"), true, Set.of("status", "code")));

        q.add(new InterviewQuestion("Q5",
                "What is CORS and why does it exist? How do you handle it in a backend?",
                Set.of("rest", "api"), true, Set.of("cors", "origin")));

        q.add(new InterviewQuestion("Q6",
                "What is content negotiation (Accept / Content-Type)?",
                Set.of("rest", "api"), false, Set.of("content-type", "accept")));

        q.add(new InterviewQuestion("Q7",
                "Explain the difference between authentication and authorization.",
                Set.of("api"), true, Set.of("authentication", "authorization")));

        q.add(new InterviewQuestion("Q8",
                "What is JWT? What are the risks if you store sensitive data inside it?",
                Set.of("api", "security"), true, Set.of("jwt", "token")));

        q.add(new InterviewQuestion("Q9",
                "What is OAuth2 at a high level? What problem does it solve?",
                Set.of("api", "security"), false, Set.of("oauth", "token")));

        q.add(new InterviewQuestion("Q10",
                "How would you design API pagination? (limit/offset vs cursor)",
                Set.of("rest", "api"), true, Set.of("pagination", "cursor")));

        q.add(new InterviewQuestion("Q11",
                "What is rate limiting and why is it important?",
                Set.of("api"), false, Set.of("rate", "limit")));

        q.add(new InterviewQuestion("Q12",
                "Explain API versioning strategies (URL versioning, headers, etc.).",
                Set.of("api"), false, Set.of("version", "backward")));

        q.add(new InterviewQuestion("Q13",
                "What is a 409 Conflict usually used for? Give an example.",
                Set.of("rest", "api"), false, Set.of("409", "conflict")));

        q.add(new InterviewQuestion("Q14",
                "What is a DTO and why do we use it instead of returning entities directly?",
                Set.of("api", "design patterns"), true, Set.of("dto", "entity")));

        q.add(new InterviewQuestion("Q15",
                "What is OpenAPI/Swagger and how does it help teams?",
                Set.of("api"), false, Set.of("swagger", "documentation")));

        q.add(new InterviewQuestion("Q16",
                "Explain the difference between synchronous and asynchronous APIs.",
                Set.of("api"), false, Set.of("synchronous", "asynchronous")));

        q.add(new InterviewQuestion("Q17",
                "When would you return 204 No Content vs 200 OK?",
                Set.of("rest"), false, Set.of("204", "200")));

        q.add(new InterviewQuestion("Q18",
                "What is an API gateway in microservices and what does it typically do?",
                Set.of("api", "microservices"), false, Set.of("gateway", "routing")));

        q.add(new InterviewQuestion("Q19",
                "GraphQL: what problem does it solve compared to REST?",
                Set.of("graphql", "api"), false, Set.of("overfetching", "query")));

        q.add(new InterviewQuestion("Q20",
                "What are common REST API security mistakes you’ve seen or heard about?",
                Set.of("api", "security"), false, Set.of("validation", "authorization")));

        q.add(new InterviewQuestion("Q21",
                "Explain request validation. Where should it happen and why?",
                Set.of("api"), true, Set.of("validation", "input")));

        q.add(new InterviewQuestion("Q22",
                "What is CSRF and when is it relevant?",
                Set.of("api", "security"), false, Set.of("csrf", "cookie")));

        q.add(new InterviewQuestion("Q23",
                "What is XSS? Is it mainly a frontend or backend problem?",
                Set.of("api", "security"), false, Set.of("xss", "sanitize")));

        q.add(new InterviewQuestion("Q24",
                "Explain the difference between 401 and 403.",
                Set.of("api", "security"), true, Set.of("401", "403")));

        q.add(new InterviewQuestion("Q25",
                "How would you implement structured error responses (errorCode/message/details) in APIs?",
                Set.of("rest", "api"), false, Set.of("error", "response")));

        // =========================
        // Java Core
        // =========================
        q.add(new InterviewQuestion("Q26",
                "What is the difference between JDK, JRE, and JVM?",
                Set.of("java"), true, Set.of("jvm", "jre")));

        q.add(new InterviewQuestion("Q27",
                "Explain how garbage collection works in Java at a high level.",
                Set.of("java"), true, Set.of("garbage", "memory")));

        q.add(new InterviewQuestion("Q28",
                "What is the difference between == and equals() in Java?",
                Set.of("java"), true, Set.of("equals", "reference")));

        q.add(new InterviewQuestion("Q29",
                "Why must you override hashCode() when you override equals()?",
                Set.of("java"), true, Set.of("hashcode", "equals")));

        q.add(new InterviewQuestion("Q30",
                "Explain how HashMap works internally. What happens with collisions?",
                Set.of("java"), true, Set.of("hashmap", "collision")));

        q.add(new InterviewQuestion("Q31",
                "What’s the difference between ArrayList and LinkedList? When would you prefer each?",
                Set.of("java"), true, Set.of("arraylist", "linkedlist")));

        q.add(new InterviewQuestion("Q32",
                "What are checked vs unchecked exceptions?",
                Set.of("java"), true, Set.of("checked", "runtime")));

        q.add(new InterviewQuestion("Q33",
                "Explain try-with-resources. Why is it useful?",
                Set.of("java"), false, Set.of("try", "resource")));

        q.add(new InterviewQuestion("Q34",
                "What is immutability? Give a benefit of immutable objects.",
                Set.of("java"), false, Set.of("immutable", "thread")));

        q.add(new InterviewQuestion("Q35",
                "Explain generics in Java. What problem do they solve?",
                Set.of("java"), true, Set.of("generics", "type")));

        q.add(new InterviewQuestion("Q36",
                "What is autoboxing/unboxing? What’s a common pitfall?",
                Set.of("java"), false, Set.of("primitive", "wrapper")));

        q.add(new InterviewQuestion("Q37",
                "Explain the difference between interface and abstract class.",
                Set.of("java"), true, Set.of("interface", "abstract")));

        q.add(new InterviewQuestion("Q38",
                "What does the 'final' keyword mean on a variable / method / class?",
                Set.of("java"), false, Set.of("final", "override")));

        q.add(new InterviewQuestion("Q39",
                "What is the difference between String, StringBuilder, and StringBuffer?",
                Set.of("java"), true, Set.of("stringbuilder", "immutable")));

        q.add(new InterviewQuestion("Q40",
                "What are Java Streams and when would you use them?",
                Set.of("java"), false, Set.of("stream", "pipeline")));

        q.add(new InterviewQuestion("Q41",
                "Explain Optional: when is it helpful and when can it be overused?",
                Set.of("java"), false, Set.of("optional", "null")));

        q.add(new InterviewQuestion("Q42",
                "What is the difference between stack and heap memory?",
                Set.of("java"), true, Set.of("stack", "heap")));

        q.add(new InterviewQuestion("Q43",
                "Explain Big-O time complexity with an example from collections.",
                Set.of("java"), false, Set.of("big-o", "complexity")));

        q.add(new InterviewQuestion("Q44",
                "What is a race condition? Give an example.",
                Set.of("java", "concurrency"), true, Set.of("race", "condition")));

        q.add(new InterviewQuestion("Q45",
                "What is synchronized? What problem does it solve?",
                Set.of("java", "concurrency"), true, Set.of("synchronized", "lock")));

        q.add(new InterviewQuestion("Q46",
                "Difference between volatile and synchronized?",
                Set.of("java", "concurrency"), false, Set.of("volatile", "visibility")));

        q.add(new InterviewQuestion("Q47",
                "What is a deadlock? How can you reduce the risk of deadlocks?",
                Set.of("java", "concurrency"), true, Set.of("deadlock", "lock")));

        q.add(new InterviewQuestion("Q48",
                "Explain ExecutorService and thread pools. Why use them?",
                Set.of("java", "concurrency"), false, Set.of("thread", "pool")));

        q.add(new InterviewQuestion("Q49",
                "What is the difference between Comparable and Comparator?",
                Set.of("java"), false, Set.of("comparator", "sort")));

        q.add(new InterviewQuestion("Q50",
                "What is a lambda expression in Java? Where is it used commonly?",
                Set.of("java"), false, Set.of("lambda", "functional")));

        q.add(new InterviewQuestion("Q51",
                "Explain the difference between fail-fast and fail-safe iterators.",
                Set.of("java"), false, Set.of("iterator", "concurrent")));

        q.add(new InterviewQuestion("Q52",
                "What is serialization? When might it be needed?",
                Set.of("java", "api"), false, Set.of("serialize", "json")));

        q.add(new InterviewQuestion("Q53",
                "What is reflection? Why can it be dangerous or slow?",
                Set.of("java"), false, Set.of("reflection", "runtime")));

        q.add(new InterviewQuestion("Q54",
                "Explain classloading at a high level (just the concept).",
                Set.of("java"), false, Set.of("classloader", "load")));

        q.add(new InterviewQuestion("Q55",
                "What is dependency management? What is Maven/Gradle used for?",
                Set.of("java"), false, Set.of("maven", "dependency")));

        q.add(new InterviewQuestion("Q56",
                "Explain how exceptions affect control flow. When should you not use exceptions?",
                Set.of("java"), false, Set.of("exception", "control")));

        q.add(new InterviewQuestion("Q57",
                "What are common reasons for memory leaks in Java applications?",
                Set.of("java"), false, Set.of("memory", "reference")));

        q.add(new InterviewQuestion("Q58",
                "Explain difference between composition and inheritance. When is composition preferred?",
                Set.of("java", "oop"), true, Set.of("composition", "inheritance")));

        q.add(new InterviewQuestion("Q59",
                "What is the difference between static and instance members?",
                Set.of("java"), true, Set.of("static", "instance")));

        q.add(new InterviewQuestion("Q60",
                "What is an enum and why use it?",
                Set.of("java"), false, Set.of("enum", "constant")));

        // =========================
        // OOP / Design Patterns
        // =========================
        q.add(new InterviewQuestion("Q61",
                "Explain OOP principles: encapsulation, inheritance, polymorphism. Give an example.",
                Set.of("oop"), true, Set.of("encapsulation", "polymorphism")));

        q.add(new InterviewQuestion("Q62",
                "What is SOLID? Name at least 3 principles and explain one.",
                Set.of("oop", "design patterns"), false, Set.of("responsibility", "principle")));

        q.add(new InterviewQuestion("Q63",
                "What is dependency inversion principle (DIP)?",
                Set.of("design patterns", "oop"), false, Set.of("dependency", "abstraction")));

        q.add(new InterviewQuestion("Q64",
                "Explain the Strategy pattern with a real example.",
                Set.of("design patterns"), false, Set.of("strategy", "interface")));

        q.add(new InterviewQuestion("Q65",
                "Explain the Factory pattern. Why is it useful?",
                Set.of("design patterns"), false, Set.of("factory", "create")));

        q.add(new InterviewQuestion("Q66",
                "Explain Singleton pattern. Why can it be problematic?",
                Set.of("design patterns"), true, Set.of("singleton", "global")));

        q.add(new InterviewQuestion("Q67",
                "Explain Observer pattern. Where do you see it in real systems?",
                Set.of("design patterns"), false, Set.of("observer", "event")));

        q.add(new InterviewQuestion("Q68",
                "Explain Builder pattern and when it helps.",
                Set.of("design patterns"), false, Set.of("builder", "fluent")));

        q.add(new InterviewQuestion("Q69",
                "What is coupling and cohesion? Which one do you want high/low?",
                Set.of("oop"), true, Set.of("coupling", "cohesion")));

        q.add(new InterviewQuestion("Q70",
                "What is an anti-pattern? Give an example (God object, etc.).",
                Set.of("design patterns"), false, Set.of("god", "coupling")));

        q.add(new InterviewQuestion("Q71",
                "What is dependency injection as a design principle (not just Spring feature)?",
                Set.of("design patterns", "spring"), true, Set.of("dependency", "inject")));

        q.add(new InterviewQuestion("Q72",
                "Explain Liskov substitution principle (LSP) with a simple example.",
                Set.of("oop"), false, Set.of("liskov", "substitution")));

        q.add(new InterviewQuestion("Q73",
                "Explain interface segregation principle (ISP).",
                Set.of("oop"), false, Set.of("interface", "segregation")));

        q.add(new InterviewQuestion("Q74",
                "Explain open/closed principle (OCP).",
                Set.of("oop"), false, Set.of("open", "closed")));

        q.add(new InterviewQuestion("Q75",
                "When would you use inheritance, and when would you avoid it?",
                Set.of("oop"), true, Set.of("inheritance", "composition")));

        q.add(new InterviewQuestion("Q76",
                "What is a layered architecture? Typical backend layers?",
                Set.of("design patterns", "api"), true, Set.of("controller", "service")));

        q.add(new InterviewQuestion("Q77",
                "What is hexagonal/clean architecture at a high level?",
                Set.of("design patterns"), false, Set.of("domain", "adapter")));

        q.add(new InterviewQuestion("Q78",
                "Explain why 'fat controllers' are a problem and what to do instead.",
                Set.of("design patterns", "spring"), false, Set.of("service", "logic")));

        q.add(new InterviewQuestion("Q79",
                "What is the difference between an entity and a value object?",
                Set.of("design patterns"), false, Set.of("entity", "value")));

        q.add(new InterviewQuestion("Q80",
                "Explain caching as a pattern: where can you cache and what are pitfalls?",
                Set.of("design patterns", "redis"), false, Set.of("cache", "stale")));

        // =========================
        // Spring / Spring Boot
        // =========================
        q.add(new InterviewQuestion("Q81",
                "In Spring Boot, what is Dependency Injection and why is it useful?",
                Set.of("spring", "spring boot"), true, Set.of("dependency", "inject")));

        q.add(new InterviewQuestion("Q82",
                "What is IoC (Inversion of Control) and what is the Spring container?",
                Set.of("spring"), true, Set.of("container", "control")));

        q.add(new InterviewQuestion("Q83",
                "Explain @Component, @Service, @Repository. Why separate them?",
                Set.of("spring"), true, Set.of("component", "repository")));

        q.add(new InterviewQuestion("Q84",
                "Explain constructor injection vs field injection. Which is preferred and why?",
                Set.of("spring"), true, Set.of("constructor", "inject")));

        q.add(new InterviewQuestion("Q85",
                "What is a Spring Bean? How is it created?",
                Set.of("spring"), true, Set.of("bean", "container")));

        q.add(new InterviewQuestion("Q86",
                "What is bean scope (singleton/prototype/request)?",
                Set.of("spring"), false, Set.of("scope", "singleton")));

        q.add(new InterviewQuestion("Q87",
                "What does @Configuration do? What does @Bean do?",
                Set.of("spring"), false, Set.of("configuration", "bean")));

        q.add(new InterviewQuestion("Q88",
                "What is @RestController and how is it different from @Controller?",
                Set.of("spring boot", "rest"), true, Set.of("restcontroller", "responsebody")));

        q.add(new InterviewQuestion("Q89",
                "How do you handle exceptions in Spring REST APIs? (e.g., @ControllerAdvice)",
                Set.of("spring", "rest"), true, Set.of("controlleradvice", "exception")));

        q.add(new InterviewQuestion("Q90",
                "What is validation in Spring (Bean Validation)? How do you validate request bodies?",
                Set.of("spring", "api"), true, Set.of("validation", "valid")));

        q.add(new InterviewQuestion("Q91",
                "What does @Transactional do? What does it NOT do?",
                Set.of("spring", "sql"), true, Set.of("transactional", "transaction")));

        q.add(new InterviewQuestion("Q92",
                "How do Spring profiles work? Why use them?",
                Set.of("spring boot"), false, Set.of("profile", "environment")));

        q.add(new InterviewQuestion("Q93",
                "What is Spring Boot auto-configuration?",
                Set.of("spring boot"), true, Set.of("auto", "configuration")));

        q.add(new InterviewQuestion("Q94",
                "Explain how Spring Boot application.properties/yml is used and overridden.",
                Set.of("spring boot"), false, Set.of("properties", "override")));

        q.add(new InterviewQuestion("Q95",
                "What is Spring Actuator and what is it used for?",
                Set.of("spring boot"), false, Set.of("actuator", "metrics")));

        q.add(new InterviewQuestion("Q96",
                "How would you implement pagination in Spring Data (Pageable)?",
                Set.of("spring", "sql"), false, Set.of("pageable", "page")));

        q.add(new InterviewQuestion("Q97",
                "Explain REST controllers best practices: DTOs, validation, service layer.",
                Set.of("spring", "rest"), true, Set.of("dto", "service")));

        q.add(new InterviewQuestion("Q98",
                "What is @PathVariable vs @RequestParam?",
                Set.of("spring", "rest"), true, Set.of("pathvariable", "requestparam")));

        q.add(new InterviewQuestion("Q99",
                "How do you enable CORS in Spring Boot safely?",
                Set.of("spring boot", "rest"), false, Set.of("cors", "origin")));

        q.add(new InterviewQuestion("Q100",
                "What is the difference between filters and interceptors in Spring?",
                Set.of("spring"), false, Set.of("filter", "interceptor")));

        q.add(new InterviewQuestion("Q101",
                "How do you do logging in Spring Boot? What should you avoid logging?",
                Set.of("spring boot", "logging"), false, Set.of("log", "sensitive")));

        q.add(new InterviewQuestion("Q102",
                "Explain how Spring Boot starts: main method, auto config, embedded server.",
                Set.of("spring boot"), false, Set.of("embedded", "server")));

        q.add(new InterviewQuestion("Q103",
                "What is a circular dependency in Spring and how do you fix it?",
                Set.of("spring"), true, Set.of("circular", "dependency")));

        q.add(new InterviewQuestion("Q104",
                "What does @Qualifier solve?",
                Set.of("spring"), true, Set.of("qualifier", "bean")));

        q.add(new InterviewQuestion("Q105",
                "How do you structure a Spring Boot project package layout?",
                Set.of("spring boot"), false, Set.of("controller", "service")));

        q.add(new InterviewQuestion("Q106",
                "How do you implement file upload endpoint in Spring? What validations are important?",
                Set.of("spring", "api"), false, Set.of("multipart", "validation")));

        q.add(new InterviewQuestion("Q107",
                "What is Spring Security used for? Give basic concepts.",
                Set.of("spring", "security"), false, Set.of("security", "filter")));

        q.add(new InterviewQuestion("Q108",
                "What is CSRF protection in Spring Security and when do you disable it?",
                Set.of("spring", "security"), false, Set.of("csrf", "token")));

        q.add(new InterviewQuestion("Q109",
                "How do you protect endpoints with roles/authorities in Spring Security?",
                Set.of("spring", "security"), false, Set.of("role", "authorize")));

        q.add(new InterviewQuestion("Q110",
                "What is method-level security (e.g., @PreAuthorize)?",
                Set.of("spring", "security"), false, Set.of("preauthorize", "role")));

        // =========================
        // Hibernate / JPA / Spring Data
        // =========================
        q.add(new InterviewQuestion("Q111",
                "What is ORM? Why use JPA/Hibernate?",
                Set.of("hibernate", "sql"), true, Set.of("orm", "mapping")));

        q.add(new InterviewQuestion("Q112",
                "Explain the difference between EntityManager and Repository (Spring Data).",
                Set.of("hibernate", "spring"), false, Set.of("entitymanager", "repository")));

        q.add(new InterviewQuestion("Q113",
                "What is lazy loading vs eager loading? What are common pitfalls?",
                Set.of("hibernate"), true, Set.of("lazy", "eager")));

        q.add(new InterviewQuestion("Q114",
                "What is the N+1 query problem? How do you detect and solve it?",
                Set.of("hibernate", "sql"), true, Set.of("n+1", "join")));

        q.add(new InterviewQuestion("Q115",
                "Explain @OneToMany / @ManyToOne and ownership of relationships.",
                Set.of("hibernate"), true, Set.of("manytoone", "mappedby")));

        q.add(new InterviewQuestion("Q116",
                "What is a transaction in JPA context? Why is it important?",
                Set.of("hibernate", "sql"), true, Set.of("transaction", "commit")));

        q.add(new InterviewQuestion("Q117",
                "What is optimistic locking? How do you implement it?",
                Set.of("hibernate", "sql"), false, Set.of("optimistic", "version")));

        q.add(new InterviewQuestion("Q118",
                "What is pessimistic locking? When would you use it?",
                Set.of("hibernate", "sql"), false, Set.of("pessimistic", "lock")));

        q.add(new InterviewQuestion("Q119",
                "What is the first-level cache in Hibernate?",
                Set.of("hibernate"), false, Set.of("session", "entity")));

        q.add(new InterviewQuestion("Q120",
                "Explain cascading (CascadeType). When is it dangerous?",
                Set.of("hibernate"), false, Set.of("cascade", "remove")));

        q.add(new InterviewQuestion("Q121",
                "What is a JPQL query? How is it different from native SQL?",
                Set.of("hibernate", "sql"), false, Set.of("jpql", "entity")));

        q.add(new InterviewQuestion("Q122",
                "How would you implement pagination with JPA and why do joins complicate it?",
                Set.of("hibernate", "sql"), false, Set.of("pagination", "join")));

        q.add(new InterviewQuestion("Q123",
                "Explain what a persistence context is.",
                Set.of("hibernate"), false, Set.of("persistence", "context")));

        q.add(new InterviewQuestion("Q124",
                "What is the difference between save() and saveAndFlush() in Spring Data?",
                Set.of("hibernate", "spring"), false, Set.of("flush", "transaction")));

        q.add(new InterviewQuestion("Q125",
                "What is an Entity? What rules must it follow?",
                Set.of("hibernate"), true, Set.of("entity", "id")));

        // =========================
        // SQL / PostgreSQL / MySQL
        // =========================
        q.add(new InterviewQuestion("Q126",
                "What is the difference between SQL INNER JOIN and LEFT JOIN? Give an example use-case.",
                Set.of("sql"), true, Set.of("join", "left")));

        q.add(new InterviewQuestion("Q127",
                "Explain database indexing: why it helps, and what is the trade-off?",
                Set.of("sql"), true, Set.of("index", "tradeoff")));

        q.add(new InterviewQuestion("Q128",
                "Explain transactions and ACID. Why do they matter?",
                Set.of("sql"), true, Set.of("acid", "transaction")));

        q.add(new InterviewQuestion("Q129",
                "What is a primary key vs unique constraint?",
                Set.of("sql"), true, Set.of("primary", "unique")));

        q.add(new InterviewQuestion("Q130",
                "What is a foreign key and why is it useful?",
                Set.of("sql"), true, Set.of("foreign", "reference")));

        q.add(new InterviewQuestion("Q131",
                "What is normalization? Why do we normalize tables?",
                Set.of("sql"), false, Set.of("normalization", "redundancy")));

        q.add(new InterviewQuestion("Q132",
                "What is denormalization and when would you do it?",
                Set.of("sql"), false, Set.of("denormalization", "performance")));

        q.add(new InterviewQuestion("Q133",
                "Explain GROUP BY and HAVING with an example scenario.",
                Set.of("sql"), true, Set.of("group", "having")));

        q.add(new InterviewQuestion("Q134",
                "What is the difference between WHERE and HAVING?",
                Set.of("sql"), true, Set.of("where", "having")));

        q.add(new InterviewQuestion("Q135",
                "Explain transactions isolation levels (read committed, repeatable read, serializable).",
                Set.of("sql"), false, Set.of("isolation", "serializable")));

        q.add(new InterviewQuestion("Q136",
                "What is a deadlock in databases? How can it happen?",
                Set.of("sql"), true, Set.of("deadlock", "transaction")));

        q.add(new InterviewQuestion("Q137",
                "What is an execution plan and why is it important for performance?",
                Set.of("sql", "postgresql"), false, Set.of("plan", "index")));

        q.add(new InterviewQuestion("Q138",
                "Explain the difference between DELETE, TRUNCATE, and DROP.",
                Set.of("sql"), false, Set.of("delete", "truncate")));

        q.add(new InterviewQuestion("Q139",
                "What is an upsert? How do you do it in PostgreSQL?",
                Set.of("sql", "postgresql"), false, Set.of("upsert", "conflict")));

        q.add(new InterviewQuestion("Q140",
                "What is a composite index and when is it useful?",
                Set.of("sql"), false, Set.of("composite", "index")));

        q.add(new InterviewQuestion("Q141",
                "Explain why SELECT * is often discouraged in production code.",
                Set.of("sql"), false, Set.of("select", "columns")));

        q.add(new InterviewQuestion("Q142",
                "What is the difference between COUNT(*) and COUNT(column)?",
                Set.of("sql"), false, Set.of("count", "null")));

        q.add(new InterviewQuestion("Q143",
                "Explain database migrations. Why are they important?",
                Set.of("sql"), false, Set.of("migration", "schema")));

        q.add(new InterviewQuestion("Q144",
                "What are indexes’ downsides on write-heavy workloads?",
                Set.of("sql"), true, Set.of("index", "write")));

        q.add(new InterviewQuestion("Q145",
                "PostgreSQL vs MySQL: give one practical difference you know.",
                Set.of("postgresql", "mysql"), false, Set.of("postgresql", "mysql")));

        // =========================
        // NoSQL / MongoDB / Redis / Elasticsearch
        // =========================
        q.add(new InterviewQuestion("Q146",
                "What is NoSQL and why would you choose it over relational databases?",
                Set.of("nosql"), true, Set.of("schema", "scale")));

        q.add(new InterviewQuestion("Q147",
                "MongoDB: what is a document and how does it map to JSON?",
                Set.of("mongodb", "nosql"), true, Set.of("document", "json")));

        q.add(new InterviewQuestion("Q148",
                "MongoDB vs SQL: what do you lose and what do you gain?",
                Set.of("mongodb", "sql"), true, Set.of("join", "schema")));

        q.add(new InterviewQuestion("Q149",
                "What is eventual consistency? When is it acceptable?",
                Set.of("nosql"), false, Set.of("eventual", "consistency")));

        q.add(new InterviewQuestion("Q150",
                "What is Redis commonly used for?",
                Set.of("redis"), true, Set.of("cache", "ttl")));

        q.add(new InterviewQuestion("Q151",
                "Explain Redis TTL and why it’s useful.",
                Set.of("redis"), false, Set.of("ttl", "expire")));

        q.add(new InterviewQuestion("Q152",
                "What is a cache stampede and how can you reduce it?",
                Set.of("redis"), false, Set.of("cache", "stampede")));

        q.add(new InterviewQuestion("Q153",
                "What is a cache invalidation problem? Why is it hard?",
                Set.of("redis"), false, Set.of("invalidation", "stale")));

        q.add(new InterviewQuestion("Q154",
                "Elasticsearch: what is it designed for and what is a common use-case?",
                Set.of("elasticsearch"), true, Set.of("search", "index")));

        q.add(new InterviewQuestion("Q155",
                "Explain the difference between a database index and an Elasticsearch index.",
                Set.of("elasticsearch", "sql"), false, Set.of("elasticsearch", "index")));

        q.add(new InterviewQuestion("Q156",
                "When would you not use Redis (or when it would be risky)?",
                Set.of("redis"), false, Set.of("memory", "persistence")));

        q.add(new InterviewQuestion("Q157",
                "What is sharding/partitioning in databases at a high level?",
                Set.of("sql", "nosql"), false, Set.of("shard", "partition")));

        q.add(new InterviewQuestion("Q158",
                "Explain CAP theorem briefly and why it matters in distributed systems.",
                Set.of("nosql", "microservices"), false, Set.of("cap", "consistency")));

        q.add(new InterviewQuestion("Q159",
                "What is a read replica and why use it?",
                Set.of("sql"), false, Set.of("replica", "read")));

        q.add(new InterviewQuestion("Q160",
                "What is write-ahead logging (WAL) conceptually? (high-level)",
                Set.of("postgresql"), false, Set.of("wal", "log")));

        // =========================
        // Concurrency / Backend fundamentals
        // =========================
        q.add(new InterviewQuestion("Q161",
                "What is concurrency? How does it differ from sequential execution?",
                Set.of("concurrency"), true, Set.of("concurrent", "threads")));

        q.add(new InterviewQuestion("Q162",
                "Difference between concurrency and parallelism?",
                Set.of("concurrency"), false, Set.of("parallel", "threads")));

        q.add(new InterviewQuestion("Q163",
                "What is a thread-safe collection? Give an example in Java.",
                Set.of("concurrency", "java"), false, Set.of("thread", "safe")));

        q.add(new InterviewQuestion("Q164",
                "What is a critical section?",
                Set.of("concurrency"), false, Set.of("critical", "lock")));

        q.add(new InterviewQuestion("Q165",
                "What is a semaphore or mutex? (conceptually)",
                Set.of("concurrency"), false, Set.of("semaphore", "mutex")));

        q.add(new InterviewQuestion("Q166",
                "Explain optimistic vs pessimistic concurrency control.",
                Set.of("concurrency", "sql"), false, Set.of("optimistic", "pessimistic")));

        q.add(new InterviewQuestion("Q167",
                "What is backpressure? Where might you see it (queues/streaming)?",
                Set.of("microservices", "kafka"), false, Set.of("backpressure", "queue")));

        q.add(new InterviewQuestion("Q168",
                "What is a timeout and why must backends enforce timeouts?",
                Set.of("api", "microservices"), true, Set.of("timeout", "retry")));

        q.add(new InterviewQuestion("Q169",
                "What is retry and why can it be dangerous without idempotency?",
                Set.of("api", "microservices"), true, Set.of("retry", "idempotent")));

        q.add(new InterviewQuestion("Q170",
                "What is circuit breaker pattern? Why do microservices use it?",
                Set.of("microservices", "design patterns"), false, Set.of("circuit", "breaker")));

        // =========================
        // Microservices (practical)
        // =========================
        q.add(new InterviewQuestion("Q171",
                "What are microservices? Give 2 benefits and 2 trade-offs.",
                Set.of("microservices"), true, Set.of("independent", "deployment")));

        q.add(new InterviewQuestion("Q172",
                "What is service discovery? Why do you need it?",
                Set.of("microservices"), false, Set.of("discovery", "service")));

        q.add(new InterviewQuestion("Q173",
                "What is distributed tracing and why is it useful?",
                Set.of("microservices"), false, Set.of("tracing", "correlation")));

        q.add(new InterviewQuestion("Q174",
                "Explain the difference between synchronous HTTP communication and messaging in microservices.",
                Set.of("microservices", "kafka"), true, Set.of("sync", "async")));

        q.add(new InterviewQuestion("Q175",
                "Two microservices each own their own database. Service A finishes but Service B fails. How do you handle this inconsistency?",
                Set.of("microservices"), false, Set.of("saga", "compensation")));

        q.add(new InterviewQuestion("Q176",
                "What is the Saga pattern at a high level?",
                Set.of("microservices", "design patterns"), false, Set.of("saga", "compensation")));

        q.add(new InterviewQuestion("Q177",
                "What is a 'database per service' idea and why is it recommended?",
                Set.of("microservices"), false, Set.of("database", "service")));

        q.add(new InterviewQuestion("Q178",
                "What is an API gateway and what problems does it solve?",
                Set.of("microservices", "api"), false, Set.of("gateway", "auth")));

        q.add(new InterviewQuestion("Q179",
                "What is a contract between services? How can you avoid breaking clients?",
                Set.of("microservices", "api"), false, Set.of("contract", "backward")));

        q.add(new InterviewQuestion("Q180",
                "How would you roll out a change safely in production (blue/green, canary)?",
                Set.of("microservices", "ci/cd"), false, Set.of("canary", "rollback")));

        // =========================
        // Docker / Kubernetes / CI/CD / Cloud
        // =========================
        q.add(new InterviewQuestion("Q181",
                "What is a Docker image vs a Docker container?",
                Set.of("docker"), true, Set.of("image", "container")));

        q.add(new InterviewQuestion("Q182",
                "What is a Dockerfile and what are common instructions inside it?",
                Set.of("docker"), false, Set.of("dockerfile", "run")));

        q.add(new InterviewQuestion("Q183",
                "What is the difference between CMD and ENTRYPOINT in Docker?",
                Set.of("docker"), false, Set.of("cmd", "entrypoint")));

        q.add(new InterviewQuestion("Q184",
                "What is a container registry and why do teams use it?",
                Set.of("docker", "ci/cd"), false, Set.of("registry", "image")));

        q.add(new InterviewQuestion("Q185",
                "What is docker-compose used for?",
                Set.of("docker"), false, Set.of("compose", "services")));

        q.add(new InterviewQuestion("Q186",
                "What is Kubernetes and what does it solve compared to plain Docker?",
                Set.of("kubernetes"), true, Set.of("orchestration", "scaling")));

        q.add(new InterviewQuestion("Q187",
                "In Kubernetes, what is a pod?",
                Set.of("kubernetes"), true, Set.of("pod", "container")));

        q.add(new InterviewQuestion("Q188",
                "What is a Deployment in Kubernetes? Why use it?",
                Set.of("kubernetes"), false, Set.of("deployment", "replica")));

        q.add(new InterviewQuestion("Q189",
                "What is a Service in Kubernetes (ClusterIP/LoadBalancer) at a high level?",
                Set.of("kubernetes"), false, Set.of("service", "loadbalancer")));

        q.add(new InterviewQuestion("Q190",
                "What is a ConfigMap vs Secret? Why separate them?",
                Set.of("kubernetes", "devops"), false, Set.of("configmap", "secret")));

        q.add(new InterviewQuestion("Q191",
                "What is CI/CD? What is the goal of a pipeline?",
                Set.of("ci/cd"), true, Set.of("pipeline", "automate")));

        q.add(new InterviewQuestion("Q192",
                "What is Jenkins commonly used for?",
                Set.of("jenkins", "ci/cd"), false, Set.of("jenkins", "pipeline")));

        q.add(new InterviewQuestion("Q193",
                "What is a rollback strategy and why do you need it?",
                Set.of("ci/cd"), false, Set.of("rollback", "release")));

        q.add(new InterviewQuestion("Q194",
                "What are environment variables and why are they used in deployments?",
                Set.of("devops"), true, Set.of("environment", "variable")));

        q.add(new InterviewQuestion("Q195",
                "Cloud basics: what is the difference between IaaS, PaaS, SaaS?",
                Set.of("aws", "azure", "gcp"), false, Set.of("iaas", "paas")));

        q.add(new InterviewQuestion("Q196",
                "AWS: name a few common services (EC2, S3, RDS) and what they do.",
                Set.of("aws"), false, Set.of("ec2", "s3")));

        q.add(new InterviewQuestion("Q197",
                "What is autoscaling and when would you use it?",
                Set.of("aws", "devops"), false, Set.of("autoscaling", "load")));

        q.add(new InterviewQuestion("Q198",
                "What is monitoring? What are metrics and alerts?",
                Set.of("devops"), true, Set.of("metrics", "alert")));

        q.add(new InterviewQuestion("Q199",
                "What is logging? What is the difference between logs and metrics?",
                Set.of("devops"), false, Set.of("logs", "metrics")));

        q.add(new InterviewQuestion("Q200",
                "What is infrastructure as code (IaC) conceptually?",
                Set.of("devops"), false, Set.of("infrastructure", "code")));

        // =========================
        // Messaging / Streaming (Kafka / RabbitMQ)
        // =========================
        q.add(new InterviewQuestion("Q201",
                "Why use a message queue instead of direct HTTP calls between services?",
                Set.of("kafka", "rabbitmq", "microservices"), true, Set.of("decouple", "async")));

        q.add(new InterviewQuestion("Q202",
                "Kafka vs RabbitMQ: what is one key difference in usage or model?",
                Set.of("kafka", "rabbitmq"), false, Set.of("stream", "queue")));

        q.add(new InterviewQuestion("Q203",
                "Kafka: what is a topic and partition?",
                Set.of("kafka"), true, Set.of("topic", "partition")));

        q.add(new InterviewQuestion("Q204",
                "Kafka: what is a consumer group and why is it useful?",
                Set.of("kafka"), false, Set.of("consumer", "group")));

        q.add(new InterviewQuestion("Q205",
                "What does 'at least once' delivery mean? What is a downside?",
                Set.of("kafka"), false, Set.of("delivered", "duplicate")));

        q.add(new InterviewQuestion("Q206",
                "What does 'exactly once' mean conceptually? Why is it harder?",
                Set.of("kafka"), false, Set.of("idempotent", "deduplication")));

        q.add(new InterviewQuestion("Q207",
                "What is idempotent consumer and why is it important?",
                Set.of("kafka", "microservices"), true, Set.of("idempotent", "duplicate")));

        q.add(new InterviewQuestion("Q208",
                "RabbitMQ: what is an exchange and a queue?",
                Set.of("rabbitmq"), false, Set.of("exchange", "queue")));

        q.add(new InterviewQuestion("Q209",
                "What is a dead letter queue (DLQ) and why use it?",
                Set.of("rabbitmq", "kafka"), false, Set.of("dead", "letter")));

        q.add(new InterviewQuestion("Q210",
                "What is message ordering and when can it break?",
                Set.of("kafka", "rabbitmq"), false, Set.of("order", "partition")));

        // =========================
        // Testing (JUnit / Mockito / Integration)
        // =========================
        q.add(new InterviewQuestion("Q211",
                "What is the difference between unit tests and integration tests?",
                Set.of("testing"), true, Set.of("unit", "integration")));

        q.add(new InterviewQuestion("Q212",
                "What makes a unit test good (fast, isolated, deterministic)?",
                Set.of("testing"), true, Set.of("isolated", "deterministic")));

        q.add(new InterviewQuestion("Q213",
                "What is JUnit used for and what is @Test?",
                Set.of("junit", "testing"), false, Set.of("junit", "test")));

        q.add(new InterviewQuestion("Q214",
                "What is Mockito and why do we mock dependencies?",
                Set.of("mockito", "testing"), true, Set.of("mock", "dependency")));

        q.add(new InterviewQuestion("Q215",
                "What is a stub vs a mock?",
                Set.of("testing", "mockito"), false, Set.of("stub", "mock")));

        q.add(new InterviewQuestion("Q216",
                "Explain arrange-act-assert (AAA) structure in tests.",
                Set.of("testing"), false, Set.of("arrange", "assert")));

        q.add(new InterviewQuestion("Q217",
                "What is Testcontainers and what problem does it solve?",
                Set.of("testing", "docker"), false, Set.of("testcontainers", "container")));

        q.add(new InterviewQuestion("Q218",
                "How do you test a Spring Boot REST controller (MockMvc / WebTestClient)?",
                Set.of("testing", "spring boot"), false, Set.of("mockmvc", "controller")));

        q.add(new InterviewQuestion("Q219",
                "What is an integration test for a repository/database? What is risky about it?",
                Set.of("testing", "sql"), false, Set.of("database", "slow")));

        q.add(new InterviewQuestion("Q220",
                "Explain flaky tests: what causes them and how to reduce them.",
                Set.of("testing"), false, Set.of("flaky", "timing")));

        q.add(new InterviewQuestion("Q221",
                "What is code coverage? Why can it be misleading?",
                Set.of("testing"), false, Set.of("coverage", "quality")));

        q.add(new InterviewQuestion("Q222",
                "Selenium: what is it used for (high level)?",
                Set.of("selenium", "testing"), false, Set.of("browser", "automation")));

        q.add(new InterviewQuestion("Q223",
                "Playwright: what is it used for (high level)?",
                Set.of("playwright", "testing"), false, Set.of("browser", "automation")));

        q.add(new InterviewQuestion("Q224",
                "What is contract testing (consumer-driven contracts) conceptually?",
                Set.of("testing", "microservices"), false, Set.of("contract", "consumer")));

        q.add(new InterviewQuestion("Q225",
                "What is mocking too much? What’s a common sign of over-mocking?",
                Set.of("testing"), false, Set.of("mock", "refactor")));

        // =========================
        // Git / GitHub / Linux / Bash
        // =========================
        q.add(new InterviewQuestion("Q226",
                "What is Git and why do teams use it?",
                Set.of("git"), true, Set.of("version", "control")));

        q.add(new InterviewQuestion("Q227",
                "Explain the difference between git pull and git push.",
                Set.of("git"), true, Set.of("pull", "push")));

        q.add(new InterviewQuestion("Q228",
                "What is a merge conflict and how do you resolve it?",
                Set.of("git"), true, Set.of("conflict", "merge")));

        q.add(new InterviewQuestion("Q229",
                "What is a rebase and how is it different from merge?",
                Set.of("git"), false, Set.of("rebase", "merge")));

        q.add(new InterviewQuestion("Q230",
                "What is a commit hash and what is a branch?",
                Set.of("git"), true, Set.of("commit", "branch")));

        q.add(new InterviewQuestion("Q231",
                "What is a pull request and why is it useful?",
                Set.of("github", "git"), false, Set.of("pull", "review")));

        q.add(new InterviewQuestion("Q232",
                "Linux: what is a process? What is a port?",
                Set.of("linux"), false, Set.of("process", "port")));

        q.add(new InterviewQuestion("Q233",
                "What does 'chmod' do and why would you use it?",
                Set.of("linux", "bash"), false, Set.of("chmod", "permission")));

        q.add(new InterviewQuestion("Q234",
                "What is stdout vs stderr?",
                Set.of("linux", "bash"), false, Set.of("stdout", "stderr")));

        q.add(new InterviewQuestion("Q235",
                "What is a curl command used for in backend debugging?",
                Set.of("bash", "api"), false, Set.of("curl", "http")));

        q.add(new InterviewQuestion("Q236",
                "How would you search logs quickly on Linux? (grep basic idea)",
                Set.of("linux", "bash"), false, Set.of("grep", "log")));

        q.add(new InterviewQuestion("Q237",
                "What is an environment variable in Linux and how do you set it?",
                Set.of("linux", "bash"), true, Set.of("export", "env")));

        q.add(new InterviewQuestion("Q238",
                "Explain what a Docker container 'port mapping' means (e.g., 8080:8080).",
                Set.of("docker", "linux"), false, Set.of("port", "mapping")));

        q.add(new InterviewQuestion("Q239",
                "What is SSH and why do developers use it?",
                Set.of("linux"), false, Set.of("ssh", "remote")));

        q.add(new InterviewQuestion("Q240",
                "What is a .gitignore and why is it important?",
                Set.of("git", "github"), false, Set.of("gitignore", "track")));

        // =========================
        // Extra Backend Languages (awareness)
        // =========================
        q.add(new InterviewQuestion("Q241",
                "Python: what is a virtual environment and why use it?",
                Set.of("python"), false, Set.of("virtual", "environment")));

        q.add(new InterviewQuestion("Q242",
                "Python: what is a list vs a tuple?",
                Set.of("python"), false, Set.of("list", "tuple")));

        q.add(new InterviewQuestion("Q243",
                "Go: what is a goroutine (conceptually)?",
                Set.of("go"), false, Set.of("goroutine", "concurrency")));

        q.add(new InterviewQuestion("Q244",
                "C#: what is .NET (high level) and why is it used?",
                Set.of("c#", ".net"), false, Set.of(".net", "runtime")));

        q.add(new InterviewQuestion("Q245",
                "What is a strongly typed language? Give an example.",
                Set.of("java", "python", "go", "c#"), false, Set.of("type", "compile")));

        // =========================
        // Minimal Frontend awareness (not main focus)
        // =========================
        q.add(new InterviewQuestion("Q246",
                "What is JSON and why is it commonly used between frontend and backend?",
                Set.of("javascript", "api"), true, Set.of("json", "http")));

        q.add(new InterviewQuestion("Q247",
                "What is TypeScript and why do teams use it over plain JavaScript?",
                Set.of("typescript", "javascript"), false, Set.of("types", "compile")));

        q.add(new InterviewQuestion("Q248",
                "React: what is a component (high level)?",
                Set.of("react"), false, Set.of("component", "state")));

        q.add(new InterviewQuestion("Q249",
                "What is CORS from the frontend perspective?",
                Set.of("javascript", "rest"), false, Set.of("cors", "origin")));

        q.add(new InterviewQuestion("Q250",
                "What is a CSRF token and why might a frontend send it?",
                Set.of("javascript", "security"), false, Set.of("csrf", "token")));

        // =========================
        // More real interview questions (System design junior level)
        // =========================
        q.add(new InterviewQuestion("Q251",
                "Design a simple URL shortener at a high level: what tables/fields would you store?",
                Set.of("design patterns", "sql", "api"), true, Set.of("id", "redirect")));

        q.add(new InterviewQuestion("Q252",
                "Design a login endpoint: what validations and security checks would you add?",
                Set.of("api", "security"), true, Set.of("validation", "password")));

        q.add(new InterviewQuestion("Q253",
                "How would you prevent duplicate submissions (e.g., user clicks twice)?",
                Set.of("api", "microservices"), false, Set.of("idempotent", "retry")));

        q.add(new InterviewQuestion("Q254",
                "How would you implement a basic caching layer for a heavy endpoint?",
                Set.of("redis", "api"), false, Set.of("cache", "ttl")));

        q.add(new InterviewQuestion("Q255",
                "How would you store passwords securely? (high-level)",
                Set.of("security", "api"), true, Set.of("hash", "salt")));

        q.add(new InterviewQuestion("Q256",
                "What is SQL injection and how do you prevent it?",
                Set.of("sql", "security"), true, Set.of("injection", "prepared")));

        q.add(new InterviewQuestion("Q257",
                "What is the difference between symmetric and asymmetric encryption? (high-level)",
                Set.of("security"), false, Set.of("symmetric", "asymmetric")));

        q.add(new InterviewQuestion("Q258",
                "What is a load balancer and why is it used?",
                Set.of("microservices", "devops"), false, Set.of("load", "balance")));

        q.add(new InterviewQuestion("Q259",
                "What is horizontal scaling vs vertical scaling?",
                Set.of("microservices", "devops"), true, Set.of("horizontal", "vertical")));

        q.add(new InterviewQuestion("Q260",
                "If an endpoint is slow, how do you debug it step-by-step? (logs, metrics, DB, profiling)",
                Set.of("devops", "sql", "api"), true, Set.of("logs", "metrics")));

        // =========================
        // Modern Java Concurrency (Java 8-21)
        // =========================
        q.add(new InterviewQuestion("Q261",
                "What is CompletableFuture in Java? How does it differ from Future?",
                Set.of("java", "concurrency"), true, Set.of("completablefuture", "async")));

        q.add(new InterviewQuestion("Q262",
                "What is the difference between ReentrantLock and synchronized in Java?",
                Set.of("java", "concurrency"), false, Set.of("reentrantlock", "lock")));

        q.add(new InterviewQuestion("Q263",
                "Why is ConcurrentHashMap preferred over Collections.synchronizedMap()?",
                Set.of("java", "concurrency"), true, Set.of("concurrenthashmap", "segment")));

        q.add(new InterviewQuestion("Q264",
                "What is AtomicInteger? When would you use it instead of synchronized?",
                Set.of("java", "concurrency"), false, Set.of("atomic", "compare")));

        q.add(new InterviewQuestion("Q265",
                "What is a ForkJoinPool and when does Java use it internally?",
                Set.of("java", "concurrency"), false, Set.of("forkjoin", "parallel")));

        q.add(new InterviewQuestion("Q266",
                "What is ThreadLocal and what is a common use-case in backend frameworks?",
                Set.of("java", "concurrency"), false, Set.of("threadlocal", "thread")));

        q.add(new InterviewQuestion("Q267",
                "Explain the happens-before relationship in Java memory model.",
                Set.of("java", "concurrency"), false, Set.of("happens", "visibility")));

        // =========================
        // Scenario / Applied Questions
        // =========================
        q.add(new InterviewQuestion("Q268",
                "Your API endpoint suddenly takes 5 seconds to respond. It was fast yesterday, nothing was deployed. Walk me through how you diagnose and fix it.",
                Set.of("sql", "api", "devops"), true, Set.of("query", "index")));

        q.add(new InterviewQuestion("Q269",
                "You need to add a NOT NULL column to a table that already has 10 million rows in production. What are the risks and how do you do it safely?",
                Set.of("sql", "devops"), false, Set.of("default", "migration")));

        q.add(new InterviewQuestion("Q270",
                "A junior developer's PR catches Exception everywhere and returns HTTP 500 for all errors. What issues do you raise in code review and what do you suggest instead?",
                Set.of("spring", "java"), true, Set.of("specific", "handler")));

        q.add(new InterviewQuestion("Q271",
                "Your service calls an external payment API that sometimes fails or takes too long. How do you make your service resilient to this?",
                Set.of("api", "microservices"), true, Set.of("timeout", "fallback")));

        q.add(new InterviewQuestion("Q272",
                "A user reports their data disappeared after the server restarted. You realise the session store was in-memory. What happened and how do you prevent it?",
                Set.of("java", "spring"), true, Set.of("persist", "database")));

        q.add(new InterviewQuestion("Q273",
                "Production is returning 500 errors for roughly 20% of requests. You have access to logs and metrics. What do you do first?",
                Set.of("devops", "api"), true, Set.of("logs", "trace")));

        q.add(new InterviewQuestion("Q274",
                "Two users buy the last item in stock at exactly the same time. Both checks pass, both purchases complete — but there is only one item. What went wrong and how do you fix it?",
                Set.of("sql", "concurrency"), true, Set.of("lock", "transaction")));

        q.add(new InterviewQuestion("Q275",
                "Your Spring Boot app memory grows steadily over 24 hours then crashes with OutOfMemoryError. How do you investigate this?",
                Set.of("java", "devops"), false, Set.of("heap", "leak")));

        q.add(new InterviewQuestion("Q276",
                "You need to rename a field in a REST API response. Three external clients use this API. What is your migration strategy?",
                Set.of("api", "microservices"), false, Set.of("version", "backward")));

        q.add(new InterviewQuestion("Q277",
                "You have a method that sends a confirmation email and then saves to the database. The email sends but the DB save fails. Users get an email but no record is created. How would you redesign this?",
                Set.of("spring", "sql"), true, Set.of("transaction", "rollback")));

        // =========================
        // Spot-the-Bug / Applied Code Questions
        // =========================
        q.add(new InterviewQuestion("SPOTBUG1",
                "Write a SQL query to find all users who have placed more than 3 orders. Tables: users(id, username), orders(id, user_id, created_at).",
                Set.of("sql"), true,
                "",
                Set.of("count", "having", "group")));

        q.add(new InterviewQuestion("SPOTBUG2",
                "Write a SQL query to return the username and total amount spent for the top 5 customers. Tables: users(id, username), orders(id, user_id, total_amount).",
                Set.of("sql"), false,
                "",
                Set.of("sum", "group", "limit")));

        q.add(new InterviewQuestion("SPOTBUG3",
                "What is the bug in this code and how do you fix it?\n\nOptional<User> user = userRepo.findById(id);\nreturn user.get().getName();",
                Set.of("java"), true,
                "",
                Set.of("present", "orElse")));

        q.add(new InterviewQuestion("SPOTBUG4",
                "What is wrong with this code? How would you make it thread-safe?\n\nprivate int counter = 0;\npublic void increment() { counter++; }\npublic int getCount() { return counter; }",
                Set.of("java", "concurrency"), true,
                "",
                Set.of("atomic", "synchronized")));

        q.add(new InterviewQuestion("SPOTBUG5",
                "Rewrite this loop using Java Streams:\n\nList<String> result = new ArrayList<>();\nfor (String name : names) {\n  if (name.length() > 5) result.add(name.toUpperCase());\n}\nreturn result;",
                Set.of("java"), false,
                "",
                Set.of("filter", "map")));

        q.add(new InterviewQuestion("SPOTBUG6",
                "Write a SQL query to return all departments where the average employee salary is above 50000. Tables: employees(id, name, salary, dept_id), departments(id, name).",
                Set.of("sql"), false,
                "",
                Set.of("avg", "having")));

        q.add(new InterviewQuestion("SPOTBUG7",
                "What is wrong with this Spring service, and why does it make testing harder?\n\n@Service\npublic class OrderService {\n  @Autowired\n  private UserRepository repo;\n}",
                Set.of("spring", "testing"), false,
                "",
                Set.of("constructor", "final")));

        q.add(new InterviewQuestion("SPOTBUG8",
                "What is the problem with this code? A ConcurrentModificationException is thrown at runtime.\n\nfor (String key : map.keySet()) {\n  if (key.startsWith(\"tmp_\")) map.remove(key);\n}",
                Set.of("java"), true,
                "",
                Set.of("iterator", "remove")));

        // =========================
        // Frontend / JavaScript / React
        // =========================
        q.add(new InterviewQuestion("Q278",
                "What is the difference between let, const, and var in JavaScript?",
                Set.of("javascript"), true, Set.of("hoisting", "block")));

        q.add(new InterviewQuestion("Q279",
                "Explain closures in JavaScript with a simple example.",
                Set.of("javascript"), true, Set.of("closure", "scope")));

        q.add(new InterviewQuestion("Q280",
                "What is the JavaScript event loop? How does async code actually work?",
                Set.of("javascript"), true, Set.of("callstack", "queue")));

        q.add(new InterviewQuestion("Q281",
                "What is a Promise in JavaScript? How does async/await relate to it?",
                Set.of("javascript"), true, Set.of("promise", "resolve")));

        q.add(new InterviewQuestion("Q282",
                "What is the difference between == and === in JavaScript?",
                Set.of("javascript"), true, Set.of("coercion", "strict")));

        q.add(new InterviewQuestion("Q283",
                "What is event bubbling in the DOM? How does event delegation use it?",
                Set.of("javascript"), false, Set.of("bubble", "parent")));

        q.add(new InterviewQuestion("Q284",
                "What is the difference between state and props in React?",
                Set.of("react"), true, Set.of("mutable", "parent")));

        q.add(new InterviewQuestion("Q285",
                "What are React hooks? Name 3 common ones and explain useState.",
                Set.of("react"), true, Set.of("usestate", "useeffect")));

        q.add(new InterviewQuestion("Q286",
                "What is the virtual DOM in React and why does it improve performance?",
                Set.of("react"), true, Set.of("diffing", "reconciliation")));

        q.add(new InterviewQuestion("Q287",
                "Explain useEffect in React. What is a cleanup function and when does it run?",
                Set.of("react"), false, Set.of("dependency", "cleanup")));

        q.add(new InterviewQuestion("Q288",
                "What is the difference between a controlled and uncontrolled component in React?",
                Set.of("react"), false, Set.of("controlled", "ref")));

        q.add(new InterviewQuestion("Q289",
                "What is React Context and when would you use it instead of passing props?",
                Set.of("react"), false, Set.of("context", "drilling")));

        q.add(new InterviewQuestion("Q290",
                "What is localStorage vs sessionStorage? When would you use each?",
                Set.of("javascript"), false, Set.of("session", "persist")));

        q.add(new InterviewQuestion("Q291",
                "What is CSS flexbox? Give a practical example of when you would use it.",
                Set.of("css"), false, Set.of("flex", "align")));

        q.add(new InterviewQuestion("Q292",
                "What is responsive design and how do CSS media queries work?",
                Set.of("css"), false, Set.of("breakpoint", "media")));

        q.add(new InterviewQuestion("Q293",
                "What is code splitting and lazy loading in React? Why do they matter for performance?",
                Set.of("react"), false, Set.of("lazy", "bundle")));

        q.add(new InterviewQuestion("Q294",
                "What is TypeScript? What does it add over JavaScript and what is the trade-off?",
                Set.of("typescript"), false, Set.of("static", "compile")));

        q.add(new InterviewQuestion("Q295",
                "A React component re-renders on every keystroke slowing the UI. How do you investigate and fix it?",
                Set.of("react"), true, Set.of("memo", "dependency")));

        // =========================
        // Python / Django
        // =========================
        q.add(new InterviewQuestion("Q296",
                "What is the difference between a list and a tuple in Python?",
                Set.of("python"), true, Set.of("mutable", "immutable")));

        q.add(new InterviewQuestion("Q297",
                "What is a Python decorator? Explain what it does with a simple example.",
                Set.of("python"), true, Set.of("wrap", "function")));

        q.add(new InterviewQuestion("Q298",
                "What is a Python generator and how does yield work?",
                Set.of("python"), false, Set.of("yield", "lazy")));

        q.add(new InterviewQuestion("Q299",
                "What is the GIL in Python and how does it affect multithreading?",
                Set.of("python"), false, Set.of("gil", "thread")));

        q.add(new InterviewQuestion("Q300",
                "Explain list comprehensions in Python. How are they different from a for loop?",
                Set.of("python"), false, Set.of("comprehension", "expression")));

        q.add(new InterviewQuestion("Q301",
                "What is the difference between *args and **kwargs in Python?",
                Set.of("python"), false, Set.of("positional", "keyword")));

        q.add(new InterviewQuestion("Q302",
                "What is Django's ORM? How does it compare to writing raw SQL?",
                Set.of("django", "python"), true, Set.of("queryset", "model")));

        q.add(new InterviewQuestion("Q303",
                "Explain Django's MVT pattern (Model, View, Template). What is each layer responsible for?",
                Set.of("django", "python"), true, Set.of("model", "view")));

        q.add(new InterviewQuestion("Q304",
                "What is a Django migration and why is running them in the wrong order dangerous?",
                Set.of("django", "python"), true, Set.of("migration", "schema")));

        q.add(new InterviewQuestion("Q305",
                "What is Django REST Framework? What does a serializer do?",
                Set.of("django", "python"), false, Set.of("serializer", "validate")));

        q.add(new InterviewQuestion("Q306",
                "What is Django middleware and give one real-world use-case?",
                Set.of("django", "python"), false, Set.of("middleware", "request")));

        q.add(new InterviewQuestion("Q307",
                "What is pytest and how does it differ from Python's built-in unittest?",
                Set.of("python", "testing"), false, Set.of("fixture", "assert")));

        q.add(new InterviewQuestion("Q308",
                "What is a virtual environment in Python and why is it essential on every project?",
                Set.of("python"), false, Set.of("isolate", "dependency")));

        q.add(new InterviewQuestion("Q309",
                "What is async/await in Python? How does asyncio differ from threading?",
                Set.of("python"), false, Set.of("coroutine", "event")));

        q.add(new InterviewQuestion("Q310",
                "Your Django app queries the database on every request for the same data. How do you fix it?",
                Set.of("django", "python"), true, Set.of("cache", "queryset")));

        // =========================
        // .NET / C#
        // =========================
        q.add(new InterviewQuestion("Q311",
                "What is the difference between .NET Framework and .NET Core (now .NET 5+)?",
                Set.of("c#", ".net"), true, Set.of("cross", "platform")));

        q.add(new InterviewQuestion("Q312",
                "Explain value types vs reference types in C#. Where is each stored?",
                Set.of("c#"), true, Set.of("stack", "heap")));

        q.add(new InterviewQuestion("Q313",
                "What is LINQ in C#? Give an example of where you would use it.",
                Set.of("c#"), true, Set.of("query", "collection")));

        q.add(new InterviewQuestion("Q314",
                "What is async/await in C# and how does it relate to the Task class?",
                Set.of("c#"), true, Set.of("task", "await")));

        q.add(new InterviewQuestion("Q315",
                "What is dependency injection in ASP.NET Core? How do you register a service?",
                Set.of("c#", ".net"), true, Set.of("register", "inject")));

        q.add(new InterviewQuestion("Q316",
                "What is Entity Framework Core? How is it similar to JPA in Java?",
                Set.of("c#", ".net"), false, Set.of("dbcontext", "entity")));

        q.add(new InterviewQuestion("Q317",
                "What is middleware in ASP.NET Core and how does the pipeline work?",
                Set.of("c#", ".net"), false, Set.of("pipeline", "next")));

        q.add(new InterviewQuestion("Q318",
                "What is a nullable reference type in C# and the null-coalescing operator ??",
                Set.of("c#"), false, Set.of("nullable", "coalescing")));

        q.add(new InterviewQuestion("Q319",
                "What is NuGet and what is the equivalent of a requirements.txt or pom.xml in .NET?",
                Set.of("c#", ".net"), false, Set.of("package", "dependency")));

        q.add(new InterviewQuestion("Q320",
                "What is the difference between an interface and an abstract class in C#?",
                Set.of("c#"), true, Set.of("multiple", "implement")));

        // =========================
        // Android / Kotlin
        // =========================
        q.add(new InterviewQuestion("Q321",
                "What is the difference between an Activity and a Fragment in Android?",
                Set.of("android"), true, Set.of("lifecycle", "fragment")));

        q.add(new InterviewQuestion("Q322",
                "Explain the Android Activity lifecycle: onCreate, onResume, onPause, onDestroy.",
                Set.of("android"), true, Set.of("oncreate", "lifecycle")));

        q.add(new InterviewQuestion("Q323",
                "What is an Intent in Android? What is the difference between explicit and implicit?",
                Set.of("android"), true, Set.of("explicit", "action")));

        q.add(new InterviewQuestion("Q324",
                "What is Kotlin's null safety? How does it prevent NullPointerException?",
                Set.of("android", "kotlin"), true, Set.of("nullable", "operator")));

        q.add(new InterviewQuestion("Q325",
                "What is a Kotlin coroutine and why is it preferred over Java threads on Android?",
                Set.of("android", "kotlin"), false, Set.of("suspend", "dispatcher")));

        q.add(new InterviewQuestion("Q326",
                "What are the main local storage options on Android: SharedPreferences, Room, files?",
                Set.of("android"), false, Set.of("room", "sharedpreferences")));

        q.add(new InterviewQuestion("Q327",
                "What is RecyclerView and why is it used instead of ListView?",
                Set.of("android"), false, Set.of("recycle", "holder")));

        q.add(new InterviewQuestion("Q328",
                "What is Gradle in Android? What does the build.gradle file control?",
                Set.of("android"), false, Set.of("build", "dependency")));

        // =========================
        // iOS / Swift
        // =========================
        q.add(new InterviewQuestion("Q329",
                "What is an Optional in Swift and why does Swift have them?",
                Set.of("ios", "swift"), true, Set.of("nil", "unwrap")));

        q.add(new InterviewQuestion("Q330",
                "Explain protocols in Swift. How do they compare to interfaces in Java?",
                Set.of("ios", "swift"), true, Set.of("protocol", "conform")));

        q.add(new InterviewQuestion("Q331",
                "What is ARC (Automatic Reference Counting) in Swift?",
                Set.of("ios", "swift"), true, Set.of("retain", "count")));

        q.add(new InterviewQuestion("Q332",
                "What is the difference between a strong and a weak reference in Swift? When does each matter?",
                Set.of("ios", "swift"), false, Set.of("weak", "cycle")));

        q.add(new InterviewQuestion("Q333",
                "What is the difference between SwiftUI and UIKit?",
                Set.of("ios", "swift"), false, Set.of("declarative", "uikit")));

        q.add(new InterviewQuestion("Q334",
                "What is a closure in Swift and how does it capture values?",
                Set.of("ios", "swift"), false, Set.of("capture", "closure")));

        // =========================
        // Data Science / Machine Learning
        // =========================
        q.add(new InterviewQuestion("Q335",
                "What is the difference between supervised and unsupervised learning?",
                Set.of("ml"), true, Set.of("label", "supervised")));

        q.add(new InterviewQuestion("Q336",
                "What is overfitting? How do you detect and prevent it?",
                Set.of("ml"), true, Set.of("generalize", "validation")));

        q.add(new InterviewQuestion("Q337",
                "What is cross-validation and why is it used instead of a single train/test split?",
                Set.of("ml"), false, Set.of("fold", "evaluate")));

        q.add(new InterviewQuestion("Q338",
                "Explain precision, recall, and F1 score. When would you optimise for recall over precision?",
                Set.of("ml"), true, Set.of("precision", "recall")));

        q.add(new InterviewQuestion("Q339",
                "What is a neural network at a high level? What is a layer and an activation function?",
                Set.of("ml"), false, Set.of("weight", "activation")));

        q.add(new InterviewQuestion("Q340",
                "What is a Pandas DataFrame? What operations do you use most often?",
                Set.of("python", "ml"), true, Set.of("dataframe", "column")));

        q.add(new InterviewQuestion("Q341",
                "What is NumPy and why is it faster than Python lists for numerical computation?",
                Set.of("python", "ml"), false, Set.of("vectorized", "array")));

        q.add(new InterviewQuestion("Q342",
                "What is feature engineering? Give a concrete example.",
                Set.of("ml"), false, Set.of("feature", "transform")));

        q.add(new InterviewQuestion("Q343",
                "What is gradient descent at a high level? What does the learning rate control?",
                Set.of("ml"), false, Set.of("gradient", "minimize")));

        q.add(new InterviewQuestion("Q344",
                "Your model has 99% accuracy but it barely predicts the minority class. What is happening?",
                Set.of("ml"), true, Set.of("imbalanced", "recall")));

        // =========================
        // Database Administration (Advanced SQL)
        // =========================
        q.add(new InterviewQuestion("Q345",
                "What is a window function in SQL? Give an example using ROW_NUMBER or RANK.",
                Set.of("sql", "postgresql"), true, Set.of("over", "partition")));

        q.add(new InterviewQuestion("Q346",
                "What is a CTE (Common Table Expression)? How is it different from a subquery?",
                Set.of("sql"), false, Set.of("cte", "readable")));

        q.add(new InterviewQuestion("Q347",
                "What is a stored procedure? When would you use one and what are the downsides?",
                Set.of("sql"), false, Set.of("procedure", "reusable")));

        q.add(new InterviewQuestion("Q348",
                "What is a database trigger? Give a use-case where it makes sense.",
                Set.of("sql"), false, Set.of("trigger", "automatic")));

        q.add(new InterviewQuestion("Q349",
                "What is a connection pool? Why is it critical for backend applications?",
                Set.of("sql"), true, Set.of("pool", "overhead")));

        q.add(new InterviewQuestion("Q350",
                "What is a materialized view? How does it differ from a regular view?",
                Set.of("sql", "postgresql"), false, Set.of("materialized", "refresh")));

        q.add(new InterviewQuestion("Q351",
                "Write a query to find and remove duplicate rows from a table, keeping only one.",
                Set.of("sql"), false, Set.of("duplicate", "rowid")));

        q.add(new InterviewQuestion("Q352",
                "What is EXPLAIN / EXPLAIN ANALYZE in PostgreSQL and how do you use it to fix slow queries?",
                Set.of("sql", "postgresql"), true, Set.of("explain", "index")));

        // =========================
        // Security Engineer
        // =========================
        q.add(new InterviewQuestion("Q353",
                "What is the OWASP Top 10? Name at least 3 and explain one.",
                Set.of("security"), true, Set.of("injection", "xss")));

        q.add(new InterviewQuestion("Q354",
                "What is TLS/SSL? What does it protect against and how does the handshake work at a high level?",
                Set.of("security"), true, Set.of("certificate", "encrypt")));

        q.add(new InterviewQuestion("Q355",
                "What is the difference between hashing and encryption? When do you use each?",
                Set.of("security"), true, Set.of("hash", "reversible")));

        q.add(new InterviewQuestion("Q356",
                "What is a man-in-the-middle (MITM) attack? How do certificates prevent it?",
                Set.of("security"), false, Set.of("certificate", "intercept")));

        q.add(new InterviewQuestion("Q357",
                "Explain the OAuth 2.0 authorization code flow step by step.",
                Set.of("security", "api"), false, Set.of("code", "token")));

        q.add(new InterviewQuestion("Q358",
                "What is OpenID Connect (OIDC) and how does it extend OAuth 2.0?",
                Set.of("security", "api"), false, Set.of("identity", "openid")));

        q.add(new InterviewQuestion("Q359",
                "What is the principle of least privilege? Give a concrete backend example.",
                Set.of("security"), true, Set.of("minimum", "access")));

        q.add(new InterviewQuestion("Q360",
                "What is a security misconfiguration? Give two examples of how this happens in real systems.",
                Set.of("security"), false, Set.of("default", "exposed")));

        q.add(new InterviewQuestion("Q361",
                "What is the difference between input sanitization and validation? Why do you need both?",
                Set.of("security"), true, Set.of("sanitize", "validate")));

        q.add(new InterviewQuestion("Q362",
                "What is a JWT and what are the security risks if you store sensitive data inside it?",
                Set.of("security", "api"), true, Set.of("signed", "payload")));

        // =========================
        // Embedded / Systems Engineering
        // =========================
        q.add(new InterviewQuestion("Q363",
                "What is the difference between a process and a thread at the OS level?",
                Set.of("linux", "embedded"), true, Set.of("memory", "share")));

        q.add(new InterviewQuestion("Q364",
                "What is a pointer in C? Why are they considered dangerous?",
                Set.of("embedded"), true, Set.of("address", "dereference")));

        q.add(new InterviewQuestion("Q365",
                "What is a stack overflow? What typically causes it in embedded or systems code?",
                Set.of("embedded"), false, Set.of("recursion", "depth")));

        q.add(new InterviewQuestion("Q366",
                "What is the difference between big-endian and little-endian byte order?",
                Set.of("embedded"), false, Set.of("endian", "byte")));

        q.add(new InterviewQuestion("Q367",
                "What is an interrupt in embedded systems? What is an ISR?",
                Set.of("embedded"), true, Set.of("interrupt", "handler")));

        q.add(new InterviewQuestion("Q368",
                "What is the difference between volatile in C and volatile in Java?",
                Set.of("embedded", "java"), false, Set.of("compiler", "optimize")));

        q.add(new InterviewQuestion("Q369",
                "What is an RTOS (Real-Time Operating System)? When would you need one over a standard OS?",
                Set.of("embedded"), false, Set.of("deterministic", "deadline")));

        q.add(new InterviewQuestion("Q370",
                "What is the difference between polling and interrupt-driven I/O in embedded systems?",
                Set.of("embedded"), false, Set.of("polling", "interrupt")));

        // =========================
        // Frontend / React / JS — Extended
        // =========================
        q.add(new InterviewQuestion("Q371",
                "What is useMemo in React? When does it help and when is it premature optimisation?",
                Set.of("react"), false, Set.of("memoize", "expensive")));

        q.add(new InterviewQuestion("Q372",
                "What is useCallback in React? How is it different from useMemo?",
                Set.of("react"), false, Set.of("callback", "reference")));

        q.add(new InterviewQuestion("Q373",
                "What is a custom hook in React and when would you create one?",
                Set.of("react"), false, Set.of("reuse", "hook")));

        q.add(new InterviewQuestion("Q374",
                "What is an Error Boundary in React? What does it catch?",
                Set.of("react"), false, Set.of("boundary", "render")));

        q.add(new InterviewQuestion("Q375",
                "Explain JavaScript's prototype chain. How does inheritance work without classes?",
                Set.of("javascript"), false, Set.of("prototype", "chain")));

        q.add(new InterviewQuestion("Q376",
                "What does 'this' refer to in JavaScript? How does it behave differently in arrow functions?",
                Set.of("javascript"), true, Set.of("context", "arrow")));

        q.add(new InterviewQuestion("Q377",
                "What is the Fetch API? How do you handle errors from a failed HTTP response?",
                Set.of("javascript"), false, Set.of("fetch", "promise")));

        q.add(new InterviewQuestion("Q378",
                "What is CSS specificity and how does the browser decide which rule wins?",
                Set.of("css"), false, Set.of("specificity", "selector")));

        q.add(new InterviewQuestion("Q379",
                "What are Core Web Vitals? Name two metrics and what they measure.",
                Set.of("javascript"), false, Set.of("performance", "lcp")));

        q.add(new InterviewQuestion("Q380",
                "What is an XSS attack from the frontend perspective? How do you prevent it in React?",
                Set.of("javascript", "security"), true, Set.of("sanitize", "escape")));

        q.add(new InterviewQuestion("Q381",
                "What is the difference between client-side rendering, server-side rendering, and static site generation?",
                Set.of("react", "javascript"), false, Set.of("server", "static")));

        q.add(new InterviewQuestion("Q382",
                "Your React app's first load is slow. Name three techniques to improve it.",
                Set.of("react"), false, Set.of("lazy", "bundle")));

        // =========================
        // Python / Django — Extended
        // =========================
        q.add(new InterviewQuestion("Q383",
                "What is a Python context manager? How does the 'with' statement work under the hood?",
                Set.of("python"), false, Set.of("enter", "exit")));

        q.add(new InterviewQuestion("Q384",
                "What are Python dunder (magic) methods? Give three examples.",
                Set.of("python"), false, Set.of("dunder", "str")));

        q.add(new InterviewQuestion("Q385",
                "What are type hints in Python? Are they enforced at runtime?",
                Set.of("python"), false, Set.of("annotation", "runtime")));

        q.add(new InterviewQuestion("Q386",
                "What is a Python dataclass? What boilerplate does it remove?",
                Set.of("python"), false, Set.of("dataclass", "boilerplate")));

        q.add(new InterviewQuestion("Q387",
                "What are Django signals? Give a real-world example of when you would use one.",
                Set.of("django", "python"), false, Set.of("signal", "receiver")));

        q.add(new InterviewQuestion("Q388",
                "What is Celery in a Django project and why do you need it?",
                Set.of("django", "python"), false, Set.of("background", "task")));

        q.add(new InterviewQuestion("Q389",
                "What is the N+1 query problem in Django's ORM? How do you fix it?",
                Set.of("django", "python"), true, Set.of("prefetch", "related")));

        q.add(new InterviewQuestion("Q390",
                "What is Django's built-in authentication? What does it give you out of the box?",
                Set.of("django", "python"), false, Set.of("user", "session")));

        q.add(new InterviewQuestion("Q391",
                "What is the difference between a shallow copy and deepcopy in Python? When do you need deepcopy?",
                Set.of("python"), false, Set.of("nested", "reference")));

        q.add(new InterviewQuestion("Q392",
                "Your Django REST endpoint returns data slowly. Walk me through your investigation.",
                Set.of("django", "python"), true, Set.of("query", "profiling")));

        // =========================
        // .NET / C# — Extended
        // =========================
        q.add(new InterviewQuestion("Q393",
                "What are generics in C#? How do they improve type safety and reuse?",
                Set.of("c#"), false, Set.of("generic", "reusable")));

        q.add(new InterviewQuestion("Q394",
                "What is a delegate in C# and how does it relate to events?",
                Set.of("c#"), false, Set.of("delegate", "event")));

        q.add(new InterviewQuestion("Q395",
                "What is IDisposable in C# and why should you use the 'using' statement?",
                Set.of("c#"), false, Set.of("dispose", "resource")));

        q.add(new InterviewQuestion("Q396",
                "What is a C# record (C# 9+) and when would you prefer it over a class?",
                Set.of("c#"), false, Set.of("record", "immutable")));

        q.add(new InterviewQuestion("Q397",
                "What is the difference between IEnumerable and IQueryable in C#?",
                Set.of("c#", ".net"), true, Set.of("queryable", "deferred")));

        q.add(new InterviewQuestion("Q398",
                "How do you run Entity Framework Core migrations and what does Update-Database do?",
                Set.of("c#", ".net"), false, Set.of("migration", "schema")));

        q.add(new InterviewQuestion("Q399",
                "What testing frameworks are common in .NET and how do you mock dependencies?",
                Set.of("c#", "testing"), false, Set.of("xunit", "mock")));

        q.add(new InterviewQuestion("Q400",
                "What is C# pattern matching? Give an example using switch expressions.",
                Set.of("c#"), false, Set.of("pattern", "switch")));

        q.add(new InterviewQuestion("Q401",
                "What is IHostedService in ASP.NET Core? When would you use a background service?",
                Set.of("c#", ".net"), false, Set.of("hosted", "background")));

        q.add(new InterviewQuestion("Q402",
                "Your ASP.NET Core API returns 401 even though credentials look correct. What do you check?",
                Set.of("c#", ".net"), true, Set.of("token", "middleware")));

        // =========================
        // Android / Kotlin — Extended
        // =========================
        q.add(new InterviewQuestion("Q403",
                "What is ViewModel in Android and why does it survive configuration changes like screen rotation?",
                Set.of("android"), true, Set.of("viewmodel", "lifecycle")));

        q.add(new InterviewQuestion("Q404",
                "What is LiveData and how does it work with ViewModel?",
                Set.of("android"), false, Set.of("observe", "lifecycle")));

        q.add(new InterviewQuestion("Q405",
                "What is Jetpack Compose and how is it different from XML layouts?",
                Set.of("android"), false, Set.of("composable", "declarative")));

        q.add(new InterviewQuestion("Q406",
                "What is a Kotlin data class? What methods does the compiler generate for you?",
                Set.of("android", "kotlin"), false, Set.of("equals", "copy")));

        q.add(new InterviewQuestion("Q407",
                "What are Kotlin extension functions? Give a practical example.",
                Set.of("android", "kotlin"), false, Set.of("extension", "receiver")));

        q.add(new InterviewQuestion("Q408",
                "How do you handle runtime permissions in Android (camera, location, etc.)?",
                Set.of("android"), false, Set.of("permission", "request")));

        q.add(new InterviewQuestion("Q409",
                "What is WorkManager in Android and when would you use it over a coroutine?",
                Set.of("android"), false, Set.of("persist", "background")));

        q.add(new InterviewQuestion("Q410",
                "What is the difference between launch and async in Kotlin coroutines?",
                Set.of("android", "kotlin"), false, Set.of("deferred", "await")));

        // =========================
        // iOS / Swift — Extended
        // =========================
        q.add(new InterviewQuestion("Q411",
                "What is an enum with associated values in Swift? Give an example.",
                Set.of("ios", "swift"), false, Set.of("associated", "case")));

        q.add(new InterviewQuestion("Q412",
                "What is the Codable protocol in Swift? How does it handle JSON encoding and decoding?",
                Set.of("ios", "swift"), false, Set.of("codable", "decode")));

        q.add(new InterviewQuestion("Q413",
                "How do you make a network request in Swift using URLSession?",
                Set.of("ios", "swift"), false, Set.of("urlsession", "task")));

        q.add(new InterviewQuestion("Q414",
                "UserDefaults vs CoreData vs Keychain — when do you use each for iOS storage?",
                Set.of("ios", "swift"), false, Set.of("keychain", "sensitive")));

        q.add(new InterviewQuestion("Q415",
                "What is @State and @Binding in SwiftUI and how do they relate?",
                Set.of("ios", "swift"), false, Set.of("state", "binding")));

        q.add(new InterviewQuestion("Q416",
                "What is a retain cycle in Swift and how do you prevent it?",
                Set.of("ios", "swift"), true, Set.of("weak", "cycle")));

        q.add(new InterviewQuestion("Q417",
                "What is an @escaping closure in Swift? Why does it matter for async code?",
                Set.of("ios", "swift"), false, Set.of("escaping", "outlive")));

        q.add(new InterviewQuestion("Q418",
                "How do you write a unit test in Swift using XCTest?",
                Set.of("ios", "swift", "testing"), false, Set.of("xctest", "assert")));

        // =========================
        // Data Science / ML — Extended
        // =========================
        q.add(new InterviewQuestion("Q419",
                "What is the bias-variance tradeoff in machine learning?",
                Set.of("ml"), true, Set.of("bias", "variance")));

        q.add(new InterviewQuestion("Q420",
                "What is a decision tree? What are its main advantages and disadvantages?",
                Set.of("ml"), false, Set.of("split", "overfit")));

        q.add(new InterviewQuestion("Q421",
                "What is a Random Forest and how does it improve on a single decision tree?",
                Set.of("ml"), false, Set.of("ensemble", "bagging")));

        q.add(new InterviewQuestion("Q422",
                "What is K-means clustering? Walk me through how the algorithm works.",
                Set.of("ml"), false, Set.of("centroid", "cluster")));

        q.add(new InterviewQuestion("Q423",
                "What is PCA (Principal Component Analysis) at a high level and why is it used?",
                Set.of("ml"), false, Set.of("dimension", "variance")));

        q.add(new InterviewQuestion("Q424",
                "What is hyperparameter tuning? Name two methods for doing it.",
                Set.of("ml"), false, Set.of("grid", "search")));

        q.add(new InterviewQuestion("Q425",
                "What is an A/B test? How do you determine if a result is statistically significant?",
                Set.of("ml"), false, Set.of("significance", "control")));

        q.add(new InterviewQuestion("Q426",
                "What is model drift and how would you detect it in a production system?",
                Set.of("ml"), false, Set.of("drift", "monitor")));

        q.add(new InterviewQuestion("Q427",
                "Your model performs well in training but poorly in production. What are the most likely causes?",
                Set.of("ml"), true, Set.of("distribution", "leakage")));

        q.add(new InterviewQuestion("Q428",
                "What is tokenization in NLP and why is it a necessary preprocessing step?",
                Set.of("ml"), false, Set.of("token", "text")));

        // =========================
        // DBA / Advanced SQL — Extended
        // =========================
        q.add(new InterviewQuestion("Q429",
                "What is table partitioning in PostgreSQL? Give a use-case where it helps.",
                Set.of("sql", "postgresql"), false, Set.of("partition", "range")));

        q.add(new InterviewQuestion("Q430",
                "What is VACUUM in PostgreSQL and why does it matter for performance?",
                Set.of("sql", "postgresql"), false, Set.of("vacuum", "bloat")));

        q.add(new InterviewQuestion("Q431",
                "What is JSONB in PostgreSQL? When would you choose it over a normalised column?",
                Set.of("sql", "postgresql"), false, Set.of("jsonb", "flexible")));

        q.add(new InterviewQuestion("Q432",
                "What is full-text search in PostgreSQL (tsvector, tsquery)? How does it differ from LIKE?",
                Set.of("sql", "postgresql"), false, Set.of("tsvector", "search")));

        q.add(new InterviewQuestion("Q433",
                "What is a database sequence? Why are gaps in sequences normal and expected?",
                Set.of("sql"), false, Set.of("sequence", "gap")));

        q.add(new InterviewQuestion("Q434",
                "What are the main PostgreSQL backup strategies and their trade-offs?",
                Set.of("sql", "postgresql", "devops"), false, Set.of("pg_dump", "wal")));

        q.add(new InterviewQuestion("Q435",
                "What is replication lag? How do you detect and mitigate it?",
                Set.of("sql", "postgresql"), false, Set.of("lag", "replica")));

        q.add(new InterviewQuestion("Q436",
                "What is advisory locking in PostgreSQL and when would you use it?",
                Set.of("sql", "postgresql"), false, Set.of("advisory", "application")));

        // =========================
        // Security — Extended
        // =========================
        q.add(new InterviewQuestion("Q437",
                "What is an SSRF (Server-Side Request Forgery) attack? Give an example.",
                Set.of("security"), false, Set.of("internal", "request")));

        q.add(new InterviewQuestion("Q438",
                "What is a path traversal attack? How do you prevent it in a file upload endpoint?",
                Set.of("security"), false, Set.of("traversal", "sanitize")));

        q.add(new InterviewQuestion("Q439",
                "How should you store secrets (API keys, DB passwords) in a production application?",
                Set.of("security", "devops"), true, Set.of("vault", "env")));

        q.add(new InterviewQuestion("Q440",
                "What are HTTP security headers? Name three important ones and what they do.",
                Set.of("security"), false, Set.of("hsts", "csp")));

        q.add(new InterviewQuestion("Q441",
                "What is rate limiting as a security measure and what attacks does it prevent?",
                Set.of("security", "api"), true, Set.of("brute", "throttle")));

        q.add(new InterviewQuestion("Q442",
                "What is a supply chain attack? Give a real-world example.",
                Set.of("security"), false, Set.of("dependency", "malicious")));

        q.add(new InterviewQuestion("Q443",
                "What is a dangerous CORS misconfiguration and what can an attacker do with it?",
                Set.of("security", "api"), false, Set.of("wildcard", "origin")));

        q.add(new InterviewQuestion("Q444",
                "What is log injection and how do you prevent it?",
                Set.of("security"), false, Set.of("sanitize", "inject")));

        q.add(new InterviewQuestion("Q445",
                "What is the difference between a vulnerability, an exploit, and a patch?",
                Set.of("security"), false, Set.of("vulnerability", "exploit")));

        q.add(new InterviewQuestion("Q446",
                "What is zero-trust architecture at a high level?",
                Set.of("security"), false, Set.of("verify", "trust")));

        // =========================
        // Embedded / Systems — Extended
        // =========================
        q.add(new InterviewQuestion("Q447",
                "What is memory-mapped I/O in embedded systems?",
                Set.of("embedded"), false, Set.of("register", "mapped")));

        q.add(new InterviewQuestion("Q448",
                "What is a watchdog timer and why is it critical in embedded devices?",
                Set.of("embedded"), false, Set.of("watchdog", "reset")));

        q.add(new InterviewQuestion("Q449",
                "What is DMA (Direct Memory Access) and when would you use it?",
                Set.of("embedded"), false, Set.of("dma", "cpu")));

        q.add(new InterviewQuestion("Q450",
                "What is the difference between SPI, I2C, and UART communication protocols?",
                Set.of("embedded"), true, Set.of("spi", "uart")));

        q.add(new InterviewQuestion("Q451",
                "What is a bootloader in embedded systems? What does it do before the main application runs?",
                Set.of("embedded"), false, Set.of("bootloader", "firmware")));

        q.add(new InterviewQuestion("Q452",
                "What is power management in embedded systems and how do sleep modes help?",
                Set.of("embedded"), false, Set.of("sleep", "consumption")));

        q.add(new InterviewQuestion("Q453",
                "What makes unit testing in embedded systems harder than in regular software?",
                Set.of("embedded", "testing"), false, Set.of("hardware", "mock")));

        q.add(new InterviewQuestion("Q454",
                "What is a linker script in C/C++ embedded development and what does it control?",
                Set.of("embedded"), false, Set.of("linker", "memory")));

        // =========================
        //// MCQ (Phase C)
        //// =========================
        q.add(new InterviewQuestion("MCQ1",
                "HTTP status 201 means:",
                Set.of("rest", "api"),
                true,
                List.of(
                        "Request succeeded and returned a representation",
                        "Resource created successfully",
                        "No content",
                        "Unauthorized"
                ),
                1
        ));

        q.add(new InterviewQuestion("MCQ2",
                "Which HTTP method is idempotent?",
                Set.of("rest", "api"),
                true,
                List.of("POST", "PATCH", "PUT", "CONNECT"),
                2
        ));

        // =========================
        // CODE (Phase C - minimal, still keyword-based evaluation)
        // =========================
        q.add(new InterviewQuestion("CODE1",
                "Write a SQL query to return all users ordered by created_at descending.",
                Set.of("sql"),
                false,
                // starterCode should be a scaffold (NOT the full solution)
                "",
                Set.of("select", "from", "order", "by", "desc")
        ));


        // =========================
        // MCQ (Phase C) - EXTENDED
        // Target total MCQ = 100 (existing 2 + add 98 below)
        // =========================

        // Format:
        // [id, question, tags(Set<String>), critical(boolean), options(List<String>), correctIndex(int)]
        Object[][] mcq = new Object[][]{

                // --- REST / HTTP / API ---
                {"MCQ3", "HTTP status 204 means:", Set.of("rest","api"), true,
                        List.of("Created","No Content","Unauthorized","Conflict"), 1},
                {"MCQ4", "Which header indicates the response media type?", Set.of("rest","api"), false,
                        List.of("Accept","Content-Type","Authorization","Host"), 1},
                {"MCQ5", "Idempotent HTTP methods include:", Set.of("rest","api"), true,
                        List.of("POST only","PUT and DELETE","PATCH only","CONNECT and TRACE"), 1},
                {"MCQ6", "CORS is primarily enforced by:", Set.of("rest","api","security"), false,
                        List.of("Backend server","Browser","Database","API Gateway"), 1},
                {"MCQ7", "401 vs 403:", Set.of("rest","api","security"), true,
                        List.of("401=forbidden, 403=unauthorized","401=unauthorized, 403=forbidden","Both mean the same","401 is success"), 1},
                {"MCQ8", "Best practice for pagination in large datasets:", Set.of("rest","api"), true,
                        List.of("Offset always","Cursor-based pagination","Return all rows","No ordering needed"), 1},
                {"MCQ9", "A DTO is used to:", Set.of("api","design patterns"), true,
                        List.of("Expose DB entities directly","Separate API contract from persistence model","Replace controllers","Avoid JSON"), 1},
                {"MCQ10", "Common REST best practice:", Set.of("rest","api"), false,
                        List.of("Fat controllers","Validate inputs","Return stack traces to client","Use SELECT * always"), 1},

                // --- Java Core ---
                {"MCQ11", "== vs equals() in Java:", Set.of("java"), true,
                        List.of("Both compare content","== compares references, equals() compares content by contract","equals() compares references","== compares objects deeply"), 1},
                {"MCQ12", "When overriding equals(), you should also override:", Set.of("java"), true,
                        List.of("toString()","hashCode()","finalize()","clone()"), 1},
                {"MCQ13", "HashMap average get() time complexity:", Set.of("java"), true,
                        List.of("O(n)","O(1)","O(log n)","O(n log n)"), 1},
                {"MCQ14", "ArrayList is typically better than LinkedList for:", Set.of("java"), true,
                        List.of("Random access","Frequent inserts at head","Constant-time removals anywhere","No resizing"), 0},
                {"MCQ15", "Checked exceptions are:", Set.of("java"), false,
                        List.of("Must be caught/declared","Only RuntimeException","Ignored by compiler","Always fatal"), 0},
                {"MCQ16", "String in Java is:", Set.of("java"), true,
                        List.of("Mutable","Immutable","Thread-unsafe always","A primitive"), 1},
                {"MCQ17", "try-with-resources is used to:", Set.of("java"), false,
                        List.of("Speed up GC","Auto-close resources","Avoid exceptions","Disable logging"), 1},
                {"MCQ18", "Optional is mainly intended to:", Set.of("java"), false,
                        List.of("Replace all null checks everywhere","Represent optional return values","Store primitives only","Serialize JSON"), 1},
                {"MCQ19", "Volatile guarantees:", Set.of("java","concurrency"), true,
                        List.of("Mutual exclusion","Visibility of writes across threads","Prevents all races","Atomicity for all ops"), 1},
                {"MCQ20", "ExecutorService is used to:", Set.of("java","concurrency"), false,
                        List.of("Create DB indexes","Manage thread pools","Parse JSON","Handle HTTP routing"), 1},

                // --- OOP / Patterns ---
                {"MCQ21", "SOLID: 'S' stands for:", Set.of("oop","design patterns"), false,
                        List.of("Singleton","Single Responsibility","Serialization","Simple"), 1},
                {"MCQ22", "Strategy pattern is mainly about:", Set.of("design patterns"), false,
                        List.of("Global shared instance","Swappable behavior via interface","Building objects step-by-step","Observing events"), 1},
                {"MCQ23", "Factory pattern helps when:", Set.of("design patterns"), false,
                        List.of("You need object creation logic centralized","You want only one instance","You need database transactions","You want recursion"), 0},
                {"MCQ24", "High cohesion and low coupling is:", Set.of("oop"), true,
                        List.of("Generally desirable","Generally undesirable","Only for microservices","Only for frontend"), 0},

                // --- Spring / Spring Boot ---
                {"MCQ25", "@RestController is equivalent to:", Set.of("spring","spring boot","rest"), true,
                        List.of("@Controller + @ResponseBody","@Service + @Repository","@Bean + @Configuration","@Entity + @Table"), 0},
                {"MCQ26", "Preferred dependency injection style:", Set.of("spring"), true,
                        List.of("Field injection","Constructor injection","Static injection","Reflection injection"), 1},
                {"MCQ27", "@Transactional mainly provides:", Set.of("spring","sql"), true,
                        List.of("Caching","Transaction boundary management","CORS","JWT signing"), 1},
                {"MCQ28", "Spring profiles are used to:", Set.of("spring boot"), false,
                        List.of("Version APIs","Select environment-specific config","Replace Maven","Encrypt DB"), 1},
                {"MCQ29", "@ControllerAdvice is used for:", Set.of("spring","rest"), true,
                        List.of("DB migrations","Global exception handling","Async scheduling only","Logging only"), 1},
                {"MCQ30", "Filter vs Interceptor:", Set.of("spring"), false,
                        List.of("Same thing","Filters are servlet-level, interceptors are Spring MVC-level","Interceptors only for DB","Filters only for Kafka"), 1},
                {"MCQ31", "Bean scope default is:", Set.of("spring"), true,
                        List.of("Prototype","Singleton","Request","Session"), 1},
                {"MCQ32", "Actuator is mainly for:", Set.of("spring boot"), false,
                        List.of("UI components","Ops endpoints/metrics/health","SQL parsing","JWT encryption"), 1},

                // --- Hibernate / JPA ---
                {"MCQ33", "Lazy loading means:", Set.of("hibernate"), true,
                        List.of("Always loads relations immediately","Loads relations when accessed","Disables caching","Loads on commit only"), 1},
                {"MCQ34", "N+1 problem is commonly solved by:", Set.of("hibernate","sql"), true,
                        List.of("More indexes","Fetch join / entity graphs","More DTOs only","Turning off transactions"), 1},
                {"MCQ35", "First-level cache in Hibernate is:", Set.of("hibernate"), false,
                        List.of("Global cache shared across app","Persistence context / session cache","Redis","Elasticsearch"), 1},
                {"MCQ36", "Optimistic locking uses:", Set.of("hibernate","sql"), false,
                        List.of("@Version field","Synchronized blocks","Dead letter queue","CORS headers"), 0},

                // --- SQL / PostgreSQL ---
                {"MCQ37", "WHERE vs HAVING:", Set.of("sql"), true,
                        List.of("Same thing","WHERE filters rows, HAVING filters groups","HAVING filters rows, WHERE filters groups","Only MySQL has HAVING"), 1},
                {"MCQ38", "COUNT(column) differs from COUNT(*) because:", Set.of("sql"), false,
                        List.of("COUNT(column) ignores NULLs","COUNT(*) ignores NULLs","They are identical always","COUNT(*) is invalid"), 0},
                {"MCQ39", "Best index trade-off statement:", Set.of("sql"), true,
                        List.of("Indexes speed reads but slow writes","Indexes speed writes only","Indexes remove need for joins","Indexes replace transactions"), 0},
                {"MCQ40", "ACID 'I' stands for:", Set.of("sql"), false,
                        List.of("Integration","Isolation","Iteration","Inlining"), 1},
                {"MCQ41", "PostgreSQL UPSERT keyword pattern:", Set.of("sql","postgresql"), false,
                        List.of("MERGE INTO","INSERT ... ON CONFLICT","REPLACE INTO","UPSERT INTO"), 1},

                // --- NoSQL / Redis / ES ---
                {"MCQ42", "Redis is commonly used for:", Set.of("redis"), true,
                        List.of("Long-term cold storage","Caching / fast key-value","Image rendering","Kubernetes scheduling"), 1},
                {"MCQ43", "TTL means:", Set.of("redis"), false,
                        List.of("Total Transfer Load","Time To Live","Thread To Lock","Type To List"), 1},
                {"MCQ44", "CAP theorem is about:", Set.of("nosql","microservices"), false,
                        List.of("Caching","Consistency/Availability/Partition tolerance","Authentication","Compression"), 1},
                {"MCQ45", "Elasticsearch is optimized for:", Set.of("elasticsearch"), true,
                        List.of("OLTP transactions","Full-text search & indexing","Binary protocols","Primary keys only"), 1},

                // --- Microservices ---
                {"MCQ46", "API Gateway typically handles:", Set.of("microservices","api"), false,
                        List.of("DB schema migrations only","Routing/auth/rate limit","Garbage collection","Kafka partitions"), 1},
                {"MCQ47", "Circuit breaker purpose:", Set.of("microservices","design patterns"), false,
                        List.of("Increase DB writes","Prevent cascading failures","Improve UI rendering","Encrypt JWT"), 1},
                {"MCQ48", "Eventual consistency means:", Set.of("microservices","nosql"), false,
                        List.of("Always consistent","May be temporarily inconsistent but converges","Never consistent","Only for SQL"), 1},

                // --- Docker / Kubernetes / DevOps ---
                {"MCQ49", "Docker image vs container:", Set.of("docker"), true,
                        List.of("Same thing","Image is blueprint, container is running instance","Container is blueprint","Image is always running"), 1},
                {"MCQ50", "CMD vs ENTRYPOINT:", Set.of("docker"), false,
                        List.of("No difference","ENTRYPOINT defines executable, CMD default args","CMD builds image layers","ENTRYPOINT sets env vars"), 1},
                {"MCQ51", "Kubernetes pod is:", Set.of("kubernetes"), true,
                        List.of("A VM","Smallest deployable unit (one/more containers)","A registry","A load balancer"), 1},
                {"MCQ52", "ConfigMap vs Secret:", Set.of("kubernetes","devops"), false,
                        List.of("Secrets are for non-sensitive data","Secrets are for sensitive data","ConfigMap encrypts by default","No difference"), 1},
                {"MCQ53", "CI/CD goal:", Set.of("ci/cd"), true,
                        List.of("Manual deployments only","Automate build/test/deploy","Replace Git","Disable logs"), 1},

                // --- Kafka / Messaging ---
                {"MCQ54", "Kafka topic partition helps with:", Set.of("kafka"), true,
                        List.of("UI rendering","Parallelism/throughput","Encryption","CORS"), 1},
                {"MCQ55", "Consumer group allows:", Set.of("kafka"), false,
                        List.of("All consumers read all messages always","Load balancing partitions among consumers","Only one consumer in system","Only producers"), 1},
                {"MCQ56", "At-least-once delivery downside:", Set.of("kafka"), false,
                        List.of("Lost messages","Duplicates possible","Always exactly once","No ordering"), 1},

                // --- Testing ---
                {"MCQ57", "Unit tests should be:", Set.of("testing"), true,
                        List.of("Slow and DB-heavy","Fast and isolated","Dependent on network","Only manual"), 1},
                {"MCQ58", "Mockito is used to:", Set.of("testing","mockito"), true,
                        List.of("Manage DB migrations","Mock dependencies","Deploy Docker","Parse logs"), 1},
                {"MCQ59", "Integration test risk:", Set.of("testing","sql"), false,
                        List.of("Too fast","Flaky/slow due to external deps","Never fails","No value"), 1},
                {"MCQ60", "AAA stands for:", Set.of("testing"), false,
                        List.of("Act-Assert-Arrange","Arrange-Act-Assert","Assert-Arrange-Act","Auto-Assert-Act"), 1},

                // --- Git / Linux ---
                {"MCQ61", "git pull is:", Set.of("git"), true,
                        List.of("Upload local commits","Fetch + merge (or rebase) from remote","Delete branch","Create tag"), 1},
                {"MCQ62", "A merge conflict happens when:", Set.of("git"), true,
                        List.of("Two changes can’t be auto-merged","You forgot git init","You didn’t push","Your IDE is closed"), 0},
                {"MCQ63", "chmod affects:", Set.of("linux","bash"), false,
                        List.of("CPU scheduling","File permissions","Network routing","DB indexing"), 1},
                {"MCQ64", "stdout vs stderr:", Set.of("linux","bash"), false,
                        List.of("Both are same","Separate output streams","Only for Windows","Only for Java"), 1},

                // --- Extra: Security / OWASP basics ---
                {"MCQ65", "SQL Injection prevention:", Set.of("sql","security"), true,
                        List.of("String concatenation","Prepared statements/parameterized queries","Disable indexes","Use SELECT *"), 1},
                {"MCQ66", "XSS is mainly:", Set.of("api","security"), false,
                        List.of("DB-only issue","Script injection into pages/content","Only backend bug always","A Docker bug"), 1},
                {"MCQ67", "CSRF is relevant mostly when using:", Set.of("api","security"), false,
                        List.of("JWT in header only","Cookies for auth","gRPC","FTP"), 1},

                // --- Fillers to reach MCQ100 (more depth per topic) ---
                {"MCQ68", "HTTP caching header commonly used:", Set.of("rest","api"), false,
                        List.of("Cache-Control","X-Cache","Keep-Alive","Accept-Language"), 0},
                {"MCQ69", "PUT is typically:", Set.of("rest","api"), false,
                        List.of("Partial update","Full replace of resource","Only reads","Never idempotent"), 1},
                {"MCQ70", "A race condition occurs when:", Set.of("concurrency"), true,
                        List.of("Two threads access shared state without proper sync","GC runs","DB has index","REST returns 200"), 0},
                {"MCQ71", "Deadlock requires:", Set.of("concurrency"), true,
                        List.of("No locks","Circular wait on locks","Only one thread","Only SQL"), 1},
                {"MCQ72", "Spring Bean lifecycle is managed by:", Set.of("spring"), false,
                        List.of("JVM only","Spring container","Database","Kafka broker"), 1},
                {"MCQ73", "JPQL queries operate on:", Set.of("hibernate","sql"), false,
                        List.of("Tables only","Entities and their fields","Docker images","Redis keys"), 1},
                {"MCQ74", "Database normalization aims to reduce:", Set.of("sql"), false,
                        List.of("Throughput","Redundancy/anomalies","Indexes","Latency always"), 1},
                {"MCQ75", "Serializable isolation level is:", Set.of("sql"), false,
                        List.of("Weakest","Strongest (most strict)","Only in MongoDB","Same as Read Committed"), 1},
                {"MCQ76", "Redis persistence risk relates to:", Set.of("redis"), false,
                        List.of("It never loses data","Data is in memory and persistence config matters","It is always durable","It’s a SQL DB"), 1},
                {"MCQ77", "Kafka ordering is guaranteed:", Set.of("kafka"), false,
                        List.of("Across all partitions","Within a partition","Never","Only for consumers"), 1},
                {"MCQ78", "Canary deployment means:", Set.of("ci/cd","microservices"), false,
                        List.of("Deploy to all users at once","Gradual rollout to small subset","Rollback disabled","Only for DB"), 1},
                {"MCQ79", "Docker layer caching helps with:", Set.of("docker"), false,
                        List.of("Faster builds when layers unchanged","Faster DB joins","JWT signing","GC tuning"), 0},
                {"MCQ80", "K8s Deployment manages:", Set.of("kubernetes"), false,
                        List.of("ConfigMaps only","ReplicaSets/rolling updates","DB schema","Git branches"), 1},
                {"MCQ81", "Load balancer purpose:", Set.of("microservices","devops"), true,
                        List.of("Persist sessions only","Distribute traffic across instances","Encrypt DB","Cache Redis"), 1},
                {"MCQ82", "Horizontal scaling is:", Set.of("microservices","devops"), false,
                        List.of("Bigger machine","More instances","More RAM only","Less traffic"), 1},
                {"MCQ83", "Best logging practice:", Set.of("devops"), false,
                        List.of("Log passwords","Use structured logs","Disable logs in prod","Log only stack traces"), 1},
                {"MCQ84", "JUnit @Test indicates:", Set.of("junit","testing"), false,
                        List.of("A production endpoint","A test method","A database table","A Docker layer"), 1},
                {"MCQ85", "Mocking too much sign:", Set.of("testing"), false,
                        List.of("Tests are simple","Tests break on refactor despite same behavior","No dependencies mocked","Coverage is high"), 1},
                {"MCQ86", "OAuth2 is mainly for:", Set.of("api","security"), false,
                        List.of("DB indexing","Delegated authorization","Garbage collection","K8s orchestration"), 1},
                {"MCQ87", "JWT risk if storing sensitive data:", Set.of("api","security"), true,
                        List.of("Nothing, it’s always encrypted","Token can be decoded if not encrypted; leaks data","Improves security","Prevents CSRF always"), 1},
                {"MCQ88", "REST 409 usually indicates:", Set.of("rest","api"), false,
                        List.of("Server crash","Conflict with current state (e.g., version/duplicate)","Authentication failed","Not found"), 1},
                {"MCQ89", "DTO helps prevent:", Set.of("api","design patterns"), false,
                        List.of("Overfetching only","Leaking internal entity structure","Git conflicts","CPU spikes"), 1},
                {"MCQ90", "A foreign key enforces:", Set.of("sql"), true,
                        List.of("Uniqueness of column always","Referential integrity","Faster reads","No NULLs"), 1},
                {"MCQ91", "Composite index is useful when:", Set.of("sql"), false,
                        List.of("Query filters/sorts by multiple columns","You never filter","Only INSERT workloads","Only in Redis"), 0},
                {"MCQ92", "DELETE vs TRUNCATE:", Set.of("sql"), false,
                        List.of("Same always","TRUNCATE is typically faster and resets table (DB-dependent)","DELETE drops schema","TRUNCATE logs more"), 1},
                {"MCQ93", "Service discovery in microservices:", Set.of("microservices"), false,
                        List.of("Static IPs only","Finding service instances dynamically","SQL query optimization","Browser feature"), 1},
                {"MCQ94", "Distributed tracing uses:", Set.of("microservices"), false,
                        List.of("Correlation/trace IDs","GC logs only","DB indexes","CORS headers"), 0},
                {"MCQ95", "Backpressure relates to:", Set.of("microservices","kafka"), false,
                        List.of("Too fast producers/slow consumers handling","JWT refresh","Git rebasing","SQL joins"), 0},
                {"MCQ96", "Docker-compose defines:", Set.of("docker"), false,
                        List.of("K8s pods","Multi-container local stacks/services","Git branches","SQL migrations"), 1},
                {"MCQ97", "A Kubernetes Service (ClusterIP) provides:", Set.of("kubernetes"), false,
                        List.of("Stable virtual IP/DNS to pods","DB replication","JWT signing","CI pipeline"), 0},
                {"MCQ98", "Infrastructure as Code means:", Set.of("devops"), false,
                        List.of("Manual clicks","Managing infra with declarative code","Only Java code","Only SQL code"), 1},
                {"MCQ99", "Strongly typed language implies:", Set.of("java","go","c#","python"), false,
                        List.of("No types","Types enforced at compile time (generally)","Only runtime checks","Only for SQL"), 1},
                {"MCQ100", "JSON is commonly used because:", Set.of("api","javascript"), false,
                        List.of("It is binary","It is lightweight text and widely supported","It replaces HTTP","It prevents XSS"), 1},
        };

        for (Object[] row : mcq) {
            q.add(new InterviewQuestion(
                    (String) row[0],
                    (String) row[1],
                    (Set<String>) row[2],
                    (Boolean) row[3],
                    (List<String>) row[4],
                    (Integer) row[5]
            ));
        }

        // =========================
        // CODE (Phase C) - EXTENDED
        // Target total CODE = 57 (existing 1 + 49 backend + 7 frontend/React/JS)
        // =========================

        // Format:
        // [id, text, tags, critical, starterCode(scaffold), requiredKeywords(Set<String>)]
        Object[][] code = new Object[][]{

                // --- SQL ---
                {"CODE2", "Write a SQL query that returns only admin users.", Set.of("sql"), false,
                        "SELECT *\nFROM users\nWHERE /* condition */;",
                        Set.of("select","from","where","admin")},
                {"CODE3", "Write a SQL query that returns the latest 10 users by created_at.", Set.of("sql"), false,
                        "SELECT *\nFROM users\nORDER BY created_at DESC\nLIMIT /* n */;",
                        Set.of("select","from","order","by","desc","limit","created_at")},
                {"CODE4", "Write a SQL query to count users per country.", Set.of("sql"), false,
                        "SELECT country, COUNT(*)\nFROM users\nGROUP BY country;",
                        Set.of("select","count","group","by")},
                {"CODE5", "Write a SQL query to return users created in the last 7 days (PostgreSQL).", Set.of("sql","postgresql"), false,
                        "SELECT *\nFROM users\nWHERE created_at >= NOW() - INTERVAL '7 days';",
                        Set.of("select","from","where","now","interval","created_at")},

                // --- Java ---
                {"CODE6", "Write a Java method that returns true if a string is a palindrome.", Set.of("java"), false,
                        "",
                        Set.of("return","boolean","char","length")},
                {"CODE7", "Write a Java method that filters even numbers from a List<Integer> using streams.", Set.of("java"), false,
                        "",
                        Set.of("stream","filter","collect")},
                {"CODE8", "Write Java code to safely get a value from Optional<String> with a default.", Set.of("java"), false,
                        "",
                        Set.of("optional","orElse")},
                {"CODE9", "Write Java code that sorts a list of strings by length ascending.", Set.of("java"), false,
                        "",
                        Set.of("sort","comparator","length")},

                // --- Concurrency ---
                {"CODE10", "Write Java code using ExecutorService to run 5 tasks and wait for completion.", Set.of("java","concurrency"), false,
                        "",
                        Set.of("executor","submit","shutdown","await")},
                {"CODE11", "Write Java code to use synchronized to protect a critical section.", Set.of("java","concurrency"), false,
                        "",
                        Set.of("synchronized","lock")},

                // --- Spring / REST ---
                {"CODE12", "Create a Spring Boot @RestController with GET /health that returns 200 OK.", Set.of("spring","rest"), false,
                        "",
                        Set.of("restcontroller","getmapping","health")},
                {"CODE13", "Write a Spring controller method that reads a path variable 'id'.", Set.of("spring","rest"), false,
                        "",
                        Set.of("pathvariable","id")},
                {"CODE14", "Write a DTO class with fields name,email and getters/setters.", Set.of("api","design patterns"), false,
                        "",
                        Set.of("class","private","get","set")},

                // --- JPA / Hibernate ---
                {"CODE15", "Create a JPA entity User with id and email fields.", Set.of("hibernate","spring"), false,
                        "",
                        Set.of("entity","id","column")},
                {"CODE16", "Write a Spring Data repository interface for User with findByEmail.", Set.of("hibernate","spring"), false,
                        "",
                        Set.of("repository","extends","findByEmail")},

                // --- Docker / K8s ---
                {"CODE17", "Write a minimal Dockerfile for a Spring Boot jar (multi-stage optional).", Set.of("docker"), false,
                        "FROM eclipse-temurin:17-jre\nWORKDIR /app\nCOPY app.jar app.jar\nENTRYPOINT [\"java\",\"-jar\",\"app.jar\"]",
                        Set.of("from","copy","entrypoint","java")},
                {"CODE18", "Write a docker-compose service for postgres with port 5432 and env vars.", Set.of("docker"), false,
                        "services:\n  db:\n    image: postgres\n    ports:\n      - \"5432:5432\"\n    environment:\n      - POSTGRES_PASSWORD=postgres",
                        Set.of("services","image","ports","environment")},
                {"CODE19", "Write a Kubernetes Deployment snippet with 2 replicas (high level).", Set.of("kubernetes"), false,
                        "apiVersion: apps/v1\nkind: Deployment\nspec:\n  replicas: 2",
                        Set.of("deployment","replicas")},

                // --- Microservices ---
                {"CODE20", "Design: write JSON for a structured error response {errorCode,message,details}.", Set.of("microservices","api"), false,
                        "{\n  \"errorCode\": \"...\",\n  \"message\": \"...\",\n  \"details\": []\n}",
                        Set.of("errorCode","message","details")},
                {"CODE21", "Write pseudo-code for retry with backoff (high level).", Set.of("microservices"), false,
                        "",
                        Set.of("retry","backoff","attempt")},

                // --- Testing ---
                {"CODE22", "Write a JUnit test skeleton for a sum(a,b) method.", Set.of("testing","junit"), false,
                        "",
                        Set.of("@test","assert")},
                {"CODE23", "Write Mockito code to mock a dependency and verify it was called once.", Set.of("testing","mockito"), false,
                        "",
                        Set.of("mock","when","verify")},

                // --- Git / Bash ---
                {"CODE24", "Write the git commands to create a branch, commit, and push it to origin.", Set.of("git"), false,
                        "",
                        Set.of("git","checkout","commit","push")},
                {"CODE25", "Write a basic curl command to call GET http://localhost:8080/health", Set.of("bash","api"), false,
                        "",
                        Set.of("curl","http")},

                // --- More SQL (to reach 50 total CODE) ---
                {"CODE26", "Write a SQL query to join users and orders on user_id and return user email + order id.", Set.of("sql"), false,
                        "SELECT /* columns */\nFROM users u\nJOIN orders o ON u.id = o.user_id;",
                        Set.of("select","from","join","on")},
                {"CODE27", "Write a SQL query to return only users with more than 5 orders.", Set.of("sql"), false,
                        "SELECT u.id\nFROM users u\nJOIN orders o ON u.id = o.user_id\nGROUP BY u.id\nHAVING COUNT(*) > 5;",
                        Set.of("group","by","having","count")},
                {"CODE28", "Write a SQL query to update a user's email by id.", Set.of("sql"), false,
                        "UPDATE users\nSET email = /* value */\nWHERE id = /* id */;",
                        Set.of("update","set","where")},
                {"CODE29", "Write a SQL query to delete users that are inactive.", Set.of("sql"), false,
                        "DELETE FROM users\nWHERE /* condition */;",
                        Set.of("delete","from","where")},
                {"CODE30", "Write a PostgreSQL upsert for users(id,email).", Set.of("sql","postgresql"), false,
                        "INSERT INTO users(id,email)\nVALUES(/*id*/,/*email*/)\nON CONFLICT (id) DO UPDATE SET email = EXCLUDED.email;",
                        Set.of("insert","on","conflict","do","update")},

                // --- More Java/Spring ---
                {"CODE31", "Write Java code to read a file using try-with-resources (high level).", Set.of("java"), false,
                        "",
                        Set.of("try","resource","close")},
                {"CODE32", "Write a Spring @Service with a method that returns a greeting string.", Set.of("spring"), false,
                        "",
                        Set.of("@service","public","return")},
                {"CODE33", "Write a Spring validation example using @Valid on a request body.", Set.of("spring","api"), false,
                        "",
                        Set.of("@valid","requestbody")},
                {"CODE34", "Write a Spring exception handler method that returns 400 for IllegalArgumentException.", Set.of("spring","rest"), false,
                        "",
                        Set.of("exceptionhandler","badrequest","400")},

                // --- More DevOps/K8s ---
                {"CODE35", "Write a minimal Kubernetes Service snippet (ClusterIP) selecting app=api.", Set.of("kubernetes"), false,
                        "kind: Service\nspec:\n  selector:\n    app: api\n  ports:\n    - port: 80\n      targetPort: 8080",
                        Set.of("service","selector","ports")},
                {"CODE36", "Write a minimal GitHub Actions pipeline steps: build + test.", Set.of("ci/cd"), false,
                        "",
                        Set.of("checkout","build","test")},

                // --- Messaging ---
                {"CODE37", "Write pseudo-code for an idempotent consumer handling duplicate messages.", Set.of("kafka","microservices"), false,
                        "",
                        Set.of("idempotent","dedupe","processed")},

                // --- Testing extras ---
                {"CODE38", "Write a Spring MockMvc test skeleton for GET /health expecting 200.", Set.of("testing","spring boot"), false,
                        "",
                        Set.of("mockmvc","perform","andExpect")},

                // --- Final fillers to reach CODE50 total (we add 49 here, plus CODE1 already exists) ---
                {"CODE39", "Write SQL to select users where email ends with '@gmail.com'.", Set.of("sql"), false,
                        "SELECT * FROM users WHERE email LIKE '%@gmail.com';",
                        Set.of("select","from","where","like")},
                {"CODE40", "Write SQL to get distinct countries from users.", Set.of("sql"), false,
                        "SELECT DISTINCT country FROM users;",
                        Set.of("select","distinct","from")},
                {"CODE41", "Write Java code to create a HashMap and put/get a value.", Set.of("java"), false,
                        "",
                        Set.of("hashmap","put","get")},
                {"CODE42", "Write Java code that catches an exception and logs a message.", Set.of("java"), false,
                        "",
                        Set.of("try","catch","exception")},
                {"CODE43", "Write a simple SQL table DDL for users(id,email) with primary key.", Set.of("sql"), false,
                        "CREATE TABLE users (\n  id BIGINT PRIMARY KEY,\n  email TEXT NOT NULL\n);",
                        Set.of("create","table","primary","key")},
                {"CODE44", "Write a SQL query that returns top 3 highest salaries from employees.", Set.of("sql"), false,
                        "SELECT * FROM employees ORDER BY salary DESC LIMIT 3;",
                        Set.of("order","by","desc","limit")},
                {"CODE45", "Write a SQL query with GROUP BY to compute avg salary per department.", Set.of("sql"), false,
                        "SELECT department, AVG(salary) FROM employees GROUP BY department;",
                        Set.of("avg","group","by")},
                {"CODE46", "Write a Docker command to run a container exposing port 8080.", Set.of("docker"), false,
                        "",
                        Set.of("docker","run","-p","8080")},
                {"CODE47", "Write a git command to undo last commit but keep changes staged.", Set.of("git"), false,
                        "",
                        Set.of("reset","soft")},
                {"CODE48", "Write SQL to add an index on users(email).", Set.of("sql"), false,
                        "CREATE INDEX idx_users_email ON users(email);",
                        Set.of("create","index","on")},
                {"CODE49", "Write pseudo steps to debug slow endpoint: logs, metrics, DB.", Set.of("devops","api","sql"), false,
                        "",
                        Set.of("logs","metrics","db")},
                {"CODE50", "Write a minimal JSON example of a JWT header+payload (no signature).", Set.of("security","api"), false,
                        "",
                        Set.of("alg","typ","sub")},

                // --- Frontend / React / JavaScript ---
                {"CODE51", "Write a React functional component with useState that renders a counter with increment/decrement buttons.", Set.of("react","javascript"), false,
                        "",
                        Set.of("usestate","onclick","return")},
                {"CODE52", "Write a React useEffect hook that fetches data from an API on component mount and stores it in state.", Set.of("react","javascript"), false,
                        "",
                        Set.of("useeffect","fetch","usestate")},
                {"CODE53", "Write a JavaScript function that debounces another function by a given delay in milliseconds.", Set.of("javascript"), false,
                        "",
                        Set.of("settimeout","cleartimeout","delay")},
                {"CODE54", "Write an async JavaScript function that fetches JSON from a URL, handling errors with try/catch.", Set.of("javascript","api"), false,
                        "",
                        Set.of("async","await","try","catch")},
                {"CODE55", "Write a custom React hook useLocalStorage(key, initialValue) that syncs state with localStorage.", Set.of("react","javascript"), false,
                        "",
                        Set.of("usestate","localstorage","useeffect")},
                {"CODE56", "Write a JavaScript function that flattens a nested array of arbitrary depth.", Set.of("javascript"), false,
                        "",
                        Set.of("flat","recursion","array")},
                {"CODE57", "Write a React component that renders a list of items, each with a unique key prop, and conditionally shows an empty-state message.", Set.of("react","javascript"), false,
                        "",
                        Set.of("map","key","return")}
        };

        for (Object[] row : code) {
            q.add(new InterviewQuestion(
                    (String) row[0],
                    (String) row[1],
                    (Set<String>) row[2],
                    (Boolean) row[3],
                    (String) row[4],
                    (Set<String>) row[5]
            ));
        }

        // =========================
        // Java 21 Modern Features
        // =========================
        q.add(new InterviewQuestion("Q455",
                "What are Java records? When would you use one instead of a regular class?",
                Set.of("java"), true, Set.of("record", "immutable")));

        q.add(new InterviewQuestion("Q456",
                "What are sealed classes in Java? What problem do they solve for modelling domain types?",
                Set.of("java"), false, Set.of("sealed", "permits")));

        q.add(new InterviewQuestion("Q457",
                "What are virtual threads (Project Loom) in Java 21? How do they differ from platform threads?",
                Set.of("java", "concurrency"), true, Set.of("virtual", "lightweight")));

        q.add(new InterviewQuestion("Q458",
                "What is pattern matching for instanceof (Java 16+)? Give a before/after example.",
                Set.of("java"), false, Set.of("instanceof", "pattern")));

        q.add(new InterviewQuestion("Q459",
                "What is a switch expression in Java (Java 14+)? How is it different from a switch statement?",
                Set.of("java"), false, Set.of("switch", "expression")));

        q.add(new InterviewQuestion("Q460",
                "What are text blocks in Java (Java 15+)? Give a use case.",
                Set.of("java"), false, Set.of("text", "block")));

        // =========================
        // Advanced Spring / Spring Boot
        // =========================
        q.add(new InterviewQuestion("Q461",
                "What is Spring WebFlux? When would you choose it over Spring MVC?",
                Set.of("spring boot", "concurrency"), true, Set.of("reactive", "nonblocking")));

        q.add(new InterviewQuestion("Q462",
                "What is a Spring Boot starter? What does spring-boot-starter-web include?",
                Set.of("spring boot"), false, Set.of("starter", "autoconfigure")));

        q.add(new InterviewQuestion("Q463",
                "What does @Scheduled do in Spring? What are common pitfalls?",
                Set.of("spring boot"), false, Set.of("scheduled", "cron")));

        q.add(new InterviewQuestion("Q464",
                "Explain Spring Boot Externalized Configuration: what is the property loading order?",
                Set.of("spring boot"), false, Set.of("environment", "override")));

        q.add(new InterviewQuestion("Q465",
                "What is Spring AOP? Explain @Aspect, pointcut, and advice with a real use-case.",
                Set.of("spring"), false, Set.of("aspect", "advice")));

        q.add(new InterviewQuestion("Q466",
                "What is the difference between @Transactional(readOnly=true) and a regular @Transactional?",
                Set.of("spring", "sql"), true, Set.of("readonly", "flush")));

        q.add(new InterviewQuestion("Q467",
                "How would you implement distributed locking in a Spring Boot application (Redis / DB-level)?",
                Set.of("spring boot", "redis", "concurrency"), false, Set.of("lock", "redis")));

        q.add(new InterviewQuestion("Q468",
                "What is Spring Retry? When would you annotate a method with @Retryable?",
                Set.of("spring boot", "api"), false, Set.of("retry", "backoff")));

        q.add(new InterviewQuestion("Q469",
                "Explain how Spring Boot health checks work. What is the difference between liveness and readiness probes?",
                Set.of("spring boot", "devops", "kubernetes"), true, Set.of("liveness", "readiness")));

        // =========================
        // System Design (Junior-Mid Level)
        // =========================
        q.add(new InterviewQuestion("Q470",
                "Design a notification system (email/SMS/push). What are the key components and why use a queue?",
                Set.of("design patterns", "kafka", "microservices"), true, Set.of("queue", "decouple")));

        q.add(new InterviewQuestion("Q471",
                "What is consistent hashing? Where is it used and why?",
                Set.of("microservices", "redis"), false, Set.of("consistent", "node")));

        q.add(new InterviewQuestion("Q472",
                "What is the token bucket algorithm for rate limiting? How does it differ from leaky bucket?",
                Set.of("api", "design patterns"), true, Set.of("token", "bucket")));

        q.add(new InterviewQuestion("Q473",
                "What is event sourcing? How does it differ from storing only current state?",
                Set.of("design patterns", "microservices"), false, Set.of("event", "audit")));

        q.add(new InterviewQuestion("Q474",
                "What is CQRS? When does separating read and write models make sense?",
                Set.of("design patterns", "microservices"), false, Set.of("command", "query")));

        q.add(new InterviewQuestion("Q475",
                "Design a simple file upload service: what validations, storage options, and security concerns would you address?",
                Set.of("api", "security", "aws"), true, Set.of("size", "type")));

        q.add(new InterviewQuestion("Q476",
                "What is the two-phase commit (2PC) and why is it avoided in modern distributed systems?",
                Set.of("microservices", "sql"), false, Set.of("coordinator", "blocking")));

        q.add(new InterviewQuestion("Q477",
                "How would you design a webhook system? What guarantees must you provide to consumers?",
                Set.of("api", "microservices"), false, Set.of("retry", "idempotent")));

        q.add(new InterviewQuestion("Q478",
                "What is the outbox pattern? How does it solve the dual-write problem?",
                Set.of("microservices", "kafka", "sql"), false, Set.of("outbox", "transaction")));

        // =========================
        // Advanced SQL / PostgreSQL
        // =========================
        q.add(new InterviewQuestion("Q479",
                "What is a partial index in PostgreSQL? Give a practical example of when it reduces index size.",
                Set.of("sql", "postgresql"), false, Set.of("partial", "where")));

        q.add(new InterviewQuestion("Q480",
                "What is row-level security (RLS) in PostgreSQL and why is it useful for multi-tenant apps?",
                Set.of("sql", "postgresql", "security"), false, Set.of("policy", "tenant")));

        q.add(new InterviewQuestion("Q481",
                "Explain the difference between SERIAL and IDENTITY columns in PostgreSQL.",
                Set.of("sql", "postgresql"), false, Set.of("sequence", "identity")));

        q.add(new InterviewQuestion("Q482",
                "What is a covering index? How does it avoid a heap fetch?",
                Set.of("sql", "postgresql"), false, Set.of("covering", "include")));

        q.add(new InterviewQuestion("Q483",
                "When would you use jsonb in PostgreSQL instead of a separate table? What are the trade-offs?",
                Set.of("sql", "postgresql"), true, Set.of("jsonb", "flexible")));

        // =========================
        // DevOps / Production Readiness
        // =========================
        q.add(new InterviewQuestion("Q484",
                "What is the difference between a liveness probe and a readiness probe in Kubernetes? What happens when each fails?",
                Set.of("kubernetes", "devops"), true, Set.of("liveness", "readiness")));

        q.add(new InterviewQuestion("Q485",
                "What is a Helm chart? What problem does it solve on top of plain Kubernetes YAML?",
                Set.of("kubernetes", "devops"), false, Set.of("helm", "template")));

        q.add(new InterviewQuestion("Q486",
                "What is a blue/green deployment? What is its main advantage over a rolling update?",
                Set.of("ci/cd", "devops"), true, Set.of("blue", "instant")));

        q.add(new InterviewQuestion("Q487",
                "What is a canary release? How do you roll it back if metrics degrade?",
                Set.of("ci/cd", "devops"), false, Set.of("canary", "rollback")));

        q.add(new InterviewQuestion("Q488",
                "What is SLI/SLO/SLA? How do error budgets help engineering teams make release decisions?",
                Set.of("devops"), false, Set.of("slo", "budget")));

        q.add(new InterviewQuestion("Q489",
                "What is distributed tracing (OpenTelemetry/Jaeger)? What does a trace ID let you do across services?",
                Set.of("devops", "microservices"), true, Set.of("trace", "correlation")));

        q.add(new InterviewQuestion("Q490",
                "What is a secrets manager (Vault, AWS Secrets Manager)? Why is it better than environment variables for secrets?",
                Set.of("devops", "security"), true, Set.of("secret", "rotate")));

        // =========================
        // Real Interview Scenario (Senior-style applied)
        // =========================
        q.add(new InterviewQuestion("Q491",
                "Your service is hitting the database on every request to fetch the same config table. The table changes once a day. How do you fix this with minimal code?",
                Set.of("spring boot", "redis", "sql"), true, Set.of("cache", "ttl")));

        q.add(new InterviewQuestion("Q492",
                "A background job runs every night and sometimes overlaps with itself on a second server node. How do you ensure it only runs once across all nodes?",
                Set.of("spring boot", "redis", "sql"), true, Set.of("lock", "distributed")));

        q.add(new InterviewQuestion("Q493",
                "Your team is about to deploy a database migration that drops a column still used by the old application version. What is the safe multi-step approach?",
                Set.of("sql", "devops", "microservices"), true, Set.of("backward", "migration")));

        q.add(new InterviewQuestion("Q494",
                "A new developer on your team adds a @Cacheable annotation to a method that has side effects. What do you flag in code review?",
                Set.of("spring boot", "design patterns"), true, Set.of("side", "cache")));

        q.add(new InterviewQuestion("Q495",
                "Your microservice calls 3 external APIs in sequence. Each call takes ~200ms. How would you reduce the total latency?",
                Set.of("java", "concurrency", "microservices"), true, Set.of("parallel", "completablefuture")));

        q.add(new InterviewQuestion("Q496",
                "You need to export 5 million rows to CSV from PostgreSQL without running out of memory. How do you stream the data?",
                Set.of("sql", "postgresql", "java"), false, Set.of("cursor", "stream")));

        q.add(new InterviewQuestion("Q497",
                "A user resets their password but their old sessions remain active. How do you invalidate all existing tokens on password change?",
                Set.of("security", "api"), true, Set.of("token", "version")));

        q.add(new InterviewQuestion("Q498",
                "Your REST API returns sensitive data that changes per user. Why must you set Cache-Control: no-store and what happens if you don't?",
                Set.of("api", "security"), true, Set.of("cache", "sensitive")));

        q.add(new InterviewQuestion("Q499",
                "You are asked to add soft-delete to a table that already has a UNIQUE constraint on email. What problem appears and how do you solve it?",
                Set.of("sql", "design patterns"), true, Set.of("deleted", "unique")));

        q.add(new InterviewQuestion("Q500",
                "Explain the difference between optimistic and pessimistic locking in the context of a bank transfer: which do you choose and why?",
                Set.of("sql", "concurrency", "hibernate"), true, Set.of("optimistic", "version")));

        return q;
    }
}
