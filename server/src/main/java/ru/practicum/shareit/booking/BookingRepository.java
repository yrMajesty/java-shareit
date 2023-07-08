package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end,
                                                              Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemIdIn(List<Long> itemId, Pageable pageable);

    List<Booking> findByItemIdInAndStartIsBeforeAndEndIsAfter(List<Long> itemId, LocalDateTime start, LocalDateTime end,
                                                              Pageable pageable);

    List<Booking> findByItemIdInAndEndIsBefore(List<Long> itemId, LocalDateTime date, Pageable pageable);

    List<Booking> findByItemIdInAndStartIsAfterAndStatusIs(List<Long> itemId, LocalDateTime date, Status bookingStatus,
                                                           Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfterAndStatusIs(Long userId, LocalDateTime date, Status bookingStatus,
                                                           Pageable pageable);

    List<Booking> findByItemIdInAndStartIsAfter(List<Long> itemIdList, LocalDateTime date, Pageable pageable);

    List<Booking> findByItemIdAndEndIsBefore(Long itemId, LocalDateTime date);

    @Query(value = "select new ru.practicum.shareit.booking.dto.BookingDto(b.id, b.start, b.end, b.booker.id) " +
            "from Booking as b " +
            "where b.item.id=?1 and b.item.owner.id=?2 and b.status=?3 and b.start<?4 " +
            "order by b.start desc ")
    Page<BookingDto> findLastBooking(Long itemId, Long ownerId, Status status, LocalDateTime date, Pageable pageable);

    @Query(value = "select new ru.practicum.shareit.booking.dto.BookingDto(b.id, b.start, b.end, b.booker.id) " +
            "from Booking as b " +
            "where b.item.id=?1 and b.item.owner.id=?2 and b.status=?3 and b.start>?4 " +
            "order by b.start asc")
    Page<BookingDto> findNextBooking(Long itemId, Long ownerId, Status status, LocalDateTime date, Pageable pageable);
}