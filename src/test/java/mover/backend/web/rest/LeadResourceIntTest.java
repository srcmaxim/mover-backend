package mover.backend.web.rest;

import mover.backend.BackendApplication;
import mover.backend.model.Address;
import mover.backend.model.Estimate;
import mover.backend.model.Inventory;
import mover.backend.model.Lead;
import mover.backend.model.enumeration.Category;
import mover.backend.model.enumeration.Status;
import mover.backend.model.enumeration.Type;
import mover.backend.repository.LeadRepository;
import mover.backend.web.rest.advice.ExceptionAdvice;
import mover.backend.web.rest.advice.ValidatorAdvice;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
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

    private static final LocalDateTime DEFAULT_START = LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final LocalDateTime UPDATED_START = LocalDateTime.ofInstant(Instant.ofEpochMilli(10L), ZoneOffset.UTC);

    private static final LocalDateTime DEFAULT_END = LocalDateTime.ofInstant(Instant.ofEpochMilli(100L), ZoneOffset.UTC);
    private static final LocalDateTime UPDATED_END = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000L), ZoneOffset.UTC);

    private static final Type DEFAULT_TYPE = Type.LOCAL;
    private static final Type UPDATED_TYPE = Type.DISTANCE;

    private static final Status DEFAULT_STATUS = Status.PENDING;
    private static final Status UPDATED_STATUS = Status.ASSIGNED;

    private static final Address DEFAULT_ORIGIN = new Address("Default origin", 0D, 0D);
    private static final Address UPDATED_ORIGIN = new Address("Updated origin", 1D, 1D);

    private static final Address DEFAULT_DESTINATION = new Address("Default destination", 0D, 0D);
    private static final Address UPDATED_DESTINATION = new Address("Updated destination", 1D, 1D);

    /* ESTIMATES */
    private static final List<Estimate> DEFAULT_ESTIMATES = asList(
            new Estimate("Default estimate 1", 1, 100),
            new Estimate("Default estimate 2", 2, 200),
            new Estimate("Default estimate 3", 3, 300)
    );
    private static final List<Estimate> UPDATED_ESTIMATES = asList(
            new Estimate("Updated estimate 1", 1, 100),
            new Estimate("Updated estimate 2", 2, 200),
            new Estimate("Updated estimate 3", 3, 300)
    );

    /* INVENTORIES */
    private static final List<Inventory> DEFAULT_INVENTORIES = asList(
            new Inventory(Category.BED, "Default estimate 1", 1, 10,100),
            new Inventory(Category.LIVING, "Default estimate 2", 2, 20,200),
            new Inventory(Category.OFFICE, "Default estimate 3", 3, 30,300)
    );
    private static final List<Inventory> UPDATED_INVENTORIES = asList(
            new Inventory(Category.BED, "Updated estimate 1", 1, 10,100),
            new Inventory(Category.LIVING, "Updated estimate 2", 2, 20,200),
            new Inventory(Category.OFFICE, "Updated estimate 3", 3, 30,300)
    );

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private ExceptionAdvice exceptionTranslator;

    @Autowired
    private ValidatorAdvice validatorTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restLeadMockMvc;

    private Lead lead;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LeadResource leadResource = new LeadResource(leadRepository);
        this.restLeadMockMvc = MockMvcBuilders.standaloneSetup(leadResource)
                .setControllerAdvice(exceptionTranslator)
                .setControllerAdvice(validatorTranslator)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lead createEntity() {
        Lead lead = new Lead()
                .setStart(DEFAULT_START)
                .setEnd(DEFAULT_END)
                .setType(DEFAULT_TYPE)
                .setStatus(DEFAULT_STATUS)
                .setOrigin(DEFAULT_ORIGIN)
                .setDestination(DEFAULT_DESTINATION);
        lead.getEstimates().addAll(DEFAULT_ESTIMATES);
        lead.getInventories().addAll(DEFAULT_INVENTORIES);
        return lead;
    }

    public Lead getLastLead() {
        return (Lead) em.createQuery("select l from Lead l order by l.id desc")
                .setMaxResults(1).getSingleResult();
    }

    public int getCount() {
        return (int) leadRepository.count();
    }

    public int getLastLeadCountEstimates() {
        return getLastLead().getEstimates().size();
    }

    private int getLastLeadCountInventories() {
        return getLastLead().getInventories().size();
    }

    public void saveAndFlush(Lead lead) {
        em.persist(lead);
        em.flush();
    }

    @Before
    public void initTest() {
        lead = createEntity();
    }

    @Test
    @Transactional
    public void queryLeads() throws Exception {
        // Initialize the database
        saveAndFlush(lead);

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
        int databaseSizeBeforeCreate = getCount();

        // Create the Lead
        restLeadMockMvc.perform(post("/api/leads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(lead)))
                .andExpect(status().isCreated());

        // Validate the Lead in the database
        assertThat(getCount()).isEqualTo(databaseSizeBeforeCreate + 1);
        Lead testLead = getLastLead();
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
        int databaseSizeBeforeCreate = getCount();

        // Create the Lead with an existing ID
        lead.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLeadMockMvc.perform(post("/api/leads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(lead)))
                .andExpect(status().isBadRequest());

        assertThat(getCount()).isEqualTo(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void createNotValidLead() throws Exception {
        int databaseSizeBeforeCreate = getCount();

        // Set the fields where end < start
        lead
                .setStart(DEFAULT_END)
                .setEnd(DEFAULT_START);

        // Create the lead, which fails.
        restLeadMockMvc.perform(post("/api/leads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(lead)))
                .andExpect(status().isBadRequest());

        assertThat(getCount()).isEqualTo(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void updateLead() throws Exception {
        // Initialize the database
        saveAndFlush(lead);

        int databaseSizeBeforeUpdate = getCount();

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
        assertThat(getCount()).isEqualTo(databaseSizeBeforeUpdate);
        Lead testLead = getLastLead();
        assertThat(testLead.getStart()).isEqualTo(UPDATED_START);
        assertThat(testLead.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testLead.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testLead.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testLead.getOrigin()).isEqualTo(UPDATED_ORIGIN);
        assertThat(testLead.getDestination()).isEqualTo(UPDATED_DESTINATION);
    }

    @Test
    @Transactional
    public void updateNotValidLead() throws Exception {
        // Initialize the database
        saveAndFlush(lead);
        em.detach(lead);

        int databaseSizeBeforeUpdate = getCount();

        // Set the fields where end < start
        lead
                .setStart(DEFAULT_END)
                .setEnd(DEFAULT_START);

        // update lead witch fails
        restLeadMockMvc.perform(put("/api/leads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(lead)))
                .andExpect(status().isBadRequest());

        // Validate the Lead in the database
        assertThat(getCount()).isEqualTo(databaseSizeBeforeUpdate);
        Lead testLead = getLastLead();
        assertThat(testLead.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testLead.getEnd()).isEqualTo(DEFAULT_END);
    }

    @Test
    @Transactional
    public void updateNonExistingLead() throws Exception {
        int databaseSizeBeforeUpdate = getCount();

        // Non existing Lead
        lead.setId(Long.MAX_VALUE);

        // update with unexisting ID fails
        restLeadMockMvc.perform(put("/api/leads")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(lead)))
                .andExpect(status().isNotFound());

        assertThat(getCount()).isEqualTo(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void findLead() throws Exception {
        // Initialize the database
        saveAndFlush(lead);

        // Get the lead
        restLeadMockMvc.perform(get("/api/leads/{id}", lead.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(lead.getId().intValue()))
                .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
                .andExpect(jsonPath("$.start").value(sameInstant(DEFAULT_START)))
                .andExpect(jsonPath("$.end").value(sameInstant(DEFAULT_END)))
                .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
                .andExpect(jsonPath("$.origin.address").value(DEFAULT_ORIGIN.getAddress()))
                .andExpect(jsonPath("$.destination.address").value(DEFAULT_DESTINATION.getAddress()));
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
        saveAndFlush(lead);

        int databaseSizeBeforeDelete = getCount();

        // Get the lead
        restLeadMockMvc.perform(delete("/api/leads/{id}", lead.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        assertThat(getCount()).isEqualTo(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void deleteNonExistingLead() throws Exception {
        int databaseSizeBeforeDelete = getCount();

        // Get the lead
        restLeadMockMvc.perform(delete("/api/leads/{id}", Long.MAX_VALUE)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        assertThat(getCount()).isEqualTo(databaseSizeBeforeDelete);
    }

    /* ESTIMATES */

    @Test
    @Transactional
    public void findEstimates() throws Exception {
        // Initialize the database
        saveAndFlush(lead);

        // Get the estimates of the lead
        restLeadMockMvc.perform(get("/api/leads/{id}/estimates", lead.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_ESTIMATES.get(0).getName())))
                .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_ESTIMATES.get(0).getQuantity())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_ESTIMATES.get(0).getPrice())))

                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_ESTIMATES.get(1).getName())))
                .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_ESTIMATES.get(1).getQuantity())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_ESTIMATES.get(1).getPrice())))

                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_ESTIMATES.get(2).getName())))
                .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_ESTIMATES.get(2).getQuantity())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_ESTIMATES.get(2).getPrice())));
    }

    @Test
    @Transactional
    public void findNonExistingEstimates() throws Exception {
        // Get the estimates of the lead
        restLeadMockMvc.perform(get("/api/leads/{id}/estimates", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEstimates() throws Exception {
        // Initialize the database
        saveAndFlush(lead);

        int databaseSizeBeforeUpdate = getLastLeadCountEstimates();

        // Update the estimates of the lead
        Lead lead = leadRepository.findById(this.lead.getId()).get();

        lead.setEstimates(new HashSet<>(UPDATED_ESTIMATES));

        restLeadMockMvc.perform(put("/api/leads/{id}/estimates", this.lead.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(lead.getEstimates())))
                .andExpect(status().isOk());

        // Validate the Estimates in the database
        assertThat(getLastLeadCountEstimates()).isEqualTo(databaseSizeBeforeUpdate);
        Iterable<Estimate> testEstimates = getLastLead().getEstimates();
        assertThat(testEstimates).containsExactlyInAnyOrder((Estimate[]) UPDATED_ESTIMATES.toArray());
    }

    @Test
    @Transactional
    public void updateNotValidEstimates() throws Exception {
        // Initialize the database
        saveAndFlush(lead);
        em.detach(lead);

        int databaseSizeBeforeUpdate = getLastLeadCountEstimates();

        // Set the fields where params aren't valid
        lead.setEstimates(new HashSet<>(asList(
                new Estimate("", 0, 0),
                new Estimate(null, -1, -1)
        )));


        // update estimates witch fails
        restLeadMockMvc.perform(put("/api/leads/{id}/estimates", lead.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(lead.getEstimates())))
                .andExpect(status().isBadRequest());

        // Validate the Lead in the database
        assertThat(getLastLeadCountEstimates()).isEqualTo(databaseSizeBeforeUpdate);
        Iterable<Estimate> testEstimates = getLastLead().getEstimates();
        assertThat(testEstimates).containsExactlyInAnyOrder((Estimate[]) DEFAULT_ESTIMATES.toArray());
    }

    @Test
    @Transactional
    public void updateNonExistingEstimates() throws Exception {
        int databaseSizeBeforeUpdate = getCount();

        // Non existing Lead
        lead.setId(Long.MAX_VALUE);

        // update with unexisting ID fails
        restLeadMockMvc.perform(put("/api/leads/{id}/estimates", lead.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(lead.getEstimates())))
                .andExpect(status().isNotFound());

        assertThat(getCount()).isEqualTo(databaseSizeBeforeUpdate);
    }

    /* INVENTORIES */

    @Test
    @Transactional
    public void findInventories() throws Exception {
        // Initialize the database
        saveAndFlush(lead);

        // Get the inventories of the lead
        restLeadMockMvc.perform(get("/api/leads/{id}/inventories", lead.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_INVENTORIES.get(0).getCategory().toString())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_INVENTORIES.get(0).getName())))
                .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_INVENTORIES.get(0).getQuantity())))
                .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_INVENTORIES.get(0).getWeight())))
                .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_INVENTORIES.get(0).getVolume())))


                .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_INVENTORIES.get(1).getCategory().toString())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_INVENTORIES.get(1).getName())))
                .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_INVENTORIES.get(1).getQuantity())))
                .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_INVENTORIES.get(1).getWeight())))
                .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_INVENTORIES.get(1).getVolume())))


                .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_INVENTORIES.get(2).getCategory().toString())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_INVENTORIES.get(2).getName())))
                .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_INVENTORIES.get(2).getQuantity())))
                .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_INVENTORIES.get(2).getWeight())))
                .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_INVENTORIES.get(2).getVolume())));

    }

    @Test
    @Transactional
    public void findNonExistingInventories() throws Exception {
        // Get the inventories of the lead
        restLeadMockMvc.perform(get("/api/leads/{id}/inventories", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateInventories() throws Exception {
        // Initialize the database
        saveAndFlush(lead);

        int databaseSizeBeforeUpdate = getLastLeadCountInventories();

        // Update the inventories of the lead
        Lead lead = leadRepository.findById(this.lead.getId()).get();

        lead.setInventories(new HashSet<>(UPDATED_INVENTORIES));

        restLeadMockMvc.perform(put("/api/leads/{id}/inventories", this.lead.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(lead.getInventories())))
                .andExpect(status().isOk());

        // Validate the Inventories in the database
        assertThat(getLastLeadCountInventories()).isEqualTo(databaseSizeBeforeUpdate);
        Iterable<Inventory> testInventories = getLastLead().getInventories();
        assertThat(testInventories).containsExactlyInAnyOrder((Inventory[]) UPDATED_INVENTORIES.toArray());
    }

    @Test
    @Transactional
    public void updateNotValidInventories() throws Exception {
        // Initialize the database
        saveAndFlush(lead);
        em.detach(lead);

        int databaseSizeBeforeUpdate = getLastLeadCountInventories();

        // Set the fields where params aren't valid
        lead.setInventories(new HashSet<>(asList(
                new Inventory(Category.ANY,"", 0, 0, 0),
                new Inventory(null, null, -1, -1, -1)
        )));


        // update inventories witch fails
        restLeadMockMvc.perform(put("/api/leads/{id}/inventories", lead.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(lead.getInventories())))
                .andExpect(status().isBadRequest());

        // Validate the Lead in the database
        assertThat(getLastLeadCountInventories()).isEqualTo(databaseSizeBeforeUpdate);
        Iterable<Inventory> testInventories = getLastLead().getInventories();
        assertThat(testInventories).containsExactlyInAnyOrder((Inventory[]) DEFAULT_INVENTORIES.toArray());
    }

    @Test
    @Transactional
    public void updateNonExistingInventories() throws Exception {
        int databaseSizeBeforeUpdate = getCount();

        // Non existing Lead
        lead.setId(Long.MAX_VALUE);

        // update with unexisting ID fails
        restLeadMockMvc.perform(put("/api/leads/{id}/inventories", lead.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(lead.getInventories())))
                .andExpect(status().isNotFound());

        assertThat(getCount()).isEqualTo(databaseSizeBeforeUpdate);
    }
}
