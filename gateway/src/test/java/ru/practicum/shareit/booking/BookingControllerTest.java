package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BookingControllerTest {
    @Nested
    class ValidationBookingTest {
        Validator validator;

        @BeforeEach
        void prepare() {
            try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
                validator = validatorFactory.getValidator();
            }
        }

        @Test
        void validationBooking_correctSizeValidationList_completelyIncorrectItem() {
            BookingRequestDto test = new BookingRequestDto();

            assertEquals(3, validator.validate(test).size());
        }

        @Test
        void validationBooking_correctSizeValidationList_itemIdIsNull() {
            BookingRequestDto test = BookingRequestDto.builder()
                    .itemId(null)
                    .start(LocalDateTime.now().plusDays(1))
                    .end(LocalDateTime.now().plusDays(2))
                    .build();

            List<ConstraintViolation<BookingRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("ItemId cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        void validationBooking_correctSizeValidationList_startTimeInThePast() {
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
        void validationBooking_correctSizeValidationList_startTimeIsNull() {
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
        void validationBooking_correctSizeValidationList_endTimeIsNull() {
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
        void validationBooking_correctSizeValidationList_endEarlyStart() {
            BookingRequestDto test = BookingRequestDto.builder()
                    .itemId(1L)
                    .start(LocalDateTime.now().plusDays(2))
                    .end(LocalDateTime.now().minusDays(3))
                    .build();

            List<ConstraintViolation<BookingRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(2, validationSet.size()),
                    () -> assertTrue(validationSet.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.toList())
                            .contains("End time can be only in future")),
                    () -> assertTrue(validationSet.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.toList())
                            .contains("End date should be after start date"))
            );
        }
    }

}