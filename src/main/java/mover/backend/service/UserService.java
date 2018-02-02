package mover.backend.service;

import mover.backend.model.User;

import java.util.Optional;

/**
 * Service for the User entity.
 */
public interface UserService {
    Optional<User> getByUsername(String username);
}
