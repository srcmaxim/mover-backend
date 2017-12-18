package mover.backend.model;

import mover.backend.model.enumeration.Status;
import mover.backend.model.enumeration.Type;
import mover.backend.web.rest.TestUtil;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class LeadTest {

    private static final ZonedDateTime START = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime END = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime AFTER_END = ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.MAX_VALUE), ZoneOffset.UTC);
    private static final Type TYPE = Type.LOCAL;
    private static final Status STATUS = Status.PENDING;
    private static final Address ADDRESS = new Address();

    private static Validator validator;

    private Lead lead;

    @BeforeClass
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lead createEntity() {
        Lead lead = new Lead()
                .setType(TYPE)
                .setStart(START)
                .setEnd(END)
                .setStatus(STATUS)
                .setOrigin(ADDRESS)
                .setDestination(ADDRESS);
        return lead;
    }

    @Before
    public void initTest() {
        lead = createEntity();
    }


    @Test
    public void shouldValidateLead() {
        Set<ConstraintViolation<Lead>> validate = validator.validate(lead);

        assertThat(validate).hasSize(0);
    }

    @Test
    public void shouldNotValidateLeadIfFieldsAreNull() {
        Set<ConstraintViolation<Lead>> validate = validator.validate(new Lead());

        MatcherAssert.assertThat(validate.size(), greaterThanOrEqualTo(6));
    }

    @Test
    public void shouldNotValidateLeadIfDateRangeIsNotValid() {
        lead.setStart(AFTER_END);

        Set<ConstraintViolation<Lead>> validate = validator.validate(lead);

        assertThat(validate).hasSize(2);
    }

    @Test
    public void shouldNotValidateLeadIfDatesAreEqual() {
        lead.setEnd(START);

        Set<ConstraintViolation<Lead>> validate = validator.validate(lead);

        assertThat(validate).hasSize(2);
    }

    @Test
    @Transactional
    public void equalsHashcodeVerifier() throws Exception {
        TestUtil.equalsAndHashcodeVerifier(
                lead, newLead(), newLead(), newLead());
    }

    private Lead newLead() {
        return new Lead()
                .setStart(START).setEnd(AFTER_END)
                .setType(Type.DISTANCE).setStatus(Status.ASSIGNED)
                .setOrigin(new Address()).setDestination(new Address());
    }
}