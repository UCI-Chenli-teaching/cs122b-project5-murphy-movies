package login;

import common.RedisUtil;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    // Keep Redis-backed login sessions alive for 24 hours.
    private static final int SESSION_TTL_SECONDS = 24 * 60 * 60;
    public void init() {
        RedisUtil.init();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("LoginServlet: login request for user " + username);

        /* This example only allows username/password to be anteater/123456
        /  in the real project, you should talk to the database to verify username/password
        */
        JsonObject responseJsonObject = new JsonObject();
        if (username.equals("anteater") && password.equals("123456")) {
            // Login success:

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String loginTime = dateFormat.format(new Date());
            String sessionId = UUID.randomUUID().toString();

            // Serialize login session data as JSON for Redis set/get usage.
            JsonObject sessionObject = new JsonObject();
            sessionObject.addProperty("username", username);
            sessionObject.addProperty("loginTime", loginTime);
            // set(session:<id>, <serialized session json>, ttl): this is our login session store.
            RedisUtil.set("session:" + sessionId, sessionObject.toString(), SESSION_TTL_SECONDS);

            Cookie sessionCookie = new Cookie("redisSessionId", sessionId);
            sessionCookie.setHttpOnly(true);
            sessionCookie.setPath("/");
            sessionCookie.setMaxAge(SESSION_TTL_SECONDS);
            response.addCookie(sessionCookie);

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

        } else {
            // Login fail
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("Login failed");
            // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
            if (!username.equals("anteater")) {
                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
            } else {
                responseJsonObject.addProperty("message", "incorrect password");
            }
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
