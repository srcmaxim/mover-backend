package mover.backend.security;

import mover.backend.model.User;

import java.util.Optional;

/**
 *
 */
public interface UserService {
    Optional<User> getByUsername(String username);
}
