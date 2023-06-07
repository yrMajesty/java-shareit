package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

    List<Booking> findAllByItemId(Long id);

    List<Booking> findAllByItemIdIn(List<Long> itemsId);
}