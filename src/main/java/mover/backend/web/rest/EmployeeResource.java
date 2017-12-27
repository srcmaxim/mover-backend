package mover.backend.web.rest;

import mover.backend.model.Employee;
import mover.backend.model.Lead;
import mover.backend.repository.EmployeeRepository;
import mover.backend.web.rest.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

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
        log.debug("REST request to get all Employees");
        Iterable<Employee> employees = employeeRepository.findAll();
        return employees;
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
        log.debug("REST request to save Employee : {}", employee);
        if (employee.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Employee result = employeeRepository.save(employee);
        return ResponseEntity.created(new URI("/api/employees/" + result.getId()))
                .body(result);
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
        log.debug("REST request to update Employee : {}", employee);
        if (employee.getId() != null && employeeRepository.existsById(employee.getId())) {
            employeeRepository.save(employee);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
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
        log.debug("REST request to get Employee : {}", id);
        Optional<Employee> employee = employeeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(employee);
    }

    /**
     * DELETE  /employees/:id : Deletes employee in store by ID.
     *
     * @param id the ID of the employee to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        log.debug("REST request to delete Employee : {}", id);
        if (id != null && employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /employees/:id/leads : Finds leads in store by employee ID.
     *
     * @param id the ID of the leads employee to return
     * @return the ResponseEntity with status 200 (OK) and with body the leads,
     * or with status 404 (Not Found) if there is no employee with this ID
     */
    @GetMapping("/employees/{id}/leads")
    public  ResponseEntity<Iterable<Lead>> findLeadsByEmployeeId(@PathVariable Long id) {
        log.debug("REST request to get Leads of Employee: {}", id);
        Optional<Iterable<Lead>> leads = employeeRepository.findById(id)
                .map(Employee::getLeads);
        return ResponseUtil.wrapOrNotFound(leads);
    }
}

