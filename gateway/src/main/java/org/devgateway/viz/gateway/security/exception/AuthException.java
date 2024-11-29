package org.devgateway.viz.gateway.security.exception;


import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AuthException extends AuthenticationException {

    public AuthException(final String msg, final Throwable t) {
        super(msg, t);
    }

    public AuthException(final String msg) {
        super(msg);
    }
}
