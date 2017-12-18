package mover.backend.web.rest;

import mover.backend.model.Lead;
import mover.backend.repository.LeadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;

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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    /**
     * DELETE  /leads/:id : Deletes lead in store by ID.
     *
     * @param id the ID of the lead to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/leads/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        throw new UnsupportedOperationException();
    }

}

