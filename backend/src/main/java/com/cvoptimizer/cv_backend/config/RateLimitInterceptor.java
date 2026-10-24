package com.cvoptimizer.cv_backend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitInterceptor implements HandlerInterceptor {

    // 20 requests per minute per IP
    private static final int CAPACITY = 20;
    private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);
    private static final int MAX_TRACKED_CLIENTS = 10_000;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = resolveIp(request);
        if (buckets.size() > MAX_TRACKED_CLIENTS) {
            buckets.clear(); // crude bound on memory; briefly resets everyone's limit
        }
        Bucket bucket = buckets.computeIfAbsent(ip, k -> newBucket());

        if (bucket.tryConsume(1)) {
            response.addHeader("X-RateLimit-Remaining", String.valueOf(bucket.getAvailableTokens()));
            return true;
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Too many requests. Please wait a moment before trying again.\"}");
        return false;
    }

    private Bucket newBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(CAPACITY)
                        .refillGreedy(CAPACITY, REFILL_PERIOD)
                        .build())
                .build();
    }

    String resolveIp(HttpServletRequest request) {
        // Only trust X-Forwarded-For when the immediate connection comes from an internal address,
        // meaning the request is arriving through a reverse proxy (Railway, nginx, Docker).
        String remoteAddr = request.getRemoteAddr();
        if (!isInternalAddress(remoteAddr)) {
            return remoteAddr;
        }

        // Railway's edge sets X-Real-IP to the real client and replaces any client-sent forwarding headers.
        // Its X-Forwarded-For ends with the edge's own public IP, which changes per request, so prefer X-Real-IP.
        String realIp = request.getHeader("X-Real-IP");
        if (parseIpLiteral(realIp == null ? null : realIp.trim()) != null) {
            return realIp.trim();
        }

        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded == null || forwarded.isBlank()) {
            return remoteAddr;
        }

        // Proxies append to the right, so read right-to-left and take the first non-internal hop.
        // The leftmost entries are whatever the client sent and can be spoofed.
        String[] hops = forwarded.split(",");
        for (int i = hops.length - 1; i >= 0; i--) {
            String hop = hops[i].trim();
            if (!hop.isEmpty() && !isInternalAddress(hop)) {
                return hop;
            }
        }
        return remoteAddr;
    }

    private boolean isInternalAddress(String addr) {
        InetAddress ia = parseIpLiteral(addr);
        if (ia == null) return false;
        if (ia.isLoopbackAddress() || ia.isSiteLocalAddress() || ia.isLinkLocalAddress()) return true;

        byte[] b = ia.getAddress();
        if (ia instanceof Inet6Address) {
            return (b[0] & 0xFE) == 0xFC; // fc00::/7 unique-local (e.g. Railway's private network)
        }
        return (b[0] & 0xFF) == 100 && (b[1] & 0xC0) == 64; // 100.64.0.0/10 carrier-grade NAT
    }

    private InetAddress parseIpLiteral(String addr) {
        // Only accept IP literals, so InetAddress never triggers a DNS lookup for a header value
        if (addr == null || addr.isBlank() || !addr.matches("[0-9A-Fa-f:.%]+")) return null;
        try {
            return InetAddress.getByName(addr);
        } catch (Exception e) {
            return null;
        }
    }
}
