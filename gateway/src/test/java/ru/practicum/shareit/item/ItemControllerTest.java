package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemControllerTest {
    Validator validator;

    @BeforeEach
    void prepare() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void validationItem_correctSizeValidationList_completelyIncorrectItem() {
        ItemRequestDto test = new ItemRequestDto();

        assertEquals(3, validator.validate(test).size());
    }

    @Test
    void validationItem_notValidName_nameIsEmpty() {
        ItemRequestDto test = ItemRequestDto.builder().name("").description("description").available(true).build();

        List<ConstraintViolation<ItemRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Name cannot be empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void validationItem_notValidName_nameIsNull() {
        ItemRequestDto test = ItemRequestDto.builder().name(null).description("description").available(true).build();

        List<ConstraintViolation<ItemRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Name cannot be empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void validationItem_notValidName_descriptionIsEmpty() {
        ItemRequestDto test = ItemRequestDto.builder().name("name").description("").available(true).build();

        List<ConstraintViolation<ItemRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Description cannot be empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void validationItem_notValidName_descriptionIsNull() {
        ItemRequestDto test = ItemRequestDto.builder().name("name").description(null).available(true).build();

        List<ConstraintViolation<ItemRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Description cannot be empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void validationItem_notValidName_availableIsNull() {
        ItemRequestDto test = ItemRequestDto.builder().name("name").description("description").available(null).build();

        List<ConstraintViolation<ItemRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Available cannot be null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void validationItem_notValidTest_textCommentIsNull() {
        CommentRequestDto test = CommentRequestDto.builder().text(null).build();

        List<ConstraintViolation<CommentRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Text cannot be null or empty", validationSet.get(0).getMessage())
        );
    }
}