package mover.backend.web.rest;

import mover.backend.model.Customer;
import mover.backend.model.Lead;
import mover.backend.repository.CustomerRepository;
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
        log.debug("REST request to get all Customers");
        Iterable<Customer> customers = customerRepository.findAll();
        return customers;
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
        log.debug("REST request to save Customer : {}", customer);
        if (customer.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Customer result = customerRepository.save(customer);
        return ResponseEntity.created(new URI("/api/customers/" + result.getId()))
                .body(result);
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
        log.debug("REST request to update Customer : {}", customer);
        if (customer.getId() != null && customerRepository.existsById(customer.getId())) {
            customerRepository.save(customer);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
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
        log.debug("REST request to get Customer : {}", id);
        Optional<Customer> customer = customerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(customer);
    }

    /**
     * DELETE  /customers/:id : Deletes customer in store by ID.
     *
     * @param id the ID of the customer to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.debug("REST request to delete Customer : {}", id);
        if (id != null && customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /customers/:id/leads : Finds leads in store by customer ID.
     *
     * @param id the ID of the leads customer to return
     * @return the ResponseEntity with status 200 (OK) and with body the leads,
     * or with status 404 (Not Found) if there is no customer with this ID
     */
    @GetMapping("/customers/{id}/leads")
    public  ResponseEntity<Iterable<Lead>> findLeadsByCustomerId(@PathVariable Long id) {
        throw new UnsupportedOperationException();
    }
}

