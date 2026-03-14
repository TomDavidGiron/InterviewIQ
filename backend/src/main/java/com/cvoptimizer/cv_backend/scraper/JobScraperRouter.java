package com.cvoptimizer.cv_backend.scraper;

import com.cvoptimizer.cv_backend.model.ScraperResult;

import java.util.List;

public class JobScraperRouter {

    public static ScraperResult scrape(String url) {
        if (url.matches(".*/careers/?$")) {
            List<ScraperResult> jobs = MultiJobListingScraper.scrape(url);
            if (!jobs.isEmpty()) {
                System.out.println("[Router] Detected listing site — returning first job from MultiJobListingScraper");
                return jobs.get(0);
            } else {
                return new ScraperResult("Multi-scraper failed", "N/A", "N/A", "No jobs found", List.of());
            }
        }

        // 🌐 Step 1: Site-specific scrapers
        ScraperResult result;
        if (url.contains("smartrecruiters.com")) {
            result = JsoupSmartRecruitersScraper.scrape(url);
        } else if (url.contains("linkedin.com")) {
            result = LinkedInScraper.scrape(url);
        }else if (url.contains("microsoft.com")) {
            result = MicrosoftScraper.scrape(url);
        }else {
            result = GenericScraper.scrape(url);
        }

        // 🤖 Step 2: Fallbacks
        if (isInvalid(result)) {
            result = SeleniumScraper.scrape(url);
        }

        if (isInvalid(result)) {
            result = PlaywrightScraper.scrape(url);
        }

        if (isInvalid(result)) {
            result = CustomPortalScraper.scrape(url);
        }

        // ✅ Final fallback — generic multi job scraper
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

    private static boolean isInvalid(ScraperResult result) {
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
