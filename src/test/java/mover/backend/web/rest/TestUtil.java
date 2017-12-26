package mover.backend.web.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Utility class for testing REST controllers.
 */
public class TestUtil {

    /**
     * MediaType for JSON UTF8
     */
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    /**
     * Convert an object to JSON byte array.
     *
     * @param object the object to convert
     * @return the JSON byte array
     * @throws IOException
     */
    public static byte[] convertObjectToJsonBytes(Object object)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);

        return mapper.writeValueAsBytes(object);
    }

    /**
     * Create a byte array with a specific size filled with specified data.
     *
     * @param size the size of the byte array
     * @param data the data to put in the byte array
     * @return the JSON byte array
     */
    public static byte[] createByteArray(int size, String data) {
        byte[] byteArray = new byte[size];
        for (int i = 0; i < size; i++) {
            byteArray[i] = Byte.parseByte(data, 2);
        }
        return byteArray;
    }

    /**
     * A matcher that tests that the examined string represents the same instant as the reference datetime.
     */
    public static class LocalDateTimeMatcher extends TypeSafeDiagnosingMatcher<String> {

        private final LocalDateTime date;

        public LocalDateTimeMatcher(LocalDateTime date) {
            this.date = date;
        }

        @Override
        protected boolean matchesSafely(String item, Description mismatchDescription) {
            try {
                if (!date.isEqual(LocalDateTime.parse(item))) {
                    mismatchDescription.appendText("was ").appendValue(item);
                    return false;
                }
                return true;
            } catch (DateTimeParseException e) {
                mismatchDescription.appendText("was ").appendValue(item)
                        .appendText(", which could not be parsed as a LocalDateTime");
                return false;
            }

        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a String representing the same Instant as ").appendValue(date);
        }
    }

    /**
     * Creates a matcher that matches when the examined string reprensents the same instant as the reference datetime
     *
     * @param date the reference datetime against which the examined string is checked
     */
    public static LocalDateTimeMatcher sameInstant(LocalDateTime date) {
        return new LocalDateTimeMatcher(date);
    }

    /**
     * Verifies the equals/hashcode contract on the domain object.
     *
     * @param diffObject  doesn't equal to another params.
     * @param sameObject1, sameObject2, sameObject3 are equal between each other.
     * Requires 3 same objects for checking reflexive, symmetric, transitive relations.
     */
    @SuppressWarnings("unchecked")
    public static void equalsAndHashcodeVerifier(Object diffObject,
            Object sameObject1, Object sameObject2, Object sameObject3) throws Exception {
        verifyEquals(diffObject, sameObject1, sameObject2, sameObject3);
        verifyHashcode(diffObject, sameObject1, sameObject2, sameObject3);
    }

    private static void verifyEquals(Object diffObject, Object sameObject1, Object sameObject2, Object sameObject3) {
        // reflective
        assertThat(sameObject1).isEqualTo(sameObject1);
        // symmetric
        assertThat(sameObject1).isEqualTo(sameObject2);
        assertThat(sameObject2).isEqualTo(sameObject1);
        // transitive
        assertThat(sameObject1).isEqualTo(sameObject2);
        assertThat(sameObject2).isEqualTo(sameObject3);

        assertThat(sameObject1).isNotEqualTo(null);
        assertThat(diffObject).isNotEqualTo(sameObject1);
    }

    private static void verifyHashcode(Object diffObject, Object sameObject1, Object sameObject2, Object sameObject3) {
        // reflective
        assertThat(sameObject1.hashCode()).isEqualTo(sameObject1.hashCode());
        // symmetric
        assertThat(sameObject1.hashCode()).isEqualTo(sameObject2.hashCode());
        assertThat(sameObject2.hashCode()).isEqualTo(sameObject1.hashCode());
        // transitive
        assertThat(sameObject1.hashCode()).isEqualTo(sameObject2.hashCode());
        assertThat(sameObject2.hashCode()).isEqualTo(sameObject3.hashCode());

        assertThat(diffObject.hashCode()).isNotEqualTo(sameObject1.hashCode());
    }
}
