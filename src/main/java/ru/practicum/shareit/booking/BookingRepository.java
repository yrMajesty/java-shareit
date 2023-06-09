package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long bookerId, Sort start);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort by);

    List<Booking> findAllByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort start);

    List<Booking> findAllByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort by);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, Status status, Sort start);

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort start);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort by);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort start);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort by);

    List<Booking> findAllByItemOwnerIdAndStatus(Long bookerId, Status status, Sort start);

    List<Booking> findByItemIdAndEndIsBefore(Long itemId, LocalDateTime date);

    @Query(value = "select new ru.practicum.shareit.booking.dto.BookingDto(b.id, b.start, b.end, b.booker.id) " +
            "from Booking as b " +
            "where b.item.id=?1 and b.item.owner.id=?2 and b.status=?3 and b.start<?4 " +
            "order by b.start desc ")
    Page<BookingDto> findLastBooking(Long itemId, Long ownerId, Status status, LocalDateTime now, Pageable pageable);

    @Query(value = "select new ru.practicum.shareit.booking.dto.BookingDto(b.id, b.start, b.end, b.booker.id) " +
            "from Booking as b " +
            "where b.item.id=?1 and b.item.owner.id=?2 and b.status=?3 and b.start>?4 " +
            "order by b.start asc")
    Page<BookingDto> findNextBooking(Long itemId, Long ownerId, Status status, LocalDateTime now, Pageable pageable);
}