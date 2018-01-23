package mover.backend.security.auth.jwt.extractor;

/**
 * Implementations of this interface should always return raw base-64 encoded
 * representation of JWT Token.
 */
public interface TokenExtractor {

    /**
     * Extracts token from header.
     * @param header an value of Authentication HTTP header
     * @return JSON web token
     */
    String extract(String header);
}
