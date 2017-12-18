package mover.backend.web.rest;

import mover.backend.BackendApplication;
import mover.backend.model.Address;
import mover.backend.model.Lead;
import mover.backend.model.enumeration.Status;
import mover.backend.model.enumeration.Type;
import mover.backend.repository.LeadRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static mover.backend.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the LeadResource REST controller.
 *
 * @see LeadResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BackendApplication.class)
public class LeadResourceIntTest {

    private static final ZonedDateTime DEFAULT_START = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Type DEFAULT_TYPE = Type.LOCAL;
    private static final Type UPDATED_TYPE = Type.DISTANCE;

    private static final Status DEFAULT_STATUS = Status.PENDING;
    private static final Status UPDATED_STATUS = Status.ASSIGNED;

    private static final Address DEFAULT_ORIGIN = new Address("Default origin", 0D, 0D);
    private static final Address UPDATED_ORIGIN = new Address("Updated origin", 1D, 1D);

    private static final Address DEFAULT_DESTINATION = new Address("Default destination", 0D, 0D);
    private static final Address UPDATED_DESTINATION = new Address("Updated destination", 1D, 1D);

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private EntityManager em;

    private MockMvc restLeadMockMvc;

    private Lead lead;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LeadResource leadResource = new LeadResource(leadRepository);
        this.restLeadMockMvc = MockMvcBuilders.standaloneSetup(leadResource)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lead createEntity(EntityManager em) {
        Lead lead = new Lead()
                .setStart(DEFAULT_START)
                .setEnd(DEFAULT_END)
                .setType(DEFAULT_TYPE)
                .setStatus(DEFAULT_STATUS)
                .setOrigin(DEFAULT_ORIGIN)
                .setDestination(DEFAULT_DESTINATION);
        return lead;
    }

    @Before
    public void initTest() {
        lead = createEntity(em);
    }

