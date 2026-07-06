package com.cvoptimizer.cv_backend.scraper;

import com.cvoptimizer.cv_backend.model.ScraperResult;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class SeleniumScraper {

    public static ScraperResult scrape(String url) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);
        try {
            driver.get(url);

            // Best-effort redirect check: WebDriver's basic API can't intercept navigation
            // before it fires (unlike the Jsoup/Playwright scrapers), so this only stops us
            // from returning content scraped from an internal address after the fact.
            SsrfGuard.assertSafe(driver.getCurrentUrl());

            Thread.sleep(3000); // Let content load

            String title = tryGetFirstMatch(driver, List.of("h1", "h2", "header"));

            String company = tryGetFirstMatch(driver, List.of(
                    ".company", "[class*=company]", "[data-testid*=company]", "[id*=company]"
            ));
            if (company == null || company.isBlank()) {
                company = inferCompanyFromUrl(url);
            }

            String location = tryGetFirstMatch(driver, List.of(
                    ".location", "[class*=location]", "[data-testid*=location]", "[id*=location]"
            ));
            if (location == null || location.isBlank()) location = "Unknown Location";

            String description = extractDescriptionSection(driver);
            if (description == null || description.isBlank()) description = "No description found.";

            List<String> requirements = extractStrictRequirements(driver);

            return new ScraperResult(
                    title != null ? title : "Unknown Title",
                    company,
                    location,
                    description,
                    requirements
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new ScraperResult("Selenium Scraper Failed", "N/A", "N/A", "Error: " + e.getMessage(), List.of());
        } finally {
            driver.quit();
        }
    }

    private static List<String> extractStrictRequirements(WebDriver driver) {
        List<WebElement> items = driver.findElements(By.tagName("li"));

        return items.stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(SeleniumScraper::isStrictRequirement)
                .distinct()
                .limit(20)
                .collect(Collectors.toList());
    }

    private static boolean isStrictRequirement(String text) {
        String l = text.toLowerCase();

        List<String> mustHaveKeywords = List.of(
                "experience", "degree", "bachelor", "master", "phd",
                "skills", "familiarity", "proficient", "knowledge",
                "understanding", "expertise", "java", "kotlin", "python",
                "collaborating", "communication", "teamwork", "passionate", "ability to", "microservice"
        );

        List<String> forbidden = List.of(
                "team is", "product", "trip", "wishlist",
                "cookie", "faq", "privacy", "terms",
                "anthem", "kaiser", "apply now"
        );

        if (text.length() < 30) return false;
        if (forbidden.stream().anyMatch(l::contains)) return false;

        return text.startsWith("[Bonus]") || mustHaveKeywords.stream().anyMatch(l::contains);
    }

    private static String extractDescriptionSection(WebDriver driver) {
        List<String> verbs = List.of("design", "build", "lead", "improve", "develop", "collaborate", "scale", "deliver", "execute");

        return driver.findElements(By.tagName("p")).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(t -> t.length() > 50 && verbs.stream().anyMatch(t.toLowerCase()::contains) && !isNoise(t))
                .findFirst().orElse(null);
    }

    private static boolean isNoise(String text) {
        String l = text.toLowerCase();
        return l.contains("cookie") || l.contains("faq") || l.contains("privacy") || l.contains("terms");
    }

    private static String tryGetFirstMatch(WebDriver driver, List<String> selectors) {
        for (String css : selectors) {
            try {
                List<WebElement> elements = driver.findElements(By.cssSelector(css));
                for (WebElement el : elements) {
                    String text = el.getText().trim();
                    if (!text.isBlank()) return text;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static String inferCompanyFromUrl(String url) {
        try {
            String host = new URL(url).getHost();
            String[] parts = host.split("\\.");
            return parts.length >= 2 ? capitalize(parts[parts.length - 2]) : "Unknown Company";
        } catch (Exception e) {
            return "Unknown Company";
        }
    }
    private static String capitalize(String word) {
        if (word == null || word.isEmpty()) return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}
