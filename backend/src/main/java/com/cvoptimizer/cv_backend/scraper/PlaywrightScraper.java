package com.cvoptimizer.cv_backend.scraper;

import com.cvoptimizer.cv_backend.model.ScraperResult;
import com.microsoft.playwright.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlaywrightScraper {

    public static ScraperResult scrape(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            );
            Page page = browser.newPage();
            SsrfGuard.guardNavigation(page);
            page.navigate(url);
            page.waitForTimeout(3000); // let dynamic content load

            // 1. Page Title
            String title = page.title();

            // 2. Company (try og:site_name first)
            String company = "Unknown Company";
            Locator metaCompany = page.locator("meta[property='og:site_name']");
            if (metaCompany.count() > 0) {
                String content = metaCompany.first().getAttribute("content");
                if (content != null && !content.isBlank()) {
                    company = content.trim();
                }
            }

            // 3. Location (try known tags or fall back to page scrape)
            String location = "Unknown Location";
            Locator locationLocator = page.locator("h5[data-testid='location']");
            if (locationLocator.count() > 0) {
                location = locationLocator.first().textContent().trim();
            }

            // 4. Description (SmartRecruiters or fallback to body text)
            String description = "No description found.";
            Locator descLocator = page.locator("div[data-testid='job-description']");
            if (descLocator.count() > 0) {
                description = descLocator.first().textContent().trim();
            } else {
                // Fallback: Use first large paragraph from body
                Locator body = page.locator("body");
                if (body.count() > 0) {
                    String bodyText = body.first().innerText().trim();
                    description = bodyText.length() > 10000
                            ? bodyText.substring(0, 10000) + "..." // truncate large blobs
                            : bodyText;
                }
            }

            // 5. Requirements list (SmartRecruiters structure or fallback)
            List<String> requirements = new ArrayList<>();
            Locator reqLocator = page.locator("ul[data-testid='qualifications'] li");
            if (reqLocator.count() > 0) {
                requirements = reqLocator.allTextContents()
                        .stream()
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.toList());
            } else {
                // Fallback: try any bullet list in body
                Locator fallbackReqs = page.locator("li");
                if (fallbackReqs.count() > 0) {
                    requirements = fallbackReqs.allTextContents()
                            .stream()
                            .map(String::trim)
                            .filter(text -> text.length() > 20 && text.length() < 500)
                            .limit(30) // prevent dumping unrelated long text
                            .collect(Collectors.toList());
                }
            }

            browser.close();

            return new ScraperResult(title, company, location, description, requirements);

        } catch (Exception e) {
            e.printStackTrace();
            return new ScraperResult(
                    "Playwright Scraper Failed",
                    "N/A",
                    "N/A",
                    "Error: " + e.getMessage(),
                    List.of()
            );
        }
    }
}
