package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Sql("classpath:repository/data.sql")
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;

    @Test
    void findByBookerIdAndEndIsBefore_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 25, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Booking> result = bookingRepository.findByBookerIdAndEndIsBefore(3L, date, pageRequest);

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByBookerIdAndEndIsBefore_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 25, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Booking> result = bookingRepository.findByBookerIdAndEndIsBefore(1L, date, pageRequest);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findAllByBookerId_notEmptyResult_bookingExist() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository.findAllByBookerId(3L, pageRequest);

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findAllByBookerId_emptyResult_bookingDoesNotExist() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository.findAllByBookerId(1L, pageRequest);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByBookerIdAndStartIsBeforeAndEndIsAfter_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 15, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findByBookerIdAndStartIsBeforeAndEndIsAfter(3L, date, date, pageRequest);

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByBookerIdAndStartIsBeforeAndEndIsAfter_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 15, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findByBookerIdAndStartIsBeforeAndEndIsAfter(1L, date, date, pageRequest);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByBookerIdAndStartIsAfter_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 8, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findByBookerIdAndStartIsAfter(3L, date, pageRequest);

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByBookerIdAndStartIsAfter_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 8, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findByBookerIdAndStartIsAfter(1L, date, pageRequest);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findAllByItemIdIn_notEmptyResult_bookingExist() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findAllByItemIdIn(List.of(1L, 2L), pageRequest);

        Assertions.assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findAllByItemIdIn_emptyResult_bookingDoesNotExist() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findAllByItemIdIn(List.of(3L), pageRequest);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByItemIdInAndStartIsBeforeAndEndIsAfter_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 15, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findByItemIdInAndStartIsBeforeAndEndIsAfter(List.of(1L, 2L), date, date, pageRequest);

        Assertions.assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByItemIdInAndStartIsBeforeAndEndIsAfter_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 15, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findByItemIdInAndStartIsBeforeAndEndIsAfter(List.of(3L), date, date, pageRequest);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByItemIdInAndEndIsBefore_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 30, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findByItemIdInAndEndIsBefore(List.of(1L, 2L), date, pageRequest);

        Assertions.assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByItemIdInAndEndIsBefore_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 30, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findByItemIdInAndEndIsBefore(List.of(3L), date, pageRequest);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByItemIdInAndStartIsAfterAndStatusIs_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 1, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findByItemIdInAndStartIsAfterAndStatusIs(List.of(1L, 2L), date, Status.APPROVED, pageRequest);

        Assertions.assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByItemIdInAndStartIsAfterAndStatusIs_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 1, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findByItemIdInAndStartIsAfterAndStatusIs(List.of(3L), date, Status.WAITING, pageRequest);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByBookerIdAndStartIsAfterAndStatusIs_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 1, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findByBookerIdAndStartIsAfterAndStatusIs(3L, date, Status.APPROVED, pageRequest);

        Assertions.assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByBookerIdAndStartIsAfterAndStatusIsdStatusIs_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 1, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findByBookerIdAndStartIsAfterAndStatusIs(1L, date, Status.APPROVED, pageRequest);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByItemIdInAndStartIsAfter_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 1, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findByItemIdInAndStartIsAfter(List.of(1L), date, pageRequest);

        Assertions.assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByItemIdInAndStartIsAfter_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 30, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Booking> result = bookingRepository
                .findByItemIdInAndStartIsAfter(List.of(3L), date, pageRequest);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByItemIdAndEndIsBefore_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 30, 10, 13, 30);

        List<Booking> result = bookingRepository
                .findByItemIdAndEndIsBefore(1L, date);

        Assertions.assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getItem().getName()).isEqualTo("Book");
    }

    @Test
    void findByItemIdAndEndIsBefore_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 1, 10, 13, 30);

        List<Booking> result = bookingRepository
                .findByItemIdAndEndIsBefore(2L, date);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findLastBooking_notEmptyResult_bookingExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 30, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<BookingDto> result = bookingRepository
                .findLastBooking(1L, 1L, Status.APPROVED, date, pageRequest);

        assertThat(result).isNotEmpty();
        assertThat((int) result.get().count()).isEqualTo(1);
    }

    @Test
    void findLastBooking_emptyResult_bookingDoesNotExist() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 30, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<BookingDto> result = bookingRepository
                .findLastBooking(1L, 2L, Status.APPROVED, date, pageRequest);

        assertThat(result).isEmpty();
    }

    @Test
    void findNextBooking_notEmptyResult_ownerItemIsCorrect() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 1, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<BookingDto> result = bookingRepository
                .findNextBooking(1L, 1L, Status.APPROVED, date, pageRequest);

        assertThat(result).isNotEmpty();
        assertThat((int) result.get().count()).isEqualTo(1);
    }

    @Test
    void findNextBooking_emptyResult_ownerItemIsOther() {
        LocalDateTime date = LocalDateTime.of(2023, 6, 1, 10, 13, 30);
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<BookingDto> result = bookingRepository
                .findNextBooking(1L, 2L, Status.APPROVED, date, pageRequest);

        assertThat(result).isEmpty();
    }
}
