package com.cvoptimizer.cv_backend.scraper;

import com.cvoptimizer.cv_backend.model.ScraperResult;
import com.microsoft.playwright.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomPortalScraper {

    public static ScraperResult scrape(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            );
            Page page = browser.newPage();
            page.navigate(url);
            page.waitForTimeout(3000); // wait for content to load

            // Check for listing layout
            Locator jobLinks = page.locator("a[class*='StyledJobCard']");
            if (jobLinks.count() > 3) {
                return new ScraperResult(
                        "listing detected",
                        "Unknown",
                        "Unknown",
                        "listing detected",
                        List.of()
                );
            }

            String title = page.title();
            String description = findJobSection(page);
            List<String> requirements = extractRequirements(description);

            boolean looksLikeListing = page.locator("a[href*='job'], a[href*='careers'], div[class*='job'], li[class*='job']").count() > 10;

            if (description.split("\n").length < 15 || requirements.isEmpty() || looksLikeListing) {
                return new ScraperResult(
                        "listing detected",
                        "Unknown",
                        "Unknown",
                        "listing detected",
                        List.of()
                );
            }

            return new ScraperResult(
                    title,
                    "Unknown Company",
                    "Unknown Location",
                    description,
                    requirements
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ScraperResult(
                    "listing detected",
                    "Unknown",
                    "Unknown",
                    "listing detected",
                    List.of()
            );
        }
    }

    private static String findJobSection(Page page) {
        List<String> keywords = List.of("job-description", "description", "responsibilities", "requirements", "details", "content");

        for (String keyword : keywords) {
            Locator section = page.locator("div[class*='" + keyword + "'], section[class*='" + keyword + "'], article[class*='" + keyword + "']");
            if (section.count() > 0) {
                String text = section.first().innerText();
                if (!text.isBlank()) {
                    return text.trim();
                }
            }
        }

        return page.locator("body").innerText();
    }

    private static List<String> extractRequirements(String fullText) {
        List<String> extracted = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?i)(requirements|qualifications|what you’ll need|skills required)[\\s:\\-]*\\n?(.*)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fullText);

        while (matcher.find()) {
            String block = matcher.group(2);
            String[] lines = block.split("\\n");
            for (String line : lines) {
                line = line.trim();
                if (!line.isBlank() && line.length() > 5) {
                    extracted.add(line);
                }
            }
            break;
        }

        return extracted;
    }
}
