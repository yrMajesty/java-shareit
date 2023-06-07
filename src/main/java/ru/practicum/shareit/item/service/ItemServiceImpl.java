package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NoCorrectRequestException;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private final CommentService commentService;

    @Override
    @Transactional
    public ItemResponse createItem(ItemRequest request, Long userId) {
        User user = userService.findUserById(userId);

        Item item = ItemMapper.dtoToObject(request);
        item.setOwner(user);

        Item savedItem = itemRepository.save(item);
        return ItemMapper.objectToDto(savedItem);
    }

    @Override
    public ItemResponse getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoFoundObjectException(String.format("Item with id='%s' not found", itemId)));

        ItemResponse itemResponse = ItemMapper.objectToDto(item);

        if (Objects.equals(userId, item.getOwner().getId())) {
            List<Booking> bookingList = bookingService.getAllByItemId(itemId);
            setLastAndNextBookings(bookingList, itemResponse);
        }

        List<CommentResponse> comments = commentService.findAllByItemId(itemId);
        itemResponse.setComments(comments);

        return itemResponse;
    }

    @Override
    @Transactional
    public ItemResponse updateItemById(ItemRequest request, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoFoundObjectException(String.format("Item with id='%s' not found", itemId)));

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new AccessException(String.format("Owner of item with id='%s' is another", itemId));
        }

        if (StringUtils.hasLength(request.getName())) {
            item.setName(request.getName());
        }

        if (StringUtils.hasLength(request.getDescription())) {
            item.setDescription(request.getDescription());
        }

        if ((request.getAvailable() != null)) {
            item.setAvailable(request.getAvailable());
        }

        Item savedItem = itemRepository.save(item);
        return ItemMapper.objectToDto(savedItem);
    }

    @Override
    public List<ItemResponse> getAllByUserId(Long id) {
        userService.checkExistUserById(id);

        List<Item> items = itemRepository.findAllByOwnerId(id);
        List<ItemResponse> itemResponses = ItemMapper.objectToDto(items);

        List<Long> itemsId = itemResponses.stream()
                .map(ItemResponse::getId)
                .collect(Collectors.toList());

        List<Booking> bookingList = bookingService.getAllByItemIdIn(itemsId);

        return itemResponses
                .stream()
                .map(itemsDto -> setLastAndNextBookings(bookingList, itemsDto))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponse> searchItemByText(String text) {
        if (!StringUtils.hasLength(text)) {
            return List.of();
        }
        List<Item> items = itemRepository.findByText(text);
        return ItemMapper.objectToDto(items);
    }

    @Override
    @Transactional
    public CommentResponse createComment(CommentRequest request, Long userId, Long itemId) {
        Comment comment = CommentMapper.dtoToObject(request);

        User author = userService.findUserById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoFoundObjectException(String.format("Item with id='%s' not found", itemId)));

        List<Booking> bookings = bookingService.getAllByItemAndEndBeforeDate(itemId, comment.getCreated())
                .stream()
                .filter(booking -> Objects.equals(booking.getBooker().getId(), userId))
                .collect(Collectors.toList());

        if (bookings.isEmpty()) {
            throw new NoCorrectRequestException(String.format("User with id='%s' cannot leave a review of this thing",
                    userId));
        }

        comment.setAuthor(author);
        comment.setItem(item);
        Comment savedComment = commentService.save(comment);

        return CommentMapper.dtoToObject(savedComment);
    }

    private ItemResponse setLastAndNextBookings(List<Booking> bookingList, ItemResponse itemResponse) {
        LocalDateTime dateTime = LocalDateTime.now();

        bookingList
                .stream()
                .filter(booking -> Objects.equals(booking.getItem().getId(), itemResponse.getId()))
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .filter(booking -> booking.getStatus().equals(Status.APPROVED))
                .filter(booking -> booking.getStart().isBefore(dateTime))
                .limit(1)
                .findAny()
                .ifPresent(booking -> itemResponse.setLastBooking(BookingDto.builder()
                        .id(booking.getId())
                        .bookerId(booking.getBooker().getId())
                        .build()));

        bookingList
                .stream()
                .filter(booking -> Objects.equals(booking.getItem().getId(), itemResponse.getId()))
                .sorted(Comparator.comparing(Booking::getStart))
                .filter(booking -> booking.getStatus().equals(Status.APPROVED))
                .filter(booking -> booking.getStart().isAfter(dateTime))
                .limit(1)
                .findAny()
                .ifPresent(booking -> itemResponse.setNextBooking(BookingDto.builder()
                        .id(booking.getId())
                        .bookerId(booking.getBooker().getId())
                        .build()));

        return itemResponse;
    }
}
