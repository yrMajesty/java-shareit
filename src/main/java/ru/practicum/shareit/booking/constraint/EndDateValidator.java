package ru.practicum.shareit.booking.constraint;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EndDateValidator implements ConstraintValidator<EndDateIsAfterStartDate, Object> {

    private String startFieldName;
    private String endFieldName;

    @Override
    public void initialize(EndDateIsAfterStartDate constraintAnnotation) {
        startFieldName = "start";
        endFieldName = "end";
    }

    public boolean isValid(Object object, ConstraintValidatorContext context) {

        final Object startDateObject = new BeanWrapperImpl(object)
                .getPropertyValue(startFieldName);
        final Object endDateObject = new BeanWrapperImpl(object)
                .getPropertyValue(endFieldName);

        if (startDateObject == null || endDateObject == null) {
            return true;
        }

        LocalDateTime startDateTime = LocalDateTime.parse(startDateObject.toString());
        LocalDateTime endDateTime = LocalDateTime.parse(endDateObject.toString());

        return endDateTime.isAfter(startDateTime);
    }

}