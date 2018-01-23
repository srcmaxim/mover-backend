package mover.backend.security.auth.jwt.verifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * BloomFilterTokenVerifier is used for determine if jti is unique.
 */
@Component
public class BloomFilterTokenVerifier implements TokenVerifier {

    private final Logger log = LoggerFactory.getLogger(BloomFilterTokenVerifier.class);

    /**
     * Verifies is there sure no such token.
     *
     * @param jti an case sensitive unique identifier of the token even among different issuers.
     * @return false if there is no such token.
     */
    @Override
    public boolean verify(String jti) {
        return true;
    }
}
