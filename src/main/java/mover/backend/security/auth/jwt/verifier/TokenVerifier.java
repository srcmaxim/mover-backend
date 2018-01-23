package mover.backend.security.auth.jwt.verifier;

/**
 * TokenVerifier is used for determine if jti is unique.
 */
public interface TokenVerifier {

    /**
     * Verifies is there sure no such token.
     *
     * @param jti an case sensitive unique identifier of the token even among different issuers.
     * @return false if there is no such token.
     */
    boolean verify(String jti);
}
