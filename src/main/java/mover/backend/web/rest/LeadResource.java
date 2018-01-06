package mover.backend.web.rest;

import mover.backend.model.*;
import mover.backend.repository.LeadRepository;
import mover.backend.web.rest.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * REST controller for managing Lead.
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class LeadResource {

    private final Logger log = LoggerFactory.getLogger(LeadResource.class);

    private static final String ENTITY_NAME = "lead";

    private final LeadRepository leadRepository;

    @Autowired
    public LeadResource(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    /**
     * GET  /leads : Queries leads from store.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of leads in body
     */
    @GetMapping("/leads")
    public Iterable<Lead> queryLeads() {
        log.debug("REST request to get all Leads");
        Iterable<Lead> leads = leadRepository.findAll();
        return leads;
    }

    /**
     * POST  /leads : Creates lead in store.
     *
     * @param lead the lead to create
     * @return the ResponseEntity with status 201 (Created) and with body the new lead,
     * or with status 400 (Bad Request) if the lead is not valid or the lead has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/leads")
    public ResponseEntity<Lead> createLead(@Valid @RequestBody Lead lead) throws URISyntaxException {
        log.debug("REST request to save Lead : {}", lead);
        if (lead.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Lead result = leadRepository.save(lead);
        return ResponseEntity.created(new URI("/api/leads/" + result.getId()))
                .body(result);
    }

    /**
     * PUT  /leads : Updates lead in store.
     *
     * @param lead the lead to update
     * @return the ResponseEntity with status 200 (OK),
     * or with status 400 (Bad Request) if the lead is not valid,
     * or with status 404 (Not Found) if there is no lead with this ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/leads")
    public ResponseEntity<Lead> updateLead(@Valid @RequestBody Lead lead) throws URISyntaxException {
        log.debug("REST request to update Lead : {}", lead);
        if (lead.getId() != null && leadRepository.existsById(lead.getId())) {
            leadRepository.save(lead);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * GET  /leads/:id : Finds lead in store by ID.
     *
     * @param id the ID of the lead to return
     * @return the ResponseEntity with status 200 (OK) and with body the lead,
     * or with status 404 (Not Found) if there is no lead with this ID
     */
    @GetMapping("/leads/{id}")
    public ResponseEntity<Lead> findLead(@PathVariable Long id) {
        log.debug("REST request to get Lead : {}", id);
        Optional<Lead> lead = leadRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(lead);
    }

    /**
     * DELETE  /leads/:id : Deletes lead in store by ID.
     *
     * @param id the ID of the lead to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/leads/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        log.debug("REST request to delete Lead : {}", id);
        if (id != null) {
            leadRepository.findById(id).ifPresent(lead -> {
                Optional.ofNullable(lead.getCustomer())
                        .ifPresent(customer -> customer.getLeads().remove(lead));
                lead.setCustomer(null);
                lead.getAssignedTos().forEach(employee -> employee.getLeads().remove(lead));
                lead.setAssignedTos(Collections.emptySet());
                leadRepository.delete(lead);
            });
        }
        return ResponseEntity.ok().build();
    }

    /* EMBEDDED */

    /**
     * GET  /leads/:id/estimates : Finds estimates in store by lead ID.
     *
     * @param id the ID of the estimates lead to return
     * @return the ResponseEntity with status 200 (OK) and with body the estimates,
     * or with status 404 (Not Found) if there is no lead with this ID
     */
    @GetMapping("/leads/{id}/estimates")
    public  ResponseEntity<Iterable<Estimate>> findEstimates(@PathVariable Long id) {
        log.debug("REST request to get Estimates of Lead: {}", id);
        Optional<Iterable<Estimate>> estimates = leadRepository.findById(id)
                .map(Lead::getEstimates);
        return ResponseUtil.wrapOrNotFound(estimates);
    }

    /**
     * PUT  /leads/:id/estimates  : Updates lead estimates in store by lead ID.
     *
     * @param id the ID of the estimates lead to update
     * @param estimates  the estimates to update
     * @return the ResponseEntity with status 200 (OK),
     * or with status 400 (Bad Request) if the Estimates are not valid,
     * or with status 404 (Not Found) if there is no lead with this ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("leads/{id}/estimates")
    public ResponseEntity<Void> updateEstimates(@PathVariable Long id, @Valid @RequestBody Set<Estimate> estimates) throws URISyntaxException {
        log.debug("REST request to update Estimates of Lead: {}", id);
        if (id != null && leadRepository.existsById(id)) {
            leadRepository.findById(id).ifPresent((Lead lead) -> {
                lead.setEstimates(estimates);
                leadRepository.save(lead);
            });
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * GET  /leads/:id/inventories : Finds inventories in store by lead ID.
     *
     * @param id the ID of the inventories lead to return
     * @return the ResponseEntity with status 200 (OK) and with body the inventories,
     * or with status 404 (Not Found) if there is no lead with this ID
     */
    @GetMapping("/leads/{id}/inventories")
    public  ResponseEntity<Iterable<Inventory>> findInventories(@PathVariable Long id) {
        log.debug("REST request to get Inventories of Lead: {}", id);
        Optional<Iterable<Inventory>> inventories = leadRepository.findById(id)
                .map(Lead::getInventories);
        return ResponseUtil.wrapOrNotFound(inventories);
    }

    /**
     * PUT  /leads/:id/inventories  : Updates lead inventories in store by lead ID.
     *
     * @param id the ID of the inventories lead to update
     * @param inventories  the inventories to update
     * @return the ResponseEntity with status 200 (OK),
     * or with status 400 (Bad Request) if the Inventories are not valid,
     * or with status 404 (Not Found) if there is no lead with this ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("leads/{id}/inventories")
    public ResponseEntity<Void> updateInventories(@PathVariable Long id, @Valid @RequestBody Set<Inventory> inventories) throws URISyntaxException {
        log.debug("REST request to update Inventories of Lead: {}", id);
        if (id != null && leadRepository.existsById(id)) {
            leadRepository.findById(id).ifPresent((Lead lead) -> {
                lead.setInventories(inventories);
                leadRepository.save(lead);
            });
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /* ENTITIES */

    /**
     * GET  /leads/:id/customer : Finds customer in store by lead ID.
     *
     * @param id the ID of the customer lead to return
     * @return the ResponseEntity with status 200 (OK) and with body the customer,
     * or with status 404 (Not Found) if there is no lead with this ID
     */
    @GetMapping("/leads/{id}/customer")
    public  ResponseEntity<Customer> findCustomerByLeadId(@PathVariable Long id) {
        log.debug("REST request to get Customer of Lead: {}", id);
        Optional<Customer> customer = leadRepository.findById(id)
                .map(Lead::getCustomer);
        return ResponseUtil.wrapOrNotFound(customer);
    }

    /**
     * GET  /leads/:id/employees : Finds employees in store by lead ID.
     *
     * @param id the ID of the employees lead to return
     * @return the ResponseEntity with status 200 (OK) and with body the employees,
     * or with status 404 (Not Found) if there is no lead with this ID
     */
    @GetMapping("/leads/{id}/employees")
    public  ResponseEntity<Iterable<Employee>> findEmployeesByLeadId(@PathVariable Long id) {
        log.debug("REST request to get Employees of Lead: {}", id);
        Optional<Iterable<Employee>> employees = leadRepository.findById(id)
                .map(Lead::getAssignedTos);
        return ResponseUtil.wrapOrNotFound(employees);
    }
}

