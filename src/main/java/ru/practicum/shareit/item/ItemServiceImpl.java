package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NoCorrectRequestException;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemResponseDto createItem(ItemRequestDto request, Long userId) {
        User user = userService.findUserById(userId);

        Item item = ItemMapper.dtoToObject(request);
        item.setOwner(user);

        Item savedItem = itemRepository.save(item);
        return ItemMapper.objectToDto(savedItem);
    }

    @Override
    public ItemResponseDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoFoundObjectException(String.format("Item with id='%s' not found", itemId)));

        ItemResponseDto itemResponseDto = ItemMapper.objectToDto(item);

        if (Objects.equals(userId, item.getOwner().getId())) {
            itemResponseDto.setNextBooking(
                    (bookingRepository
                            .findNextBooking(itemId, userId, Status.APPROVED, LocalDateTime.now(),
                                    PageRequest.of(0, 1)))
                            .get().findFirst().orElse(null));

            itemResponseDto.setLastBooking(
                    (bookingRepository
                            .findLastBooking(itemId, userId, Status.APPROVED, LocalDateTime.now(),
                                    PageRequest.of(0, 1)))
                            .get().findFirst().orElse(null));
        }

        List<CommentResponse> comments = commentService.findAllByItemId(itemId);
        itemResponseDto.setComments(comments);

        return itemResponseDto;
    }

    @Override
    @Transactional
    public ItemResponseDto updateItemById(ItemRequestDto request, Long itemId, Long userId) {
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
    public List<ItemResponseDto> getAllByUserId(Long userId) {
        userService.checkExistUserById(userId);

        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemResponseDto> itemResponseDtos = ItemMapper.objectToDto(items);

        return itemResponseDtos
                .stream()
                .peek(itemsDto -> {
                    itemsDto.setNextBooking(
                            (bookingRepository
                                    .findNextBooking(itemsDto.getId(), userId, Status.APPROVED, LocalDateTime.now(),
                                            PageRequest.of(0, 1)))
                                    .get().findFirst().orElse(null));

                    itemsDto.setLastBooking(
                            (bookingRepository
                                    .findLastBooking(itemsDto.getId(), userId, Status.APPROVED, LocalDateTime.now(),
                                            PageRequest.of(0, 1)))
                                    .get().findFirst().orElse(null));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> searchItemByText(String text) {
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

        List<Booking> bookings = bookingRepository.findByItemIdAndEndIsBefore(itemId, comment.getCreated())
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
}
