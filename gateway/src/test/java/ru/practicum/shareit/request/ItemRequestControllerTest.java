package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestControllerTest {

    Validator validator;

    @BeforeEach
    void prepare() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void validationRequest_correctSizeValidationList_completelyIncorrectRequest() {
        RequestDto test = new RequestDto();

        assertEquals(1, validator.validate(test).size());
    }

    @Test
    void validationRequest_notValidName_descriptionIsEmpty() {
        RequestDto test = RequestDto.builder().description("").build();

        List<ConstraintViolation<RequestDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Description cannot be empty or null", validationSet.get(0).getMessage())
        );
    }
}