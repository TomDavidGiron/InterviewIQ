package com.cvoptimizer.cv_backend.scraper;

import com.microsoft.playwright.Page;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public final class SsrfGuard {

    private static final int MAX_REDIRECTS = 5;

    private SsrfGuard() {}

    /**
     * Fetches a URL via Jsoup, re-validating {@link #assertSafe} on every hop so a
     * redirect can't be used to smuggle a request to an internal address.
     */
    public static Document fetchFollowingSafeRedirects(String url) throws Exception {
        String currentUrl = url;
        for (int i = 0; i <= MAX_REDIRECTS; i++) {
            assertSafe(currentUrl);
            Connection.Response response = Jsoup.connect(currentUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                            + "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                    .timeout(15000)
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

   
    public static void guardNavigation(Page page) {
        page.route("**/*", route -> {
            if (route.request().isNavigationRequest()) {
                try {
                    assertSafe(route.request().url());
                } catch (IllegalArgumentException e) {
                    route.abort();
                    return;
                }
            }
            route.resume();
        });
    }

    
    public static void assertSafe(String rawUrl) {
        URL url;
        try {
            url = new URL(rawUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL.");
        }

        String scheme = url.getProtocol();
        if (!scheme.equals("http") && !scheme.equals("https")) {
            throw new IllegalArgumentException("Only http/https URLs are allowed.");
        }

        String host = url.getHost();
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("URL has no host.");
        }

        InetAddress address;
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Cannot resolve host: " + host);
        }

        if (address.isLoopbackAddress()
                || address.isSiteLocalAddress()
                || address.isLinkLocalAddress()
                || address.isMulticastAddress()
                || address.isAnyLocalAddress()) {
            throw new IllegalArgumentException("Requests to internal or private addresses are not allowed.");
        }
    }
}
