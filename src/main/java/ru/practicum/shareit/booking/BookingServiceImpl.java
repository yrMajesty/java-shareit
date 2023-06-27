package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NoCorrectRequestException;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.exception.NoValidArgumentException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(Long userId, BookingRequestDto request) {
        User user = userService.findUserById(userId);

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NoFoundObjectException(String.format("Item with id='%s' not found",
                        request.getItemId())));

        if (!item.getAvailable()) {
            throw new NoCorrectRequestException("Item is not available for booking");
        }

        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NoFoundObjectException("You cannot book your item");
        }

        Booking booking = BookingMapper.dtoToObject(request);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.objectToDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto updateStatusById(Long id, Boolean approved, Long userId) {
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
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoFoundObjectException(
                        String.format("Booking with id='%s' not found", bookingId)));

        userService.checkExistUserById(userId);
        UserResponseDto booker = userService.getUserById(booking.getBooker().getId());

        if (!Objects.equals(booking.getBooker().getId(), userId)
                && !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NoFoundObjectException(String.format("Only booker or item owner can get booking with id='%s'",
                    bookingId));
        }
        booking.setBooker(UserMapper.dtoToObject(booker));

        return BookingMapper.objectToDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllByBookerId(Long userId, String state, Integer from, Integer size) {
        userService.checkExistUserById(userId);

        Pageable pageable = getPageable(from, size);

        State bookingState = State.from(state)
                .orElseThrow(() -> new NoCorrectRequestException("Unknown state: " + state));

        LocalDateTime dateTimeNow = LocalDateTime.now();

        switch (bookingState) {
            case CURRENT:
                return BookingMapper.objectToDto(bookingRepository
                        .findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, dateTimeNow, dateTimeNow, pageable));
            case PAST:
                return BookingMapper.objectToDto(bookingRepository
                        .findByBookerIdAndEndIsBefore(userId, dateTimeNow, pageable));
            case FUTURE:
                return BookingMapper.objectToDto(bookingRepository
                        .findByBookerIdAndStartIsAfter(userId, dateTimeNow, pageable));
            case WAITING:
                return BookingMapper.objectToDto(bookingRepository
                        .findByBookerIdAndStartIsAfterAndStatusIs(userId, dateTimeNow, Status.WAITING, pageable));
            case REJECTED:
                return BookingMapper.objectToDto(bookingRepository
                        .findByBookerIdAndStartIsAfterAndStatusIs(userId, dateTimeNow, Status.REJECTED, pageable));
            default:
                return BookingMapper.objectToDto(bookingRepository.findAllByBookerId(userId, pageable));
        }
    }

    @Override
    public List<BookingResponseDto> getAllByOwnerId(Long userId, String state, Integer from, Integer size) {
        User user = userService.findUserById(userId);

        Pageable pageable = getPageable(from, size);

        State bookingState = State.from(state)
                .orElseThrow(() -> new NoCorrectRequestException("Unknown state: " + state));

        List<Long> itemIdList = itemRepository.findAllByOwnerId(user.getId())
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        LocalDateTime dateTimeNow = LocalDateTime.now();

        switch (bookingState) {
            case CURRENT:
                return BookingMapper.objectToDto(bookingRepository
                        .findByItemIdInAndStartIsBeforeAndEndIsAfter(itemIdList, dateTimeNow, dateTimeNow, pageable));
            case PAST:
                return BookingMapper.objectToDto(bookingRepository
                        .findByItemIdInAndEndIsBefore(itemIdList, dateTimeNow, pageable));
            case FUTURE:
                return BookingMapper.objectToDto(bookingRepository
                        .findByItemIdInAndStartIsAfter(itemIdList, dateTimeNow, pageable));
            case WAITING:
                return BookingMapper.objectToDto(bookingRepository
                        .findByItemIdInAndStartIsAfterAndStatusIs(itemIdList, dateTimeNow, Status.WAITING, pageable));
            case REJECTED:
                return BookingMapper.objectToDto(bookingRepository
                        .findByItemIdInAndStartIsAfterAndStatusIs(itemIdList, dateTimeNow, Status.REJECTED, pageable));
            default:
                return BookingMapper.objectToDto(bookingRepository.findAllByItemIdIn(itemIdList, pageable));
        }
    }

    private Pageable getPageable(Integer from, Integer size) {
        validatePageableParameters(from, size);

        int page = from == 0 ? 0 : (from / size);
        return PageRequest.of(page, size, Sort.by("start").descending());
    }

    private void validatePageableParameters(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new NoValidArgumentException("The request parameters from b size are invalid and cannot be negative");
        }
    }
}
