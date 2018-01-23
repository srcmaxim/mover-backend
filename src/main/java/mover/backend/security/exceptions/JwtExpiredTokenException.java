package mover.backend.security.exceptions;

import mover.backend.security.model.token.JwtToken;
import org.springframework.security.core.AuthenticationException;

/**
 * Uses when JWT iat + exp > Date.now().
 */
public class JwtExpiredTokenException extends AuthenticationException {
    private JwtToken token;

    public JwtExpiredTokenException(String msg) {
        super(msg);
    }

    public JwtExpiredTokenException(JwtToken token, String msg, Throwable t) {
        super(msg, t);
        this.token = token;
    }

    public String token() {
        return this.token.getToken();
    }
}
