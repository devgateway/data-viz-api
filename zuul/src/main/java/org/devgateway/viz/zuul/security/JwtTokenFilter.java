package org.devgateway.viz.security.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import org.devgateway.viz.security.security.exception.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class JwtTokenFilter extends GenericFilterBean {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);

        if (token != null) {
            if (!jwtTokenProvider.isTokenPresentInDB(token)) {
                sendError(response, new AuthException("Invalid JWT token"));
            }
            try {
                jwtTokenProvider.validateToken(token);
            } catch (JwtException | IllegalArgumentException e) {
                sendError(response, new AuthException("Invalid JWT token"));
            }

            Authentication auth = token != null ? jwtTokenProvider.getAuthentication(token) : null;
            //setting auth in the context.
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(req, res);
    }

    private void sendError(final HttpServletResponse response, final AuthException e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("message", e.getMessage());
        response.getWriter().write(MAPPER.writeValueAsString(body));
        throw e;
    }
}
