package mover.backend.security.auth.jwt.verifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * BloomFilterTokenVerifier
 */
@Component
public class BloomFilterTokenVerifier implements TokenVerifier {

    private final Logger log = LoggerFactory.getLogger(BloomFilterTokenVerifier.class);

    private static Pattern ENCRYPTED_PASSWORD_PATTERN = Pattern
            .compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");
    
    @Override
    public boolean verify(String jti) {
        if (!ENCRYPTED_PASSWORD_PATTERN.matcher(jti).matches()) {
            log.warn("Encoded password does not look like BCrypt");
            return false;
        }
        return true;
    }
}
