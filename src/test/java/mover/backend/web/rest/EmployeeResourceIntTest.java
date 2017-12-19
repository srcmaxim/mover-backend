package mover.backend.web.rest;

import mover.backend.BackendApplication;
import mover.backend.model.Employee;
import mover.backend.repository.EmployeeRepository;
import mover.backend.web.rest.error.ExceptionAdvice;
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
 * Test class for the EmployeeResource REST controller.
 *
 * @see EmployeeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BackendApplication.class)
public class EmployeeResourceIntTest {

    private static final String DEFAULT_FIRST_NAME = "Defaultfirstname";
    private static final String UPDATED_FIRST_NAME = "Updatedfirstname";

    private static final String DEFAULT_LAST_NAME = "Defaultlastname";
    private static final String UPDATED_LAST_NAME = "Updatedlastname";

    private static final String DEFAULT_PHONE = "+111-111-1111";
    private static final String UPDATED_PHONE = "+222-222-2222";

    private static final String DEFAULT_EMAIL = "default-email@gmail.com";
    private static final String UPDATED_EMAIL = "updated-email@gmail.com";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private ExceptionAdvice exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restEmployeeMockMvc;

    private Employee employee;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EmployeeResource employeeResource = new EmployeeResource(employeeRepository);
        this.restEmployeeMockMvc = MockMvcBuilders.standaloneSetup(employeeResource)
                .setControllerAdvice(exceptionTranslator)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createEntity() {
        Employee employee = (Employee) new Employee()
                .setFirstName(DEFAULT_FIRST_NAME)
                .setLastName(DEFAULT_LAST_NAME)
                .setPhone(DEFAULT_PHONE)
                .setEmail(DEFAULT_EMAIL);
        return employee;
    }

    public Employee getLastEmployee() {
        return (Employee) em.createQuery("select l from Employee l order by l.id desc")
                .setMaxResults(1).getSingleResult();
    }

    public int getCount() {
        return (int) employeeRepository.count();
    }

    public void saveAndFlush(Employee employee) {
        em.persist(employee);
        em.flush();
    }

    @Before
    public void initTest() {
        employee = createEntity();
    }

    @Test
    @Transactional
    public void queryEmployees() throws Exception {
        // Initialize the database
        saveAndFlush(employee);

        // Get all the employeeList
        restEmployeeMockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(employee.getId().intValue())))
                .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
                .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
                .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));
    }

    @Test
    @Transactional
    public void createEmployee() throws Exception {
        int databaseSizeBeforeCreate = getCount();

        // Create the Employee
        restEmployeeMockMvc.perform(post("/api/employees")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(employee)))
                .andExpect(status().isCreated());

        // Validate the Employee in the database
        assertThat(getCount()).isEqualTo(databaseSizeBeforeCreate + 1);
        Employee testEmployee = getLastEmployee();
        assertThat(testEmployee.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testEmployee.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testEmployee.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testEmployee.getPhone()).isEqualTo(DEFAULT_PHONE);
    }

    @Test
    @Transactional
    public void createEmployeeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = getCount();

        // Create the Employee with an existing ID
        employee.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmployeeMockMvc.perform(post("/api/employees")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(employee)))
                .andExpect(status().isBadRequest());

        assertThat(getCount()).isEqualTo(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void createNotValidEmployee() throws Exception {
        int databaseSizeBeforeCreate = getCount();

        // Set the field where employee email is wrong
        employee
                .setEmail("WRONG EMAIL");

        // Create the employee, which fails.
        restEmployeeMockMvc.perform(post("/api/employees")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(employee)))
                .andExpect(status().isBadRequest());

        assertThat(getCount()).isEqualTo(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void updateEmployee() throws Exception {
        // Initialize the database
        saveAndFlush(employee);

        int databaseSizeBeforeUpdate = getCount();

        // Update the employee
        Employee updatedEmployee = employeeRepository.findById(employee.getId()).get();
        updatedEmployee
                .setFirstName(UPDATED_FIRST_NAME)
                .setLastName(UPDATED_LAST_NAME)
                .setEmail(UPDATED_EMAIL)
                .setPhone(UPDATED_PHONE);

        restEmployeeMockMvc.perform(put("/api/employees")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedEmployee)))
                .andExpect(status().isOk());

        // Validate the Employee in the database
        assertThat(getCount()).isEqualTo(databaseSizeBeforeUpdate);
        Employee testEmployee = getLastEmployee();
        assertThat(testEmployee.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testEmployee.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testEmployee.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testEmployee.getPhone()).isEqualTo(UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void updateNotValidEmployee() throws Exception {
        // Initialize the database
        saveAndFlush(employee);
        em.detach(employee);

        int databaseSizeBeforeUpdate = getCount();

        // Set the field where employee email is wrong
        employee
                .setEmail("WRONG EMAIL");

        // update employee witch fails
        restEmployeeMockMvc.perform(put("/api/employees")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(employee)))
                .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        assertThat(getCount()).isEqualTo(databaseSizeBeforeUpdate);
        Employee testEmployee = getLastEmployee();
        assertThat(testEmployee.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    public void updateNonExistingEmployee() throws Exception {
        int databaseSizeBeforeUpdate = getCount();

        // Non existing Employee
        employee.setId(Long.MAX_VALUE);

        // update with unexisting ID fails
        restEmployeeMockMvc.perform(put("/api/employees")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(employee)))
                .andExpect(status().isNotFound());

        assertThat(getCount()).isEqualTo(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void findEmployee() throws Exception {
        // Initialize the database
        saveAndFlush(employee);

        // Get the employee
        restEmployeeMockMvc.perform(get("/api/employees/{id}", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(employee.getId().intValue()))
                .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
                .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
                .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE));
    }

    @Test
    @Transactional
    public void findNonExistingEmployee() throws Exception {
        // Get the employee
        restEmployeeMockMvc.perform(get("/api/employees/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void deleteEmployee() throws Exception {
        // Initialize the database
        saveAndFlush(employee);

        int databaseSizeBeforeDelete = getCount();

        // Get the employee
        restEmployeeMockMvc.perform(delete("/api/employees/{id}", employee.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        assertThat(getCount()).isEqualTo(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void deleteNonExistingEmployee() throws Exception {
        int databaseSizeBeforeDelete = getCount();

        // Get the employee
        restEmployeeMockMvc.perform(delete("/api/employees/{id}", Long.MAX_VALUE)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        assertThat(getCount()).isEqualTo(databaseSizeBeforeDelete);
    }
}