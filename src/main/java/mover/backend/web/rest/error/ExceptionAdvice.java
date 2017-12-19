package mover.backend.web.rest.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controller advice to handle exceptions.
 */
@RestControllerAdvice
public class ExceptionAdvice {

    private final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);

    @Autowired
    private javax.validation.Validator messageValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        System.out.println(messageValidator.getClass().getName());
        binder.setValidator(new SpringValidatorAdapter(messageValidator));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void processValidationError(Exception ex) {
        log.error("REST request validation error", ex);
    }
}