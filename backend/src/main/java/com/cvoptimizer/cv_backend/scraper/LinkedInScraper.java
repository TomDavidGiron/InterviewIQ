package com.cvoptimizer.cv_backend.scraper;

import com.cvoptimizer.cv_backend.model.ScraperResult;
import java.util.Collections;

public class LinkedInScraper {
    public static ScraperResult scrape(String url) {
        return new ScraperResult(
                "LinkedIn - Not Yet Implemented",
                "Unknown",
                "Unknown",
                "This is a placeholder response for LinkedIn scraping.",
                Collections.emptyList()
        );
    }
}

