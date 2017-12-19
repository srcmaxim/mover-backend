package mover.backend.web.rest;

import mover.backend.model.Employee;
import mover.backend.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;

/**
 * REST controller for managing Employee.
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class EmployeeResource {

    private final Logger log = LoggerFactory.getLogger(EmployeeResource.class);

    private static final String ENTITY_NAME = "employee";

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeResource(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * GET  /employees : Queries employees from store.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of employees in body
     */
    @GetMapping("/employees")
    public Iterable<Employee> queryEmployees() {
        throw new UnsupportedOperationException();
    }

    /**
     * POST  /employees : Creates employee in store.
     *
     * @param employee the employee to create
     * @return the ResponseEntity with status 201 (Created) and with body the new employee,
     * or with status 400 (Bad Request) if the employee is not valid or the employee has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/employees")
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) throws URISyntaxException {
        throw new UnsupportedOperationException();
    }

    /**
     * PUT  /employees : Updates employee in store.
     *
     * @param employee the employee to update
     * @return the ResponseEntity with status 200 (OK),
     * or with status 400 (Bad Request) if the employee is not valid,
     * or with status 404 (Not Found) if there is no employee with this ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/employees")
    public ResponseEntity<Employee> updateEmployee(@Valid @RequestBody Employee employee) throws URISyntaxException {
        throw new UnsupportedOperationException();
    }

    /**
     * GET  /employees/:id : Finds employee in store by ID.
     *
     * @param id the ID of the employee to return
     * @return the ResponseEntity with status 200 (OK) and with body the employee,
     * or with status 404 (Not Found) if there is no employee with this ID
     */
    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> findEmployee(@PathVariable Long id) {
        throw new UnsupportedOperationException();
    }

    /**
     * DELETE  /employees/:id : Deletes employee in store by ID.
     *
     * @param id the ID of the employee to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        throw new UnsupportedOperationException();
    }
}

