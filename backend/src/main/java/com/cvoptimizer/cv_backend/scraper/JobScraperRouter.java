package com.cvoptimizer.cv_backend.scraper;

import com.cvoptimizer.cv_backend.model.ScraperResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobScraperRouter {

    public ScraperResult scrape(String url) {
        if (url.matches(".*/careers/?$")) {
            List<ScraperResult> jobs = MultiJobListingScraper.scrape(url);
            if (!jobs.isEmpty()) {
                System.out.println("[Router] Detected listing site — returning first job from MultiJobListingScraper");
                return jobs.get(0);
            } else {
                return new ScraperResult("Multi-scraper failed", "N/A", "N/A", "No jobs found", List.of());
            }
        }

        // Step 1: Universal schema.org/JobPosting structured-data extraction.
        // Most ATS platforms (Greenhouse, Lever, Workday, iCIMS, SmartRecruiters,
        // LinkedIn, Indeed, most corporate careers pages) embed this for SEO, so it
        // covers far more sites than any hand-tuned selector and skips launching a
        // full browser for the common case.
        ScraperResult jsonLdResult = JsonLdJobPostingScraper.scrape(url);
        if (!isInvalid(jsonLdResult)) {
            System.out.println("[Router] Using JSON-LD JobPosting result: " + jsonLdResult.getTitle());
            return jsonLdResult;
        }

        // Step 2: Site-specific scrapers
        ScraperResult result;
        if (url.contains("smartrecruiters.com")) {
            result = JsoupSmartRecruitersScraper.scrape(url);
        } else if (url.contains("microsoft.com")) {
            result = MicrosoftScraper.scrape(url);
        } else {
            result = PlaywrightScraper.scrape(url);
        }

        // Step 3: Fallbacks
        if (isInvalid(result)) {
            result = SeleniumScraper.scrape(url);
        }

        if (isInvalid(result)) {
            result = PlaywrightScraper.scrape(url);
        }

        if (isInvalid(result)) {
            result = CustomPortalScraper.scrape(url);
        }

        if (isInvalid(result)) {
            List<ScraperResult> jobs = MultiJobListingScraper.scrape(url);
            if (!jobs.isEmpty()) {
                System.out.println("[Router] Final fallback to MultiJobListingScraper — returning first");
                return jobs.get(0);
            } else {
                return new ScraperResult("Multi-scraper failed", "N/A", "N/A", "No jobs found", List.of());
            }
        }

        System.out.println("[Router] Using result: " + result.getTitle());
        return result;
    }

    private boolean isInvalid(ScraperResult result) {
        if (result == null || result.getDescription() == null) return true;

        String desc = result.getDescription().toLowerCase().trim();
        String title = result.getTitle() != null ? result.getTitle().toLowerCase().trim() : "";

        return desc.isBlank()
                || desc.contains("error")
                || desc.contains("no description")
                || desc.contains("placeholder")
                || desc.contains("listing detected")
                || title.contains("listing detected")
                || title.contains("listing page fallback");
    }
}