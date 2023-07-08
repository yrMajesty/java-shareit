package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NoCorrectRequestException;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.exception.NoValidArgumentException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.comment.CommentService;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class ItemServiceImplTest {
    @Autowired
    ItemServiceImpl underTest;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    UserService userService;

    @MockBean
    CommentService commentService;

    @MockBean
    RequestRepository requestRepository;

    User user1;
    User user2;
    User user3;
    Item item;
    Booking bookingUser2;
    Booking bookingUser3;
    Comment comment;

    BookingDto bookingDtoUser2;
    BookingDto bookingDtoUser3;

    @BeforeEach
    void prepare() {
        user1 = User.builder().id(1L).name("Tom").email("tom@mail.ru").build();
        user2 = User.builder().id(2L).name("Mike").email("mike@mail.ru").build();
        user3 = User.builder().id(3L).name("Sam").email("sam@mail.ru").build();

        item = Item.builder()
                .id(1L)
                .name("Book")
                .description("Good old book")
                .owner(user1)
                .request(new Request(10L, "I need book", user2, LocalDateTime.now()))
                .available(true)
                .build();

        bookingUser2 = Booking.builder()
                .id(1L)
                .item(item)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(5))
                .booker(user2)
                .status(Status.APPROVED)
                .build();

        bookingUser3 = Booking.builder()
                .id(3L)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(15))
                .booker(user3)
                .status(Status.APPROVED)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("good book")
                .author(user2)
                .item(item)
                .created(LocalDateTime.now().minusDays(2))
                .build();

        bookingDtoUser2 = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(5))
                .build();

        bookingDtoUser3 = BookingDto.builder()
                .id(3L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(15))
                .build();

    }

    @Test
    void createItem_successfulCreated_requestIsCorrectAndUserExist() {
        Long userId = 1L;
        ItemRequestDto request = ItemRequestDto.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        when(userService.findUserById(anyLong())).thenReturn(user1);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        underTest.createItem(request, userId);

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_notFoundObjectException_userDoesNotExist() {
        Long userId = 100L;
        ItemRequestDto request = ItemRequestDto.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        Mockito.doThrow(NoFoundObjectException.class)
                .when(userService).findUserById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.createItem(request, userId));
    }

    @Test
    void createItem_successfulCreatedWithNullRequest_requestIsCorrectAndUserExist() {
        Long userId = 1L;
        ItemRequestDto request = ItemRequestDto.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .requestId(55L)
                .build();

        item.setRequest(null);

        when(userService.findUserById(anyLong())).thenReturn(user1);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponseDto result = underTest.createItem(request, userId);

        verify(itemRepository, times(1)).save(any(Item.class));
        Assertions.assertEquals(0, result.getRequestId());
    }

    @Test
    void createItem_successfulCreatedWithNotNullRequest_requestIsCorrectAndUserExist() {
        Long userId = 1L;
        Request ir = Request.builder()
                .id(10L)
                .description("I need book")
                .build();
        ItemRequestDto request = ItemRequestDto.builder()
                .name("Book")
                .description("Good old book")
                .available(true)
                .requestId(10L)
                .build();

        when(userService.findUserById(anyLong())).thenReturn(user1);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(ir));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponseDto result = underTest.createItem(request, userId);

        verify(itemRepository, times(1)).save(any(Item.class));
        Assertions.assertEquals(10L, result.getRequestId());
    }

    @Test
    void getItemById_notFoundObjectException_itemDoesNotExist() {
        Long userId = 1L;
        Long itemId = 1L;

        Mockito.doThrow(NoFoundObjectException.class)
                .when(itemRepository).findById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.getItemById(itemId, userId));
    }

    @Test
    void getItemById_correctResult_itemExist() {
        Long userId = 1L;
        Long itemId = 1L;
        Page<BookingDto> page1 = new PageImpl<>(List.of(bookingDtoUser2), PageRequest.of(0, 10), 10);
        Page<BookingDto> page2 = new PageImpl<>(List.of(), PageRequest.of(0, 10), 10);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.findNextBooking(anyLong(), anyLong(),
                any(Status.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(page1);

        when(bookingRepository.findLastBooking(anyLong(), anyLong(),
                any(Status.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(page2);

        when(commentService.getAllCommentsByItemId(anyLong()))
                .thenReturn(CommentMapper.objectsToDto(List.of(comment)));

        underTest.getItemById(itemId, userId);

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getItemById_correctResultWithBookings_itemExist() {
        Page<BookingDto> page1 = new PageImpl<>(List.of(bookingDtoUser2), PageRequest.of(0, 10), 10);
        Page<BookingDto> page2 = new PageImpl<>(List.of(bookingDtoUser3), PageRequest.of(0, 10), 10);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.findNextBooking(anyLong(), anyLong(),
                any(Status.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(page1);

        when(bookingRepository.findLastBooking(anyLong(), anyLong(),
                any(Status.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(page2);

        when(commentService.getAllCommentsByItemId(anyLong()))
                .thenReturn(CommentMapper.objectsToDto(List.of(comment)));

        underTest.getItemById(1L, 1L);

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateItemById_notFoundObjectException_itemNotExist() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemRequestDto request = ItemRequestDto.builder()
                .name("Update title Book")
                .description("Good old book")
                .available(true)
                .build();

        Mockito.doThrow(NoFoundObjectException.class)
                .when(itemRepository).findById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.updateItemById(request, itemId, userId));
    }

    @Test
    void updateItemById_accessException_userIsNotOwnerItem() {
        Long userId = 2L;
        Long itemId = 1L;
        ItemRequestDto request = ItemRequestDto.builder()
                .name("Update title Book")
                .description("Good old book")
                .available(true)
                .build();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(AccessException.class, () -> underTest.updateItemById(request, itemId, userId));
    }

    @Test
    void updateItemById_successfullyUpdated_userExist() {
        Long userId = 1L;
        Long itemId = 1L;

        ItemRequestDto request = ItemRequestDto.builder()
                .name("Update title Book")
                .description("Good old book")
                .available(true)
                .build();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        item.setName(request.getName());

        when(itemRepository.save(any(Item.class))).thenReturn(item);

        underTest.updateItemById(request, itemId, userId);

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void getAllItemsByUserId_noValidArgumentException_argumentFromIsIncorrect() {
        Long userId = 1L;
        Integer from = -1;
        Integer size = 10;

        doNothing()
                .when(userService)
                .checkExistUserById(anyLong());

        assertThrows(NoValidArgumentException.class, () -> underTest.getAllItemsByUserId(userId, from, size));
    }

    @Test
    void getAllItemsByUserId_noValidArgumentException_argumentSizeIsIncorrect() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = -10;

        doNothing()
                .when(userService)
                .checkExistUserById(anyLong());

        assertThrows(NoValidArgumentException.class, () -> underTest.getAllItemsByUserId(userId, from, size));
    }

    @Test
    void getAllItemsByUserId_notFoundObjectException_userNotExist() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 20;

        Mockito.doThrow(NoFoundObjectException.class)
                .when(userService).checkExistUserById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.getAllItemsByUserId(userId, from, size));
    }

    @Test
    void searchItemByText_notEmptyList_itemExist() {
        Integer from = 0;
        Integer size = 20;

        String text = "book";

        when(itemRepository.findByText(anyString(), any()))
                .thenReturn(List.of(item));

        List<ItemResponseDto> result = underTest.searchItemByText(text, from, size);


        verify(itemRepository, times(1)).findByText(anyString(), any());

        assertFalse(result.isEmpty());
    }

    @Test
    void searchItemByText_emptyList_itemExist() {
        Integer from = 0;
        Integer size = 20;
        String text = "";

        List<ItemResponseDto> result = underTest.searchItemByText(text, from, size);

        verify(itemRepository, times(0)).findByText(anyString(), any());

        assertTrue(result.isEmpty());
    }

    @Test
    void createComment_notFoundObjectException_userNotExist() {
        Long userId = 2L;
        Long itemId = 1L;

        CommentRequestDto request = CommentRequestDto.builder()
                .text("good book")
                .build();

        Mockito.doThrow(NoFoundObjectException.class)
                .when(userService).findUserById(anyLong());

        assertThrows(NoFoundObjectException.class, () -> underTest.createComment(request, userId, itemId));
    }

    @Test
    void createComment_notFoundObjectException_itemNotExist() {
        Long userId = 2L;
        Long itemId = 2L;

        CommentRequestDto request = CommentRequestDto.builder()
                .text("good book")
                .build();

        when(userService.findUserById(anyLong()))
                .thenReturn(user2);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());


        assertThrows(NoFoundObjectException.class, () -> underTest.createComment(request, userId, itemId));
    }

    @Test
    void createComment_noCorrectRequestException_userHasNotBookingItem() {
        Long userId = 2L;
        Long itemId = 2L;

        CommentRequestDto request = CommentRequestDto.builder()
                .text("good book")
                .build();

        when(userService.findUserById(anyLong()))
                .thenReturn(user2);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.findByItemIdAndEndIsBefore(anyLong(), any()))
                .thenReturn(List.of());

        assertThrows(NoCorrectRequestException.class, () -> underTest.createComment(request, userId, itemId));
    }

    @Test
    void createComment_successfullyCreated_requestCreateIsCorrect() {
        Long userId = 2L;
        Long itemId = 2L;

        CommentRequestDto request = CommentRequestDto.builder()
                .text("good book")
                .build();

        when(userService.findUserById(anyLong()))
                .thenReturn(user2);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.findByItemIdAndEndIsBefore(anyLong(), any()))
                .thenReturn(List.of(bookingUser2));

        when(commentService.createComment(any(Comment.class)))
                .thenReturn(comment);

        underTest.createComment(request, userId, itemId);

        verify(commentService, times(1)).createComment(any(Comment.class));
    }

    @Test
    void getAllByRequestIds_emptyList_requestsDontExist() {
        when(itemRepository.findAllByRequestIdIn(any()))
                .thenReturn(List.of());

        underTest.getAllByRequestIds(Set.of(11L, 22L));

        verify(itemRepository, times(1)).findAllByRequestIdIn(any());
    }

    @Test
    void getItemByRequestId_nullResult_requestDontExist() {
        underTest.getItemByRequestId(11L);

        verify(itemRepository, times(1)).findByRequestId(anyLong());
    }
}