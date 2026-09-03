package com.legacy.shop.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Legacy In-Memory Rate Limiter Filter.
 * Demonstrates IP-based sliding window rate limiting (Anti-Pattern: in-memory state inside Servlet filter).
 */
public class RateLimitFilter implements Filter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private final Map<String, RequestCounter> clientCounters = new ConcurrentHashMap<String, RequestCounter>();

    private static class RequestCounter {
        long windowStart;
        AtomicInteger count;

        RequestCounter(long windowStart) {
            this.windowStart = windowStart;
            this.count = new AtomicInteger(1);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String ip = req.getRemoteAddr();
        long now = System.currentTimeMillis();

        RequestCounter counter = clientCounters.compute(ip, (k, v) -> {
            if (v == null || (now - v.windowStart) > 60000) {
                return new RequestCounter(now);
            }
            v.count.incrementAndGet();
            return v;
        });

        if (counter.count.get() > MAX_REQUESTS_PER_MINUTE) {
            res.setStatus(429);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"error\":\"TOO_MANY_REQUESTS\",\"message\":\"Rate limit exceeded. Try again in 1 minute.\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        clientCounters.clear();
    }
}
