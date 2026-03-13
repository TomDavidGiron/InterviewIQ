package com.cvoptimizer.cv_backend.scraper;

import com.cvoptimizer.cv_backend.model.ScraperResult;
import com.microsoft.playwright.*;

import java.util.ArrayList;
import java.util.List;

public class MicrosoftScraper {

    public static ScraperResult scrape(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();

            page.navigate(url);
            page.waitForTimeout(6000); // wait for JS content

            String title = tryGetText(page, new String[]{"h1", "h2"});
            if (title == null || title.isBlank()) title = "Unknown Title";

            String company = "Microsoft";

            String location = tryGetText(page, new String[]{"[data-ph-at-id=job-location]"});
            if (location == null || location.isBlank()) location = "Unknown Location";

            // Description: longest paragraph inside job-description
            List<ElementHandle> paragraphs = page.querySelectorAll(".job-description p");
            String description = "No description found.";
            int maxLength = 0;
            for (ElementHandle p : paragraphs) {
                String text = p.innerText().trim();
                if (text.length() > 100 && text.length() > maxLength) {
                    description = text;
                    maxLength = text.length();
                }
            }

            // Requirements from "Qualifications" section
            List<ElementHandle> sections = page.querySelectorAll("section");
            List<String> requirements = new ArrayList<>();

            for (ElementHandle sec : sections) {
                try {
                    String heading = sec.querySelector("h2") != null ? sec.querySelector("h2").innerText().toLowerCase() : "";
                    if (heading.contains("qualifications")) {
                        List<ElementHandle> listItems = sec.querySelectorAll("li");
                        for (ElementHandle li : listItems) {
                            String text = li.innerText().trim();
                            if (text.length() > 20 && text.length() < 400) {
                                requirements.add(text);
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }

            return new ScraperResult(title, company, location, description, requirements);

        } catch (Exception e) {
            e.printStackTrace();
            return new ScraperResult("Microsoft Scraper Failed", "Microsoft", "Unknown", "Error: " + e.getMessage(), List.of());
        }
    }

    private static String tryGetText(Page page, String[] selectors) {
        for (String selector : selectors) {
            List<ElementHandle> elements = page.querySelectorAll(selector);
            for (ElementHandle el : elements) {
                String text = el.innerText().trim();
                if (!text.isBlank()) return text;
            }
        }
        return null;
    }
}
