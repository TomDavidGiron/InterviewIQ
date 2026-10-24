package com.cvoptimizer.cv_backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitInterceptorTest {

    private final RateLimitInterceptor interceptor = new RateLimitInterceptor();

    @Test
    void directConnection_ignoresForwardedHeader() {
        MockHttpServletRequest request = request("203.0.113.50", "1.2.3.4");
        assertThat(interceptor.resolveIp(request)).isEqualTo("203.0.113.50");
    }

    @Test
    void behindProxy_spoofedLeftmostEntryIsIgnored() {
        // client sent "1.2.3.4"; the proxy appended the real client address
        MockHttpServletRequest request = request("10.0.0.5", "1.2.3.4, 198.51.100.7");
        assertThat(interceptor.resolveIp(request)).isEqualTo("198.51.100.7");
    }

    @Test
    void behindIpv6UniqueLocalProxy_trustsForwardedHeader() {
        MockHttpServletRequest request = request("fd12:3456::1", "198.51.100.7");
        assertThat(interceptor.resolveIp(request)).isEqualTo("198.51.100.7");
    }

    @Test
    void internalHopsAppendedAfterClient_areSkipped() {
        MockHttpServletRequest request = request("100.64.0.2", "198.51.100.7, 100.64.0.9");
        assertThat(interceptor.resolveIp(request)).isEqualTo("198.51.100.7");
    }

    @Test
    void railwayStyleRequest_usesRealIpNotTheEdgeServer() {
        // Shape observed in production: internal proxy remoteAddr, edge IP appended last, X-Real-IP = client
        MockHttpServletRequest request = request("100.64.0.4", "198.51.100.7, 152.233.13.166");
        request.addHeader("X-Real-IP", "198.51.100.7");
        assertThat(interceptor.resolveIp(request)).isEqualTo("198.51.100.7");
    }

    @Test
    void directConnection_ignoresRealIpHeader() {
        MockHttpServletRequest request = request("203.0.113.50", "1.2.3.4");
        request.addHeader("X-Real-IP", "1.2.3.4");
        assertThat(interceptor.resolveIp(request)).isEqualTo("203.0.113.50");
    }

    @Test
    void sameClientThroughDifferentRailwayEdges_sharesOneBucket() throws Exception {
        String[] edges = {"152.233.13.166", "152.233.13.165", "152.233.12.241", "152.233.12.245"};
        for (int i = 0; i < 20; i++) {
            MockHttpServletRequest request = request("100.64.0." + (i + 2), "198.51.100.7, " + edges[i % edges.length]);
            request.addHeader("X-Real-IP", "198.51.100.7");
            assertThat(interceptor.preHandle(request, new MockHttpServletResponse(), null)).isTrue();
        }
        MockHttpServletRequest blocked = request("100.64.0.99", "198.51.100.7, 152.233.13.166");
        blocked.addHeader("X-Real-IP", "198.51.100.7");
        assertThat(interceptor.preHandle(blocked, new MockHttpServletResponse(), null)).isFalse();
    }

    @Test
    void sameClientThroughDifferentProxyConnections_sharesOneBucket() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        for (int i = 0; i < 20; i++) {
            assertThat(interceptor.preHandle(request("fd12::" + (i + 1), "198.51.100.7"), response, null)).isTrue();
        }
        assertThat(interceptor.preHandle(request("fd12::99", "198.51.100.7"), new MockHttpServletResponse(), null)).isFalse();
    }

    private MockHttpServletRequest request(String remoteAddr, String forwardedFor) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(remoteAddr);
        request.addHeader("X-Forwarded-For", forwardedFor);
        return request;
    }
}
