package mover.backend.repository;


import mover.backend.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends CrudRepository<User,Long> {
    @Query("select u from User u left join fetch u.roles r where u.username=:username")
    Optional<User> findByUsername(@Param("username") String username);
}