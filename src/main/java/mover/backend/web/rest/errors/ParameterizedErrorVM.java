package mover.backend.web.rest.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * View Model for sending a parameterized error message.
 */
@Data
@AllArgsConstructor
public class ParameterizedErrorVM {

    private final String message;
    private final Map<String, String> paramMap;

}
