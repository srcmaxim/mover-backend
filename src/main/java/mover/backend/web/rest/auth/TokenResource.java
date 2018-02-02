package mover.backend.web.rest.auth;

import mover.backend.model.User;
import mover.backend.security.UserService;
import mover.backend.security.auth.jwt.extractor.TokenExtractor;
import mover.backend.security.auth.jwt.verifier.TokenVerifier;
import mover.backend.security.config.JwtSettings;
import mover.backend.security.config.WebSecurityConfig;
import mover.backend.security.exceptions.InvalidJwtToken;
import mover.backend.security.model.UserContext;
import mover.backend.security.model.token.JwtToken;
import mover.backend.security.model.token.JwtTokenFactory;
import mover.backend.security.model.token.RawAccessJwtToken;
import mover.backend.security.model.token.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TokenResource uses for refreshing access token.
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class TokenResource {
    @Autowired
    private JwtTokenFactory tokenFactory;
    @Autowired
    private JwtSettings jwtSettings;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenVerifier tokenVerifier;
    @Autowired
    @Qualifier("jwtHeaderTokenExtractor") private TokenExtractor tokenExtractor;

    /**
     * GET  /auth/token : refreshes token
     * uses Header name defined in {@link WebSecurityConfig#AUTHENTICATION_HEADER_NAME}.
     *
     * @return the JwtTokene
     */
    @GetMapping("/auth/token")
    public JwtToken refreshToken(@RequestHeader(WebSecurityConfig.AUTHENTICATION_HEADER_NAME) String bearerToken) {
        String encodedToken = tokenExtractor.extract(bearerToken);

        RawAccessJwtToken rawToken = new RawAccessJwtToken(encodedToken);
        RefreshToken refreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey()).orElseThrow(() -> new InvalidJwtToken());

        String jti = refreshToken.getJti();
        if (!tokenVerifier.verify(jti)) {
            throw new InvalidJwtToken();
        }

        String subject = refreshToken.getSubject();
        User user = userService.getByUsername(subject).orElseThrow(() -> new UsernameNotFoundException("User not found: " + subject));

        if (user.getRoles() == null) throw new InsufficientAuthenticationException("User has no roles assigned");
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.name()))
                .collect(Collectors.toList());

        UserContext userContext = UserContext.create(user.getUsername(), authorities);

        return tokenFactory.createAccessJwtToken(userContext);
    }
}
