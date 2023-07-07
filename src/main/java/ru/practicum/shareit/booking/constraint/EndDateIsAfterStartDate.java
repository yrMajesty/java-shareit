package ru.practicum.shareit.booking.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EndDateValidator.class)
public @interface EndDateIsAfterStartDate {
    String message() default "End date should be after start date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
