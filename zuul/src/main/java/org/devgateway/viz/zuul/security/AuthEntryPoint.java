package org.devgateway.viz.zuul.security;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public class AuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException)
            throws IOException {
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.getWriter().write(new JSONObject()
                .put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .put("timestamp", LocalDateTime.now())
                .put("status", HttpServletResponse.SC_FORBIDDEN)
                .put("message", authException.getMessage()).toString());
    }
}
