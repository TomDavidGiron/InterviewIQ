package com.cvoptimizer.cv_backend.interview.service;

import com.cvoptimizer.cv_backend.interview.persistence.entity.QuestionEntity;
import com.cvoptimizer.cv_backend.interview.persistence.repo.QuestionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QuestionBankServiceTest {

    private final QuestionRepository repository = mock(QuestionRepository.class);
    private final QuestionBankService service = new QuestionBankService(repository, new ObjectMapper());

    @Test
    void bankIsLoadedOnceAndReloadedAfterInvalidate() {
        when(repository.findByStatus("ACTIVE")).thenReturn(List.of(entity("Q1")));

        assertThat(service.getAllFromDatabase()).hasSize(1);
        assertThat(service.getAllFromDatabase()).hasSize(1);
        verify(repository, times(1)).findByStatus("ACTIVE");

        when(repository.findByStatus("ACTIVE")).thenReturn(List.of(entity("Q1"), entity("Q2")));
        service.invalidateCache();

        assertThat(service.getAllFromDatabase()).hasSize(2);
        verify(repository, times(2)).findByStatus("ACTIVE");
    }

    private QuestionEntity entity(String id) {
        QuestionEntity e = new QuestionEntity();
        e.setId(id);
        e.setText("Question " + id);
        e.setType("OPEN");
        e.setTags(new String[]{"java"});
        e.setRequiredKeywords(new String[]{"jvm"});
        e.setDifficulty("MEDIUM");
        return e;
    }
}
