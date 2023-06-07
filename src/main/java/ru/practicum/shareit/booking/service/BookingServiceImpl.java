package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.exception.NoCorrectRequestException;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(Long userId, BookingRequest request) {
        User user = userService.findUserById(userId);

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NoFoundObjectException(String.format("Item with id='%s' not found",
                        request.getItemId())));

        if (!item.getAvailable()) {
            throw new NoCorrectRequestException("Item is not available for booking");
        }

        if (request.getEnd().isBefore(request.getStart()) ||
                request.getEnd().equals(request.getStart())) {
            throw new NoCorrectRequestException("Exception of start date or end date");
        }

        if (Objects.equals(item.getOwner().getId(), userId))
            throw new NoFoundObjectException("You cannot book your item");

        Booking booking = BookingMapper.dtoToObject(request);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.objectToDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponse updateStatusById(Long id, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NoFoundObjectException(String.format("Booking with id='%s' not found", id)));

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId))
            throw new NoFoundObjectException(String.format("User with id='%s' can not change status item with id='%s", userId, id));

        if (!booking.getStatus().equals(Status.WAITING))
            throw new NoCorrectRequestException("Booker status must be WAITING.");

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.objectToDto(savedBooking);
    }

    @Override
    public BookingResponse getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoFoundObjectException(String.format("Booking with id='%s' not found", bookingId)));

        if (!Objects.equals(booking.getBooker().getId(), userId) && !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NoFoundObjectException(String.format("Only booker or item owner can get booking with id='%s'", bookingId));
        }

        return BookingMapper.objectToDto(booking);
    }

    @Override
    public List<BookingResponse> getAllByBookerId(Long userId, String state) {
        userService.checkExistUserById(userId);

        State bookingState = State.from(state)
                .orElseThrow(() -> new NoCorrectRequestException("Unknown state: " + state));

        Sort sortBy = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookingList = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.findAllByBookerId(userId, sortBy);
                break;
            case PAST:
                bookingList =  bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sortBy);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sortBy);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), sortBy);
                break;
            case WAITING:
                bookingList =  bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING, sortBy);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED, sortBy);
        }

        return BookingMapper.objectToDto(bookingList);
    }

    @Override
    public List<BookingResponse> getAllByOwnerId(Long userId, String state) {
        userService.checkExistUserById(userId);

        State bookingState = State.from(state)
                .orElseThrow(() -> new NoCorrectRequestException("Unknown state: " + state));

        Sort sortBy = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> bookingList = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.findAllByItemOwnerId(userId, sortBy);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), sortBy);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(), sortBy);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), sortBy);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.WAITING, sortBy);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.REJECTED, sortBy);
        }

        return BookingMapper.objectToDto(bookingList);
    }

    @Override
    public List<Booking> getAllByItemId(Long id) {
        return bookingRepository.findAllByItemId(id);
    }

    @Override
    public List<Booking> getAllByItemAndEndBeforeDate(Long itemId, LocalDateTime created) {
        return bookingRepository.findByItemIdAndEndIsBefore(itemId, created);
    }

    @Override
    public List<Booking> getAllByItemIdIn(List<Long> itemsId) {
        return bookingRepository.findAllByItemIdIn(itemsId);
    }
}
