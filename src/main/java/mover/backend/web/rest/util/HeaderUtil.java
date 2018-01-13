package mover.backend.web.rest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

    private static final Logger log = LoggerFactory.getLogger(HeaderUtil.class);

    private static final String APPLICATION_NAME = "moverApp";

    private HeaderUtil() {
    }

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-moverApp-alert", message);
        headers.add("X-moverApp-params", param);
        return headers;
    }

    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert(APPLICATION_NAME + "." + entityName + ".created", param);
    }

    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert(APPLICATION_NAME + "." + entityName + ".updated", param);
    }

    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert(APPLICATION_NAME + "." + entityName + ".deleted", param);
    }

    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
        log.error("Entity creation failed, {}", defaultMessage);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-moverApp-error", "error." + errorKey);
        headers.add("X-moverApp-params", entityName);
        return headers;
    }

    public static HttpHeaders createConnectionEntityAlert(String entityName, String param) {
        return createAlert(APPLICATION_NAME + "." + entityName + ".createdConnection", param);
    }

    public static HttpHeaders deleteConnectionEntityAlert(String entityName, String param) {
        return createAlert(APPLICATION_NAME + "." + entityName + ".deletedConnection", param);
    }
}

