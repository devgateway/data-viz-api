package org.devgateway.viz.zuul.controller;

import org.devgateway.viz.zuul.security.exception.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExceptionHandlerController {

    @ExceptionHandler(AuthException.class)
    public void handleAuthException(HttpServletResponse response, AuthException e) throws IOException {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }
}
