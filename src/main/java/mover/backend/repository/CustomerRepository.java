package mover.backend.repository;

import mover.backend.model.Customer;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data JPA repository for the Customer entity.
 */
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}