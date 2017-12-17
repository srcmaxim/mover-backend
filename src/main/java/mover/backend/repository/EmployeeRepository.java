package mover.backend.repository;

import mover.backend.model.Employee;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data JPA repository for the Employee entity.
 */
public interface EmployeeRepository extends CrudRepository<Employee, Long> {
}
