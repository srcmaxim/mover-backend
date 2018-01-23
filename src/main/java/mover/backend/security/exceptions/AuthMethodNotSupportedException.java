package mover.backend.security.exceptions;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * Uses when HTTP method isn't POST
 */
public class AuthMethodNotSupportedException extends AuthenticationServiceException {

    public AuthMethodNotSupportedException(String msg) {
        super(msg);
    }
}
