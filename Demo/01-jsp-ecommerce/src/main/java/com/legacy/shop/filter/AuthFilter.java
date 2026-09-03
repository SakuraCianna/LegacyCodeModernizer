package com.legacy.shop.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        if (uri.endsWith("checkout.jsp") || uri.endsWith("/order")) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("CURRENT_USER") == null) {
                // For demo purposes, auto-seed a guest user session if missing
                req.getSession(true).setAttribute("CURRENT_USER", "guest_demo_user");
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
