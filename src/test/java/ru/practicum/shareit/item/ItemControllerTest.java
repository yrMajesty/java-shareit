package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemControllerTest {

    Validator validator;

    @BeforeEach
    void prepare() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void createItem_correctSizeValidationList_completelyIncorrectItem() {
        ItemDto test = new ItemDto();

        assertEquals(3, validator.validate(test).size());
    }

    @Test
    void createItem_notValidName_nameIsEmpty() {
        ItemDto test = ItemDto.builder().name("").description("description").available(true).build();

        List<ConstraintViolation<ItemDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Name cannot be empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createItem_notValidName_nameIsNull() {
        ItemDto test = ItemDto.builder().name(null).description("description").available(true).build();

        List<ConstraintViolation<ItemDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Name cannot be empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createItem_notValidName_descriptionIsEmpty() {
        ItemDto test = ItemDto.builder().name("name").description("").available(true).build();

        List<ConstraintViolation<ItemDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Description cannot be empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createItem_notValidName_descriptionIsNull() {
        ItemDto test = ItemDto.builder().name("name").description(null).available(true).build();

        List<ConstraintViolation<ItemDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Description cannot be empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createItem_notValidName_availableIsNull() {
        ItemDto test = ItemDto.builder().name("name").description("description").available(null).build();

        List<ConstraintViolation<ItemDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Available cannot be null", validationSet.get(0).getMessage())
        );
    }
}