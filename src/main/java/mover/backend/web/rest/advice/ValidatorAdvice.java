package mover.backend.web.rest.advice;

import mover.backend.validator.CollectionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.util.Collection;

/**
 * Controller advice that adds the {@link CollectionValidator} to the
 * {@link WebDataBinder}.
 */
@ControllerAdvice
public class ValidatorAdvice {

    @Autowired
    protected LocalValidatorFactoryBean validator;

    /**
     * Adds the {@link CollectionValidator} to the supplied
     * {@link WebDataBinder} if {@link Collection} are used
     *
     * @param binder web data binder.
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        if(binder.getTarget() instanceof Collection) {
            binder.addValidators(new CollectionValidator(validator));
        }
    }
}
