package mover.backend.security.auth.ajax;

import mover.backend.model.User;
import mover.backend.profile.endpoint.ProfileEndpoint;
import mover.backend.security.model.UserContext;
import mover.backend.service.DatabaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AjaxAuthenticationProvider performs authentication in application.
 */
@Component
public class AjaxAuthenticationProvider implements AuthenticationProvider {
    private final PasswordEncoder encoder;
    private final DatabaseUserService userService;

    @Autowired
    public AjaxAuthenticationProvider(final DatabaseUserService userService, final PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    /**
     * The main responsibilities are:
     * 1. Verify user credentials against database, LDAP or some other system which holds the user data.
     * 2. Create {@link UserContext} and populate it with user data you need (in our case just username and user privileges).
     * The {@link UserContext} is used in {@link ProfileEndpoint} to request information about user.
     *
     * @param authentication holds credentials and principals of the user.
     * @return
     * @throws AuthenticationException if username and password do not match.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");

        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        User user = userService.getByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
        }

        if (user.getRoles() == null) throw new InsufficientAuthenticationException("User has no roles assigned");
        
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.name()))
                .collect(Collectors.toList());
        
        UserContext userContext = UserContext.create(user.getUsername(), authorities);
        
        return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
