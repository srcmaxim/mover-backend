package mover.backend.web.rest;

import mover.backend.BackendApplication;
import mover.backend.model.Customer;
import mover.backend.repository.CustomerRepository;
import mover.backend.web.rest.advice.ExceptionAdvice;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CustomerResource REST controller.
 *
 * @see CustomerResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BackendApplication.class)
public class CustomerResourceIntTest {

    private static final String DEFAULT_FIRST_NAME = "Defaultfirstname";
    private static final String UPDATED_FIRST_NAME = "Updatedfirstname";

    private static final String DEFAULT_LAST_NAME = "Defaultlastname";
    private static final String UPDATED_LAST_NAME = "Updatedlastname";

    private static final String DEFAULT_PHONE = "+111-111-1111";
    private static final String UPDATED_PHONE = "+222-222-2222";

    private static final String DEFAULT_EMAIL = "default-email@gmail.com";
    private static final String UPDATED_EMAIL = "updated-email@gmail.com";

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private ExceptionAdvice exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restCustomerMockMvc;

    private Customer customer;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CustomerResource customerResource = new CustomerResource(customerRepository);
        this.restCustomerMockMvc = MockMvcBuilders.standaloneSetup(customerResource)
                .setControllerAdvice(exceptionTranslator)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Customer createEntity() {
        Customer customer = (Customer) new Customer()
                .setFirstName(DEFAULT_FIRST_NAME)
                .setLastName(DEFAULT_LAST_NAME)
                .setPhone(DEFAULT_PHONE)
                .setEmail(DEFAULT_EMAIL);
        return customer;
    }

    public Customer getLastCustomer() {
        return (Customer) em.createQuery("select l from Customer l order by l.id desc")
                .setMaxResults(1).getSingleResult();
    }

    public int getCount() {
        return (int) customerRepository.count();
    }

    public void saveAndFlush(Customer customer) {
        em.persist(customer);
        em.flush();
    }

    @Before
    public void initTest() {
        customer = createEntity();
    }

    @Test
    @Transactional
    public void queryCustomers() throws Exception {
        // Initialize the database
        saveAndFlush(customer);

        // Get all the customerList
        restCustomerMockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(customer.getId().intValue())))
                .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
                .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
                .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));
    }

    @Test
    @Transactional
    public void createCustomer() throws Exception {
        int databaseSizeBeforeCreate = getCount();

        // Create the Customer
        restCustomerMockMvc.perform(post("/api/customers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customer)))
                .andExpect(status().isCreated());

        // Validate the Customer in the database
        assertThat(getCount()).isEqualTo(databaseSizeBeforeCreate + 1);
        Customer testCustomer = getLastCustomer();
        assertThat(testCustomer.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testCustomer.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testCustomer.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCustomer.getPhone()).isEqualTo(DEFAULT_PHONE);
    }

    @Test
    @Transactional
    public void createCustomerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = getCount();

        // Create the Customer with an existing ID
        customer.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomerMockMvc.perform(post("/api/customers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customer)))
                .andExpect(status().isBadRequest());

        assertThat(getCount()).isEqualTo(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void createNotValidCustomer() throws Exception {
        int databaseSizeBeforeCreate = getCount();

        // Set the field where customer email is wrong
        customer
                .setEmail("WRONG EMAIL");

        // Create the customer, which fails.
        restCustomerMockMvc.perform(post("/api/customers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customer)))
                .andExpect(status().isBadRequest());

        assertThat(getCount()).isEqualTo(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void updateCustomer() throws Exception {
        // Initialize the database
        saveAndFlush(customer);

        int databaseSizeBeforeUpdate = getCount();

        // Update the customer
        Customer updatedCustomer = customerRepository.findById(customer.getId()).get();
        updatedCustomer
                .setFirstName(UPDATED_FIRST_NAME)
                .setLastName(UPDATED_LAST_NAME)
                .setEmail(UPDATED_EMAIL)
                .setPhone(UPDATED_PHONE);

        restCustomerMockMvc.perform(put("/api/customers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCustomer)))
                .andExpect(status().isOk());

        // Validate the Customer in the database
        assertThat(getCount()).isEqualTo(databaseSizeBeforeUpdate);
        Customer testCustomer = getLastCustomer();
        assertThat(testCustomer.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testCustomer.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testCustomer.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCustomer.getPhone()).isEqualTo(UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void updateNotValidCustomer() throws Exception {
        // Initialize the database
        saveAndFlush(customer);
        em.detach(customer);

        int databaseSizeBeforeUpdate = getCount();

        // Set the field where customer email is wrong
        customer
                .setEmail("WRONG EMAIL");

        // update customer witch fails
        restCustomerMockMvc.perform(put("/api/customers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customer)))
                .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        assertThat(getCount()).isEqualTo(databaseSizeBeforeUpdate);
        Customer testCustomer = getLastCustomer();
        assertThat(testCustomer.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    public void updateNonExistingCustomer() throws Exception {
        int databaseSizeBeforeUpdate = getCount();

        // Non existing Customer
        customer.setId(Long.MAX_VALUE);

        // update with unexisting ID fails
        restCustomerMockMvc.perform(put("/api/customers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customer)))
                .andExpect(status().isNotFound());

        assertThat(getCount()).isEqualTo(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void findCustomer() throws Exception {
        // Initialize the database
        saveAndFlush(customer);

        // Get the customer
        restCustomerMockMvc.perform(get("/api/customers/{id}", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(customer.getId().intValue()))
                .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
                .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
                .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE));
    }

    @Test
    @Transactional
    public void findNonExistingCustomer() throws Exception {
        // Get the customer
        restCustomerMockMvc.perform(get("/api/customers/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void deleteCustomer() throws Exception {
        // Initialize the database
        saveAndFlush(customer);

        int databaseSizeBeforeDelete = getCount();

        // Get the customer
        restCustomerMockMvc.perform(delete("/api/customers/{id}", customer.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        assertThat(getCount()).isEqualTo(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void deleteNonExistingCustomer() throws Exception {
        int databaseSizeBeforeDelete = getCount();

        // Get the customer
        restCustomerMockMvc.perform(delete("/api/customers/{id}", Long.MAX_VALUE)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        assertThat(getCount()).isEqualTo(databaseSizeBeforeDelete);
    }
}
