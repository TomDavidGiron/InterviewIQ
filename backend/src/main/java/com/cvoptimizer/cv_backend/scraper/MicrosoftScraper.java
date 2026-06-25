package com.cvoptimizer.cv_backend.scraper;

import com.cvoptimizer.cv_backend.model.ScraperResult;
import com.microsoft.playwright.*;

import java.util.ArrayList;
import java.util.List;

public class MicrosoftScraper {

    private static final String[] TITLE_SELECTORS = {
            "[data-ph-at-id='job-title']",
            "h1[class*='title']",
            "h1[class*='heading']",
            "h1"
    };

    private static final String[] LOCATION_SELECTORS = {
            "[data-ph-at-id='job-location']",
            "[class*='location']",
            "[class*='jobLocation']"
    };

    private static final String[] DESCRIPTION_SELECTORS = {
            "#jdPostingDescription",
            ".job-description",
            "[class*='jobDescription']",
            "[data-ph-at-id='job-description']",
            "article"
    };

    public static ScraperResult scrape(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(List.of("--no-sandbox", "--disable-dev-shm-usage")));

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                            + "AppleWebKit/537.36 (KHTML, like Gecko) "
                            + "Chrome/124.0.0.0 Safari/537.36"));

            Page page = context.newPage();
            page.navigate(url);

            // Wait for h1 to appear; fall back to a timed wait if the selector never loads
            try {
                page.waitForSelector("h1", new Page.WaitForSelectorOptions().setTimeout(12000));
            } catch (Exception ignored) {
                page.waitForTimeout(8000);
            }

            String title = tryGetText(page, TITLE_SELECTORS);
            if (title == null || title.isBlank()) title = "Unknown Title";

            String location = tryGetText(page, LOCATION_SELECTORS);
            if (location == null || location.isBlank()) location = "Unknown Location";

            String description = tryGetText(page, DESCRIPTION_SELECTORS);
            if (description == null || description.isBlank()) {
                description = extractLongestParagraphBlock(page);
            }
            if (description == null || description.isBlank()) description = "No description found.";

            List<String> requirements = extractRequirements(page);
            if (requirements.isEmpty()) {
                requirements = extractFallbackListItems(page);
            }

            context.close();
            return new ScraperResult(title, "Microsoft", location, description, requirements);

        } catch (Exception e) {
            e.printStackTrace();
            return new ScraperResult("Microsoft Scraper Failed", "Microsoft", "Unknown",
                    "Error: " + e.getMessage(), List.of());
        }
    }

    private static List<String> extractRequirements(Page page) {
        List<String> requirements = new ArrayList<>();
        List<ElementHandle> containers = page.querySelectorAll(
                "section, div[class*='section'], div[class*='block'], div[class*='content']");

        for (ElementHandle container : containers) {
            try {
                String heading = findHeading(container);
                if (heading.contains("qualifications") || heading.contains("requirements")
                        || heading.contains("responsibilities")) {
                    for (ElementHandle li : container.querySelectorAll("li")) {
                        String text = li.innerText().trim();
                        if (text.length() > 20 && text.length() < 500) {
                            requirements.add(text);
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        return requirements;
    }

    private static List<String> extractFallbackListItems(Page page) {
        List<String> items = new ArrayList<>();
        List<ElementHandle> all = page.querySelectorAll("main li, article li, #jdPostingDescription li");
        for (ElementHandle li : all) {
            try {
                String text = li.innerText().trim();
                if (text.length() > 20 && text.length() < 500) {
                    items.add(text);
                }
                if (items.size() >= 30) break;
            } catch (Exception ignored) {}
        }
        return items;
    }

    private static String extractLongestParagraphBlock(Page page) {
        String longest = "";
        List<ElementHandle> blocks = page.querySelectorAll("p, div[class*='desc'], div[class*='content']");
        for (ElementHandle b : blocks) {
            try {
                String text = b.innerText().trim();
                if (text.length() > 150 && text.length() > longest.length()) {
                    longest = text;
                }
            } catch (Exception ignored) {}
        }
        return longest.isEmpty() ? null : longest;
    }

    private static String findHeading(ElementHandle container) {
        for (String tag : new String[]{"h2", "h3", "h4", "h5"}) {
            try {
                ElementHandle h = container.querySelector(tag);
                if (h != null) return h.innerText().toLowerCase().trim();
            } catch (Exception ignored) {}
        }
        return "";
    }

    private static String tryGetText(Page page, String[] selectors) {
        for (String selector : selectors) {
            try {
                List<ElementHandle> elements = page.querySelectorAll(selector);
                for (ElementHandle el : elements) {
                    String text = el.innerText().trim();
                    if (!text.isBlank()) return text;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }
}
