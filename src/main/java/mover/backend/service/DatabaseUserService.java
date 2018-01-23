package mover.backend.service;

import mover.backend.model.User;
import mover.backend.repository.UserRepository;
import mover.backend.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for the User entity.
 */
@Service
public class DatabaseUserService implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> getByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }
}
