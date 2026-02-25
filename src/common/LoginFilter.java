package common;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    // Session TTL must match LoginServlet, so active users stay logged in.
    private static final int SESSION_TTL_SECONDS = 24 * 60 * 60;
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("LoginFilter: " + httpRequest.getRequestURI());

        // Check if this URL is allowed to access without logging in
        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            // Keep default action: pass along the filter chain
            chain.doFilter(request, response);
            return;
        }

        String sessionId = getCookieValue(httpRequest, "redisSessionId");
        if (sessionId == null) {
            httpResponse.sendRedirect("login.html");
            return;
        }

        String sessionKey = "session:" + sessionId;
        try {
            // get(session:<id>): load serialized session JSON from Redis.
            String sessionJson = RedisUtil.get(sessionKey);
            if (sessionJson == null || sessionJson.isEmpty()) {
                httpResponse.sendRedirect("login.html");
                return;
            }

            JsonObject sessionObject = JsonParser.parseString(sessionJson).getAsJsonObject();
            String username = sessionObject.get("username").getAsString();
            String loginTime = sessionObject.get("loginTime").getAsString();

            // set(session:<id>, same json, ttl): refresh Redis session TTL for active users.
            RedisUtil.set(sessionKey, sessionJson, SESSION_TTL_SECONDS);

            // Attach session data for downstream endpoints
            httpRequest.setAttribute("username", username);
            httpRequest.setAttribute("loginTime", loginTime);

            chain.doFilter(request, response);
        } catch (Exception e) {
            request.getServletContext().log("LoginFilter Redis error", e);
            httpResponse.sendRedirect("login.html");
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");
        RedisUtil.init();
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void destroy() {
        // ignored.
    }

}
