package com.cvoptimizer.cv_backend.scraper;

import com.cvoptimizer.cv_backend.model.ScraperResult;
import com.microsoft.playwright.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiJobListingScraper {

    public static List<ScraperResult> scrape(String url) {
        List<ScraperResult> results = new ArrayList<>();

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            SsrfGuard.guardNavigation(page);
            page.navigate(url);
            page.waitForTimeout(5000); // Wait longer for JS to load

            Locator cards = page.locator("a:has(h3), a:has(.job-title), div[class*='job'], li[class*='job']");
            int count = cards.count();
            System.out.println("🧩 Universal scraper found " + count + " job cards");

            for (int i = 0; i < count; i++) {
                try {
                    Locator card = cards.nth(i);
                    String title = tryGetText(card.locator("h3, .job-title, h2"));
                    String location = tryGetText(card.locator("p, span.location, .job-location"));
                    String href = card.getAttribute("href");

                    if (href == null || href.contains("#")) continue;
                    String fullLink = href.startsWith("http") ? href : resolveRelativeUrl(url, href);

                    Page jobPage = context.newPage();
                    SsrfGuard.guardNavigation(jobPage);
                    jobPage.navigate(fullLink);
                    jobPage.waitForTimeout(3000);

                    String description = tryGetText(jobPage.locator("main, .description, .job-detail, body"));
                    List<String> requirements = extractRequirements(description);

                    results.add(new ScraperResult(
                            title != null ? title : "Unknown Title",
                            inferCompanyNameFromUrl(url),
                            location != null ? location : "Unknown Location",
                            description,
                            requirements
                    ));

                    jobPage.close();
                } catch (Exception e) {
                    System.out.println("⚠️ Failed to extract a job card: " + e.getMessage());
                }
            }

            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    private static String tryGetText(Locator locator) {
        try {
            return locator.textContent().trim();
        } catch (Exception e) {
            return null;
        }
    }

    private static String resolveRelativeUrl(String base, String relative) {
        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        return relative.startsWith("/") ? base + relative : base + "/" + relative;
    }

    private static String inferCompanyNameFromUrl(String url) {
        String domain = url.replace("https://", "").replace("http://", "").split("/")[0];
        String[] parts = domain.split("\\.");
        if (parts.length >= 2) {
            return capitalize(parts[parts.length - 2]);
        }
        return "Unknown Company";
    }

    private static String capitalize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private static List<String> extractRequirements(String fullText) {
        List<String> extracted = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?i)(requirements|qualifications|skills|you’ll need)[\\s:\\-]*\\n?(.*)", Pattern.DOTALL);
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
