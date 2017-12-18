package mover.backend.validator;

import mover.backend.annotation.DateRange;
import org.springframework.util.ReflectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    private String before;
    private String after;
    private String message;

    @Override
    public void initialize(DateRange annotation) {
        before = annotation.before();
        after = annotation.after();
        message = annotation.message();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isValid(Object validated, ConstraintValidatorContext context) {
        Class<?> clazz = validated.getClass();
        Comparable beforeValue = (Comparable) getFieldValue(clazz, validated, before);
        Comparable afterValue = (Comparable) getFieldValue(clazz, validated, after);
        if (beforeValue == null || afterValue == null) {
            return false;
        }
        if (beforeValue.compareTo(afterValue) >= 0) {
            addMessage(context, before, message);
            addMessage(context, after, message);
            return false;
        }
        return true;
    }

    private Object getFieldValue(Class<?> clazz, Object validated, String firstFieldName) {
        Field field = ReflectionUtils.findField(clazz, firstFieldName);
        field.setAccessible(true);
        return ReflectionUtils.getField(field, validated);
    }

    private void addMessage(ConstraintValidatorContext context, String fieldName, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(fieldName).addConstraintViolation();
    }

}