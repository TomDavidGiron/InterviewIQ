package com.cvoptimizer.cv_backend.interview.rag.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Loads the curated concept notes in resources/knowledge/*.md and splits them into RAG chunks.
 *
 * Note format:
 * <pre>
 * ---
 * topic: sql
 * tags: sql, postgresql
 * ---
 * # SQL and Relational Databases
 *
 * ## Indexes
 * One self-contained explanation per "##" section — each section becomes one chunk.
 * </pre>
 */
@Component
public class KnowledgeNoteLoader {

    private static final String LOCATION = "classpath:knowledge/*.md";

    public record KnowledgeNote(String chunkKey, String topic, List<String> tags, String title,
                                String content, String contentHash) {}

    public List<KnowledgeNote> loadAll() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(LOCATION);
            Arrays.sort(resources, (a, b) -> String.valueOf(a.getFilename()).compareTo(String.valueOf(b.getFilename())));

            List<KnowledgeNote> notes = new ArrayList<>();
            Set<String> keys = new HashSet<>();
            for (Resource resource : resources) {
                String markdown = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                for (KnowledgeNote note : parse(baseName(resource.getFilename()), markdown)) {
                    if (!keys.add(note.chunkKey())) {
                        throw new IllegalStateException("Duplicate knowledge section: " + note.chunkKey());
                    }
                    notes.add(note);
                }
            }
            return notes;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load knowledge notes", e);
        }
    }

    List<KnowledgeNote> parse(String fileName, String markdown) {
        String[] lines = markdown.replace("\r\n", "\n").split("\n");
        Map<String, String> frontMatter = new HashMap<>();
        int i = 0;

        if (lines.length > 0 && lines[0].trim().equals("---")) {
            for (i = 1; i < lines.length && !lines[i].trim().equals("---"); i++) {
                int colon = lines[i].indexOf(':');
                if (colon > 0) {
                    frontMatter.put(lines[i].substring(0, colon).trim(), lines[i].substring(colon + 1).trim());
                }
            }
            i++;
        }

        String topic = frontMatter.get("topic");
        List<String> tags = Arrays.stream(frontMatter.getOrDefault("tags", "").split(","))
                .map(t -> t.trim().toLowerCase(Locale.ROOT))
                .filter(t -> !t.isEmpty())
                .toList();
        if (topic == null || topic.isBlank() || tags.isEmpty()) {
            throw new IllegalStateException("Knowledge note " + fileName + " needs 'topic' and 'tags' front matter");
        }

        String title = fileName;
        List<KnowledgeNote> notes = new ArrayList<>();
        String heading = null;
        StringBuilder body = new StringBuilder();

        for (; i < lines.length; i++) {
            String line = lines[i];
            if (line.startsWith("# ")) {
                title = line.substring(2).trim();
            } else if (line.startsWith("## ")) {
                addNote(notes, fileName, topic, tags, title, heading, body);
                heading = line.substring(3).trim();
                body.setLength(0);
            } else if (heading != null) {
                body.append(line).append('\n');
            }
        }
        addNote(notes, fileName, topic, tags, title, heading, body);
        return notes;
    }

    private void addNote(List<KnowledgeNote> notes, String fileName, String topic, List<String> tags,
                         String title, String heading, StringBuilder body) {
        if (heading == null || body.toString().isBlank()) {
            return;
        }
        String content = title + " — " + heading + "\n\n" + body.toString().trim();
        String chunkKey = "note:" + fileName + ":" + slug(heading);
        notes.add(new KnowledgeNote(chunkKey, topic, tags, title, content, sha256(content + "|" + String.join(",", tags))));
    }

    private String slug(String heading) {
        return heading.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }

    private String baseName(String fileName) {
        return fileName == null ? "unknown" : fileName.replaceFirst("\\.md$", "");
    }

    private String sha256(String text) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
