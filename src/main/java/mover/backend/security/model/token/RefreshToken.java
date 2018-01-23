package mover.backend.security.model.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import mover.backend.security.exceptions.JwtExpiredTokenException;
import mover.backend.security.model.Scopes;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.List;
import java.util.Optional;

/**
 * RefreshToken is used for refreshing raw token.
 */
@SuppressWarnings("unchecked")
public class RefreshToken implements JwtToken {
    private Jws<Claims> claims;

    private RefreshToken(Jws<Claims> claims) {
        this.claims = claims;
    }

    /**
     * Creates and validates Refresh token 
     * 
     * @param token a token for refreshing
     * @param signingKey a claims as string
     * 
     * @throws BadCredentialsException if token not valid
     * @throws JwtExpiredTokenException if token has expired
     * 
     * @return RefreshToken a raw token
     */
    public static Optional<RefreshToken> create(RawAccessJwtToken token, String signingKey) {
        Jws<Claims> claims = token.parseClaims(signingKey);

        List<String> scopes = claims.getBody().get("scopes", List.class);
        if (scopes == null || scopes.isEmpty() 
                || !scopes.stream().filter(scope -> Scopes.REFRESH_TOKEN.authority().equals(scope)).findFirst().isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new RefreshToken(claims));
    }

    @Override
    public String getToken() {
        return null;
    }

    public Jws<Claims> getClaims() {
        return claims;
    }
    
    public String getJti() {
        return claims.getBody().getId();
    }
    
    public String getSubject() {
        return claims.getBody().getSubject();
    }
}
