package com.cvoptimizer.cv_backend.scraper;

import com.cvoptimizer.cv_backend.model.ScraperResult;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsoupSmartRecruitersScraper {

    private static final int MAX_REDIRECTS = 5;

    public static ScraperResult scrape(String url) {
        try {
            Document doc = fetchFollowingSafeRedirects(url);

            // Extract raw body text
            String bodyText = doc.body().text();

            // Title: from <title>, clean SmartRecruiters suffix
            String rawTitle = doc.title();
            String title = rawTitle.replace("| SmartRecruiters", "").replace("Omio", "").trim();

            // Company: hardcode for SmartRecruiters URLs or extract from title
            String company = "Omio";

            // Location: after the job title in bodyText
            String location = extractLocationFromBody(bodyText, title);

            // Description: text between "Job Description" and "Must-haves"
            String description = extractBetween(bodyText, "Job Description", "Must-haves");

            // Requirements: lines after "Must-haves"
            List<String> requirements = extractListAfter("Must-haves", bodyText);

            return new ScraperResult(title, company, location, description, requirements);

        } catch (Exception e) {
            e.printStackTrace();
            return new ScraperResult(
                    "SmartRecruiters Scraper Failed",
                    "N/A",
                    "N/A",
                    "Error: " + e.getMessage(),
                    List.of()
            );
        }
    }

    
    private static Document fetchFollowingSafeRedirects(String url) throws Exception {
        String currentUrl = url;
        for (int i = 0; i <= MAX_REDIRECTS; i++) {
            SsrfGuard.assertSafe(currentUrl);
            Connection.Response response = Jsoup.connect(currentUrl)
                    .userAgent("Mozilla")
                    .followRedirects(false)
                    .execute();

            int status = response.statusCode();
            String location = response.header("Location");
            if (status >= 300 && status < 400 && location != null && !location.isBlank()) {
                currentUrl = new URL(new URL(currentUrl), location).toString();
                continue;
            }
            return response.parse();
        }
        throw new IllegalStateException("Too many redirects while fetching " + url);
    }

    private static String extractLocationFromBody(String body, String jobTitle) {
        // Often location comes right after the title in text
        Pattern pattern = Pattern.compile(Pattern.quote(jobTitle) + "\\s+(.*?)(\\s+Full-time|\\s+Company Description)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Unknown Location";
    }

    private static String extractBetween(String text, String startMarker, String endMarker) {
        Pattern pattern = Pattern.compile("(?i)" + Pattern.quote(startMarker) + "\\s*(.*?)\\s*" + Pattern.quote(endMarker), Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "No description found.";
    }

    private static List<String> extractListAfter(String marker, String text) {
        List<String> lines = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?i)" + Pattern.quote(marker) + "[:\\s]*\\s*(.*?)(Technology Stack|Additional Information|Learn more|Hiring|Cookies|Refer a friend)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String block = matcher.group(1);
            for (String line : block.split("\n")) {
                line = line.trim();
                if (!line.isBlank() && line.length() > 5) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }
}
