package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.persistence.entity.QuestionEntity;
import com.cvoptimizer.cv_backend.interview.persistence.repo.QuestionRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * One-shot SQL migration generator. Run once to produce V10__seed_questions.sql,
 * then delete this class along with QuestionDbSeeder and getBackendJuniorBank().
 *
 * Usage:
 *   mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=export-sql"
 *
 * Output: V10__seed_questions.sql in the project root.
 * Move it to: src/main/resources/db/migration/
 */
@Component
@Profile("export-sql")
public class QuestionMigrationExporter implements ApplicationRunner {

    private final QuestionRepository questionRepository;

    public QuestionMigrationExporter(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<QuestionEntity> all = questionRepository.findAll(Sort.by("id"));

        StringBuilder sql = new StringBuilder();
        sql.append("-- V10__seed_questions.sql\n");
        sql.append("-- Full question bank seed. Safe to re-run (ON CONFLICT DO NOTHING).\n\n");
        sql.append("INSERT INTO question\n");
        sql.append("    (id, text, type, tags, required_keywords, critical,\n");
        sql.append("     options, correct_option_index, starter_code, source, status, created_at)\n");
        sql.append("VALUES\n");

        for (int i = 0; i < all.size(); i++) {
            QuestionEntity q = all.get(i);
            String sep = (i < all.size() - 1) ? "," : "";

            sql.append(String.format("(%s, %s, '%s', %s, %s, %b, %s, %s, %s, 'question_bank', 'ACTIVE', NOW())%s%n",
                    lit(q.getId()),
                    lit(q.getText()),
                    q.getType(),
                    lit(q.getTags()),
                    lit(q.getRequiredKeywords()),
                    q.isCritical(),
                    lit(q.getOptions()),
                    q.getCorrectOptionIndex() == null ? "NULL" : q.getCorrectOptionIndex(),
                    lit(q.getStarterCode()),
                    sep));
        }

        sql.append("ON CONFLICT (id) DO NOTHING;\n");

        Path out = Path.of("V10__seed_questions.sql");
        Files.writeString(out, sql.toString());
        System.out.printf("[QuestionMigrationExporter] Wrote %d questions to %s%n",
                all.size(), out.toAbsolutePath());
        System.exit(0);
    }

    private static String lit(String s) {
        if (s == null || s.isBlank()) return "NULL";
        return "'" + s.replace("'", "''") + "'";
    }
}
