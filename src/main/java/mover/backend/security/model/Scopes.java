package mover.backend.security.model;

/**
 * Scopes uses for refresh token.
 */
public enum Scopes {
    REFRESH_TOKEN;
    
    public String authority() {
        return "ROLE_" + this.name();
    }
}
