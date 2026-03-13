package com.cvoptimizer.cv_backend.scraper;

import com.cvoptimizer.cv_backend.model.ScraperResult;
import java.util.Collections;

public class GenericScraper {
    public static ScraperResult scrape(String url) {
        return new ScraperResult(
                "Generic - Not Yet Implemented",
                "Unknown",
                "Unknown",
                "This is a placeholder response for a generic scraper.",
                Collections.emptyList()
        );
    }
}

