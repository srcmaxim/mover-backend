package mover.backend.web.rest.advice;

import mover.backend.web.rest.errors.CustomParameterizedException;
import mover.backend.web.rest.errors.ErrorConstants;
import mover.backend.web.rest.errors.ErrorVM;
import mover.backend.web.rest.errors.ParameterizedErrorVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
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

    @ExceptionHandler(ConcurrencyFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorVM processConcurrencyError(ConcurrencyFailureException ex) {
        return new ErrorVM(ErrorConstants.ERR_CONCURRENCY_FAILURE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorVM processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        ErrorVM dto = new ErrorVM(ErrorConstants.ERR_VALIDATION);
        for (FieldError fieldError : fieldErrors) {
            dto.add(fieldError.getObjectName(), fieldError.getField(), fieldError.getCode());
        }
        return dto;
    }

    @ExceptionHandler(CustomParameterizedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ParameterizedErrorVM processParameterizedValidationError(CustomParameterizedException ex) {
        return ex.getErrorVM();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorVM processAccessDeniedException(AccessDeniedException e) {
        return new ErrorVM(ErrorConstants.ERR_ACCESS_DENIED, e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorVM processMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        return new ErrorVM(ErrorConstants.ERR_METHOD_NOT_SUPPORTED, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorVM> processRuntimeException(Exception ex) {
        ResponseEntity.BodyBuilder builder;
        ErrorVM errorVM;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            builder = ResponseEntity.status(responseStatus.value());
            errorVM = new ErrorVM("error." + responseStatus.value().value(), responseStatus.reason());
        } else {
            builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
            errorVM = new ErrorVM(ErrorConstants.ERR_INTERNAL_SERVER_ERROR, "Internal server error");
        }
        return builder.body(errorVM);
    }
}