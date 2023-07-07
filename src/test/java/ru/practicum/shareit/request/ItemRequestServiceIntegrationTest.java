package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIntegrationTest {
    private final ItemRequestService underTest;
    private final UserService userService;
    private final EntityManager entityManager;

    @BeforeEach
    void prepare() {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY = 0; " +
                "TRUNCATE TABLE requests, users RESTART IDENTITY;" +
                "SET REFERENTIAL_INTEGRITY = 1;");
    }

    @Test
    void createRequest_noFoundObjectException_userDoesNotExist() {
        RequestDto request = createRequestDto();

        assertThrows(NoFoundObjectException.class, () -> underTest.createRequest(request, 100L));
    }

    @Test
    void createRequest_createdRequest_userExist() {
        UserResponseDto user = userService.createUser(createFirstUserDto());

        RequestDto request = createRequestDto();
        RequestDto result = underTest.createRequest(request, user.getId());

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo(request.getDescription());
    }

    @Test
    void getRequestById_noFoundObjectException_requestDoesNotExist() {
        assertThrows(NoFoundObjectException.class, () -> underTest.getRequestById(1L, 1L));
    }

    @Test
    void getRequestById_foundRequest_requestExist() {
        RequestDto request = createRequestDto();

        assertThrows(NoFoundObjectException.class, () -> underTest.createRequest(request, 100L));
    }

    @Test
    void getOwnerRequestByUserId_notEmptyResultList_requestExist() {
        UserResponseDto createdUser = userService.createUser(createFirstUserDto());
        RequestDto requestDto = createRequestDto();
        requestDto.setRequestorId(createdUser.getId());

        RequestDto createdRequest = underTest.createRequest(requestDto, createdUser.getId());

        List<RequestDto> result = underTest.getOwnerRequestByUserId(createdUser.getId());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(createdRequest.getId());
    }

    @Test
    void getOwnerRequestByUserId_emptyResultList_requestExist() {
        UserResponseDto createdUser1 = userService.createUser(createFirstUserDto());
        UserResponseDto createdUser2 = userService.createUser(createSecondUserDto());


        RequestDto requestDto = createRequestDto();
        requestDto.setRequestorId(createdUser1.getId());

        RequestDto createdRequest = underTest.createRequest(requestDto, createdUser1.getId());

        List<RequestDto> result = underTest.getOwnerRequestByUserId(createdUser2.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void getRequestsOtherUsers_emptyResultList_requestFromOwner() {
        UserResponseDto createdUser1 = userService.createUser(createFirstUserDto());
        UserResponseDto createdUser2 = userService.createUser(createSecondUserDto());

        RequestDto requestDto = createRequestDto();
        requestDto.setRequestorId(createdUser1.getId());

        RequestDto createdRequest = underTest.createRequest(requestDto, createdUser1.getId());

        List<RequestDto> result = underTest.getRequestsOtherUsers(createdUser1.getId(), 0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    void getRequestsOtherUsers_emptyResultList_requestFromOtherUser() {
        UserResponseDto createdUser1 = userService.createUser(createFirstUserDto());
        UserResponseDto createdUser2 = userService.createUser(createSecondUserDto());

        RequestDto requestDto = createRequestDto();
        requestDto.setRequestorId(createdUser1.getId());

        RequestDto createdRequest = underTest.createRequest(requestDto, createdUser2.getId());

        List<RequestDto> result = underTest.getRequestsOtherUsers(createdUser1.getId(), 0, 10);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getDescription()).isEqualTo(requestDto.getDescription());
    }

    private static UserRequestDto createFirstUserDto() {
        return UserRequestDto.builder().name("Mike").email("mike@mail.domen").build();
    }

    private static UserRequestDto createSecondUserDto() {
        return UserRequestDto.builder().name("Tom").email("tom@mail.domen").build();
    }

    private static CommentRequestDto createCommentDto() {
        return CommentRequestDto.builder()
                .text("good book")
                .build();
    }

    private static RequestDto createRequestDto() {
        return RequestDto.builder()
                .description("i need interesting book")
                .build();
    }
}