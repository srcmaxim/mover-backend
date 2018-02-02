package mover.backend.web.rest.auth;

import mover.backend.security.auth.JwtAuthenticationToken;
import mover.backend.security.model.UserContext;
import org.springframework.web.bind.annotation.*;

/**
 * ProfileEndpoint is the ViewModel for user information
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class ProfileResource {

    @GetMapping(value="/whoami")
    public @ResponseBody UserContext whoami(JwtAuthenticationToken token) {
        return (UserContext) token.getPrincipal();
    }
}
