package com.cvoptimizer.cv_backend.scraper;

import com.cvoptimizer.cv_backend.model.ScraperResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JobScraperRouterTest {

    private final JobScraperRouter router = new JobScraperRouter();

    @Test
    void realPostingMentioningErrors_isValid() {
        ScraperResult posting = new ScraperResult("Backend Engineer", "Acme", "Remote",
                "You will own error handling, monitoring and alerting for our payment APIs.", List.of());

        assertThat(router.isInvalid(posting)).isFalse();
    }

    @Test
    void scraperFailureResult_isInvalid() {
        ScraperResult failure = new ScraperResult("Playwright Scraper Failed", "N/A", "N/A",
                "Error: Timeout 30000ms exceeded", List.of());

        assertThat(router.isInvalid(failure)).isTrue();
    }

    @Test
    void listingPageAndEmptyResults_areInvalid() {
        assertThat(router.isInvalid(new ScraperResult("listing detected", "Unknown", "Unknown", "listing detected", List.of()))).isTrue();
        assertThat(router.isInvalid(new ScraperResult("Title", "Co", "Loc", "   ", List.of()))).isTrue();
        assertThat(router.isInvalid(null)).isTrue();
    }
}
