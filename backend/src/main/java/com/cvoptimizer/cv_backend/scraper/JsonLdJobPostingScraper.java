package com.cvoptimizer.cv_backend.scraper;

import com.cvoptimizer.cv_backend.model.ScraperResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Extracts job details from schema.org/JobPosting JSON-LD, which most ATS platforms
 * (Greenhouse, Lever, Workday, iCIMS, SmartRecruiters, LinkedIn, Indeed, and most
 * corporate careers pages) embed for Google-for-Jobs SEO. Structured data beats
 * CSS-selector guessing, so this runs before any site-specific/generic scraper.
 */
public class JsonLdJobPostingScraper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ScraperResult scrape(String url) {
        try {
            Document doc = SsrfGuard.fetchFollowingSafeRedirects(url);
            ScraperResult result = extractFromDocument(doc);
            if (result != null) {
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fast path found nothing — the JSON-LD may be injected client-side (SPA).
        return scrapeWithPlaywright(url);
    }

    private static ScraperResult scrapeWithPlaywright(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            SsrfGuard.guardNavigation(page);
            page.navigate(url);
            page.waitForTimeout(3000);

            List<String> scripts = new ArrayList<>();
            for (ElementHandle el : page.querySelectorAll("script[type='application/ld+json']")) {
                try {
                    String text = el.textContent();
                    if (text != null && !text.isBlank()) {
                        scripts.add(text);
                    }
                } catch (Exception ignored) {}
            }

            browser.close();
            return extractFromScripts(scripts);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ScraperResult extractFromDocument(Document doc) {
        Elements scriptTags = doc.select("script[type=application/ld+json]");
        List<String> scripts = new ArrayList<>();
        for (Element el : scriptTags) {
            scripts.add(el.data());
        }
        return extractFromScripts(scripts);
    }

    private static ScraperResult extractFromScripts(List<String> scripts) {
        for (String raw : scripts) {
            if (raw == null || raw.isBlank()) continue;
            try {
                JsonNode root = MAPPER.readTree(raw);
                JsonNode jobPosting = findJobPosting(root);
                if (jobPosting != null) {
                    return toScraperResult(jobPosting);
                }
            } catch (Exception ignored) {
                // Not valid JSON, or not the shape we expect — try the next script block.
            }
        }
        return null;
    }

    private static JsonNode findJobPosting(JsonNode node) {
        if (node == null || node.isMissingNode()) return null;

        if (node.isObject()) {
            if (isJobPosting(node)) {
                return node;
            }
            if (node.has("@graph")) {
                JsonNode found = findJobPosting(node.get("@graph"));
                if (found != null) return found;
            }
            return null;
        }

        if (node.isArray()) {
            for (JsonNode child : node) {
                JsonNode found = findJobPosting(child);
                if (found != null) return found;
            }
        }

        return null;
    }

    private static boolean isJobPosting(JsonNode node) {
        JsonNode type = node.get("@type");
        if (type == null) return false;
        if (type.isTextual()) {
            return "JobPosting".equalsIgnoreCase(type.asText());
        }
        if (type.isArray()) {
            for (JsonNode t : type) {
                if (t.isTextual() && "JobPosting".equalsIgnoreCase(t.asText())) return true;
            }
        }
        return false;
    }

    private static ScraperResult toScraperResult(JsonNode job) {
        String title = textOf(job.get("title"));
        if (title == null || title.isBlank()) title = "Unknown Title";

        String company = extractOrganizationName(job.get("hiringOrganization"));
        if (company == null || company.isBlank()) company = "Unknown Company";

        String location = extractLocation(job.get("jobLocation"));
        if (location == null || location.isBlank()) location = "Unknown Location";

        String description = stripHtml(textOf(job.get("description")));
        if (description == null || description.isBlank()) description = "No description found.";

        List<String> requirements = extractRequirements(job);

        return new ScraperResult(title, company, location, description, requirements);
    }

    private static String extractOrganizationName(JsonNode org) {
        if (org == null || org.isMissingNode() || org.isNull()) return null;
        if (org.isTextual()) return org.asText();
        if (org.isArray() && org.size() > 0) return extractOrganizationName(org.get(0));
        if (org.isObject()) return textOf(org.get("name"));
        return null;
    }

    private static String extractLocation(JsonNode jobLocation) {
        if (jobLocation == null || jobLocation.isMissingNode() || jobLocation.isNull()) return null;

        if (jobLocation.isArray()) {
            List<String> parts = new ArrayList<>();
            for (JsonNode loc : jobLocation) {
                String part = extractLocation(loc);
                if (part != null && !part.isBlank()) parts.add(part);
            }
            return String.join(" / ", parts);
        }

        JsonNode address = jobLocation.get("address");
        if (address == null) return textOf(jobLocation.get("name"));

        List<String> parts = new ArrayList<>();
        addIfPresent(parts, address, "addressLocality");
        addIfPresent(parts, address, "addressRegion");
        addIfPresent(parts, address, "addressCountry");
        return String.join(", ", parts);
    }

    private static void addIfPresent(List<String> parts, JsonNode node, String field) {
        String value = textOf(node.get(field));
        if (value != null && !value.isBlank()) parts.add(value.trim());
    }

    private static List<String> extractRequirements(JsonNode job) {
        List<String> requirements = new ArrayList<>();
        for (String field : new String[]{"qualifications", "responsibilities", "skills",
                "experienceRequirements", "educationRequirements"}) {
            String raw = textOf(job.get(field));
            if (raw == null || raw.isBlank()) continue;

            String plain = stripHtml(raw);
            List<String> listItems = extractHtmlListItems(raw);
            if (!listItems.isEmpty()) {
                requirements.addAll(listItems);
            } else {
                for (String line : plain.split("\\r?\\n|(?<=[.;])\\s+(?=[A-Z])")) {
                    line = line.trim();
                    if (line.length() > 5) requirements.add(line);
                }
            }
        }
        return requirements;
    }

    private static List<String> extractHtmlListItems(String html) {
        List<String> items = new ArrayList<>();
        Elements lis = Jsoup.parse(html).select("li");
        for (Element li : lis) {
            String text = li.text().trim();
            if (text.length() > 5) items.add(text);
        }
        return items;
    }

    private static String stripHtml(String value) {
        if (value == null) return null;
        return Jsoup.parse(value).text().trim();
    }

    private static String textOf(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        return node.isTextual() ? node.asText() : node.toString();
    }
}
