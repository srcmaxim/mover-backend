package mover.backend.repository;

import mover.backend.model.Customer;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Customer entity.
 */
@Repository
public interface CustomerRepository extends BaseReposetory<Customer, Long> {
}