package com.cvoptimizer.cv_backend.scraper;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public final class SsrfGuard {

    private SsrfGuard() {}

    /**
     * Throws IllegalArgumentException if the URL targets a private, loopback,
     * or link-local address — preventing the server from being used as a proxy
     * to reach internal infrastructure.
     */
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
