package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingControllerTest {

    Validator validator;

    @BeforeEach
    void prepare() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void validationBooking_correctSizeValidationList_completelyIncorrectItemg() {
        BookingRequestDto test = new BookingRequestDto();

        assertEquals(3, validator.validate(test).size());
    }

    @Test
    void validationItem_notValidId_nameIsEmpty() {
        BookingRequestDto test = BookingRequestDto.builder()
                .itemId(null)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        List<ConstraintViolation<BookingRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("ItemId cannot be empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void validationItem_notValidStartTimeInThePast_nameIsEmpty() {
        BookingRequestDto test = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        List<ConstraintViolation<BookingRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Start time can be only in present or future", validationSet.get(0).getMessage())
        );
    }

    @Test
    void validationItem_notValidStartTimeIsNull_nameIsEmpty() {
        BookingRequestDto test = BookingRequestDto.builder()
                .itemId(1L)
                .start(null)
                .end(LocalDateTime.now().plusDays(1))
                .build();

        List<ConstraintViolation<BookingRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Start date cannot be empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void validationItem_notValidEndTimeIsNull_nameIsEmpty() {
        BookingRequestDto test = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(null)
                .build();

        List<ConstraintViolation<BookingRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("End date cannot be empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void validationItem_notValidEndTimeInThePast_nameIsEmpty() {
        BookingRequestDto test = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        List<ConstraintViolation<BookingRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("End time can be only in future", validationSet.get(0).getMessage())
        );
    }

}