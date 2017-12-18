package mover.backend.repository;

import mover.backend.model.Employee;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Employee entity.
 */
@Repository
public interface EmployeeRepository extends BaseReposetory<Employee, Long> {
}
