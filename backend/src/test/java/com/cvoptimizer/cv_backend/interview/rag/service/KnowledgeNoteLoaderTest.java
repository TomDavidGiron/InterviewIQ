package com.cvoptimizer.cv_backend.interview.rag.service;

import com.cvoptimizer.cv_backend.interview.rag.service.KnowledgeNoteLoader.KnowledgeNote;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KnowledgeNoteLoaderTest {

    private final KnowledgeNoteLoader loader = new KnowledgeNoteLoader();

    @Test
    void bundledNotes_loadWithTopicsTagsAndReasonableSize() {
        List<KnowledgeNote> notes = loader.loadAll();

        Set<String> topics = notes.stream().map(KnowledgeNote::topic).collect(Collectors.toSet());
        assertThat(topics).containsExactlyInAnyOrder("rest-api", "java", "spring", "sql", "concurrency");
        assertThat(notes).hasSizeGreaterThan(50);

        for (KnowledgeNote note : notes) {
            assertThat(note.tags()).as(note.chunkKey()).isNotEmpty();
            // Big enough to be useful context, small enough to stay one focused chunk in the prompt
            assertThat(note.content().length()).as(note.chunkKey()).isBetween(300, 2_500);
        }
    }

    @Test
    void parse_splitsSectionsAndPrefixesTitle() {
        String markdown = """
                ---
                topic: sql
                tags: sql, PostgreSQL
                ---
                # SQL Basics

                ## Indexes
                An index speeds up lookups.

                ## Joins
                A join combines tables.
                """;

        List<KnowledgeNote> notes = loader.parse("sample", markdown);

        assertThat(notes).extracting(KnowledgeNote::chunkKey)
                .containsExactly("note:sample:indexes", "note:sample:joins");
        assertThat(notes.get(0).content()).isEqualTo("SQL Basics — Indexes\n\nAn index speeds up lookups.");
        assertThat(notes.get(0).tags()).containsExactly("sql", "postgresql");
    }

    @Test
    void contentHash_changesOnlyWhenSectionTextChanges() {
        String before = "---\ntopic: t\ntags: a\n---\n# T\n\n## S\nOriginal text.\n";
        String after = "---\ntopic: t\ntags: a\n---\n# T\n\n## S\nEdited text.\n";

        String original = loader.parse("f", before).get(0).contentHash();
        assertThat(loader.parse("f", before).get(0).contentHash()).isEqualTo(original);
        assertThat(loader.parse("f", after).get(0).contentHash()).isNotEqualTo(original);
    }

    @Test
    void missingFrontMatter_failsLoudly() {
        assertThatThrownBy(() -> loader.parse("f", "# No front matter\n\n## Section\nText"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("front matter");
    }
}
