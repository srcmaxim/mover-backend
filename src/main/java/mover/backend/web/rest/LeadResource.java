package mover.backend.web.rest;

import mover.backend.model.Lead;
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
import java.util.Optional;

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
        if (id != null && leadRepository.existsById(id)) {
            leadRepository.deleteById(id);
        }
        return ResponseEntity.ok().build();
    }
}