    @Test
    @Transactional
    public void queryLeads() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get all the leadList
        restLeadMockMvc.perform(get("/api/leads"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(lead.getId().intValue())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(DEFAULT_START))))
                .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(DEFAULT_END))))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].origin.address").value(hasItem(DEFAULT_ORIGIN.getAddress())))
                .andExpect(jsonPath("$.[*].destination.address").value(hasItem(DEFAULT_DESTINATION.getAddress())));
    }

    @Test
    @Transactional
    public void createLead() throws Exception {
        long databaseSizeBeforeCreate = leadRepository.count();

        // Create the Lead
        restLeadMockMvc.perform(post("/api/leads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(lead)))
                .andExpect(status().isCreated());

        // Validate the Lead in the database
        List<Lead> leadList = leadRepository.findAllByOrderByIdAsc();
        assertThat(leadList).hasSize((int) (databaseSizeBeforeCreate + 1));
        Lead testLead = leadList.get(leadList.size() - 1);
        assertThat(testLead.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testLead.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testLead.getEnd()).isEqualTo(DEFAULT_END);
        assertThat(testLead.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testLead.getOrigin()).isEqualTo(DEFAULT_ORIGIN);
        assertThat(testLead.getDestination()).isEqualTo(DEFAULT_DESTINATION);
    }

    @Test
    @Transactional
    public void createLeadWithExistingId() throws Exception {
        long databaseSizeBeforeCreate = leadRepository.count();

        // Create the Lead with an existing ID
        lead.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLeadMockMvc.perform(post("/api/leads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(lead)))
                .andExpect(status().isBadRequest());

        long databaseSizeAfterCreate = leadRepository.count();
        assertThat(databaseSizeAfterCreate).isEqualTo(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void createNotValidLead() throws Exception {
        long databaseSizeBeforeCreate = leadRepository.count();
        // set the fields where end < start
        lead
            .setStart(DEFAULT_END)
            .setEnd(DEFAULT_START);

        // Create the lead, which fails.
        restLeadMockMvc.perform(post("/api/leads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(lead)))
                .andExpect(status().isBadRequest());

        long databaseSizeAfterCreate = leadRepository.count();
        assertThat(databaseSizeAfterCreate).isEqualTo(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void updateLead() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);
        long databaseSizeBeforeUpdate = leadRepository.count();

        // Update the lead
        Lead updatedLead = leadRepository.findById(lead.getId()).get();
        updatedLead
                .setType(UPDATED_TYPE)
                .setStart(UPDATED_START)
                .setEnd(UPDATED_END)
                .setStatus(UPDATED_STATUS)
                .setOrigin(UPDATED_ORIGIN)
                .setDestination(UPDATED_DESTINATION);

        restLeadMockMvc.perform(put("/api/leads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedLead)))
                .andExpect(status().isOk());

        // Validate the Lead in the database
        List<Lead> leadList = leadRepository.findAllByOrderByIdAsc();
        assertThat(leadList).hasSize((int) databaseSizeBeforeUpdate);
        Lead testLead = leadList.get(leadList.size() - 1);
        assertThat(testLead.getStart()).isEqualTo(UPDATED_START);
        assertThat(testLead.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testLead.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testLead.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testLead.getOrigin()).isEqualTo(DEFAULT_ORIGIN);
        assertThat(testLead.getDestination()).isEqualTo(DEFAULT_DESTINATION);
    }

    @Test
    @Transactional
    public void updateNotValidLead() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);
        long databaseSizeBeforeUpdate = leadRepository.count();

        // Update the lead, which fails.
        Lead updatedLead = leadRepository.findById(lead.getId()).get();

        // set the fields where end < start
        updatedLead
            .setStart(DEFAULT_END)
            .setEnd(DEFAULT_START);

        // update lead witch fails
        restLeadMockMvc.perform(put("/api/leads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedLead)))
                .andExpect(status().isBadRequest());

        // Validate the Lead in the database
        List<Lead> leadList = leadRepository.findAllByOrderByIdAsc();
        assertThat(leadList).hasSize((int) databaseSizeBeforeUpdate);
        Lead testLead = leadList.get(leadList.size() - 1);
        assertThat(testLead.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testLead.getEnd()).isEqualTo(DEFAULT_END);
    }

    @Test
    @Transactional
    public void updateNonExistingLead() throws Exception {
        long databaseSizeBeforeUpdate = leadRepository.count();

        // Non existing Lead
        lead.setId(Long.MAX_VALUE);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restLeadMockMvc.perform(put("/api/leads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(lead)))
                .andExpect(status().isNotFound());

        // Validate the Lead in the database
        List<Lead> leadList = leadRepository.findAllByOrderByIdAsc();
        assertThat(leadList).hasSize((int) (databaseSizeBeforeUpdate));
    }

    @Test
    @Transactional
    public void findLead() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);

        // Get the lead
        restLeadMockMvc.perform(get("/api/leads/{id}", lead.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(lead.getId().intValue()))
                .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
                .andExpect(jsonPath("$.start").value(sameInstant(DEFAULT_START)))
                .andExpect(jsonPath("$.end").value(sameInstant(DEFAULT_END)))
                .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
                .andExpect(jsonPath("$.origin.address").value(hasItem(DEFAULT_ORIGIN.getAddress())))
                .andExpect(jsonPath("$.destination.address").value(hasItem(DEFAULT_DESTINATION.getAddress())));
    }

    @Test
    @Transactional
    public void findNonExistingLead() throws Exception {
        // Get the lead
        restLeadMockMvc.perform(get("/api/leads/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void deleteLead() throws Exception {
        // Initialize the database
        leadRepository.saveAndFlush(lead);
        long databaseSizeBeforeDelete = leadRepository.count();

        // Get the lead
        restLeadMockMvc.perform(delete("/api/leads/{id}", lead.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Lead> leadList = leadRepository.findAllByOrderByIdAsc();
        assertThat(leadList).hasSize((int) (databaseSizeBeforeDelete - 1));
    }

    @Test
    @Transactional
    public void deleteNonExistingLead() throws Exception {
        long databaseSizeBeforeDelete = leadRepository.count();

        // Get the lead
        restLeadMockMvc.perform(delete("/api/leads/{id}", Long.MAX_VALUE)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Lead> leadList = leadRepository.findAllByOrderByIdAsc();
        assertThat(leadList).hasSize((int) (databaseSizeBeforeDelete));
    }
}
