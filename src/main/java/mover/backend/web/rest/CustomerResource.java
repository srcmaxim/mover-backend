package mover.backend.web.rest;

import mover.backend.model.Customer;
import mover.backend.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;

/**
 * REST controller for managing Customer.
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class CustomerResource {

    private final Logger log = LoggerFactory.getLogger(CustomerResource.class);

    private static final String ENTITY_NAME = "customer";

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerResource(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * GET  /customers : Queries customers from store.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of customers in body
     */
    @GetMapping("/customers")
    public Iterable<Customer> queryCustomers() {
        throw new UnsupportedOperationException();
    }

    /**
     * POST  /customers : Creates customer in store.
     *
     * @param customer the customer to create
     * @return the ResponseEntity with status 201 (Created) and with body the new customer,
     * or with status 400 (Bad Request) if the customer is not valid or the customer has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) throws URISyntaxException {
        throw new UnsupportedOperationException();
    }

    /**
     * PUT  /customers : Updates customer in store.
     *
     * @param customer the customer to update
     * @return the ResponseEntity with status 200 (OK),
     * or with status 400 (Bad Request) if the customer is not valid,
     * or with status 404 (Not Found) if there is no customer with this ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/customers")
    public ResponseEntity<Customer> updateCustomer(@Valid @RequestBody Customer customer) throws URISyntaxException {
        throw new UnsupportedOperationException();
    }

    /**
     * GET  /customers/:id : Finds customer in store by ID.
     *
     * @param id the ID of the customer to return
     * @return the ResponseEntity with status 200 (OK) and with body the customer,
     * or with status 404 (Not Found) if there is no customer with this ID
     */
    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> findCustomer(@PathVariable Long id) {
        throw new UnsupportedOperationException();
    }

    /**
     * DELETE  /customers/:id : Deletes customer in store by ID.
     *
     * @param id the ID of the customer to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        throw new UnsupportedOperationException();
    }
}

