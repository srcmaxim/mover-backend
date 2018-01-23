package mover.backend.profile.endpoint;

import mover.backend.security.auth.JwtAuthenticationToken;
import mover.backend.security.model.UserContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProfileEndpoint is the ViewModel for user information
 */
@RestController
public class ProfileEndpoint {
    @RequestMapping(value="/api/me", method= RequestMethod.GET)
    public @ResponseBody
    UserContext get(JwtAuthenticationToken token) {
        return (UserContext) token.getPrincipal();
    }
}
