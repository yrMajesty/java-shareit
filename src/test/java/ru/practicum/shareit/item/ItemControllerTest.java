package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    ItemService itemService;

    @Autowired
    ObjectMapper objectMapper;

    String userIdHeader = "X-Sharer-User-Id";

    @Nested
    class ValidationItemTest {
        Validator validator;

        @BeforeEach
        void prepare() {
            try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
                validator = validatorFactory.getValidator();
            }
        }

        @Test
        void validationItem_correctSizeValidationList_completelyIncorrectItem() {
            ItemRequestDto test = new ItemRequestDto();

            assertEquals(3, validator.validate(test).size());
        }

        @Test
        void validationItem_notValidName_nameIsEmpty() {
            ItemRequestDto test = ItemRequestDto.builder().name("").description("description").available(true).build();

            List<ConstraintViolation<ItemRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Name cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        void validationItem_notValidName_nameIsNull() {
            ItemRequestDto test = ItemRequestDto.builder().name(null).description("description").available(true).build();

            List<ConstraintViolation<ItemRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Name cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        void validationItem_notValidName_descriptionIsEmpty() {
            ItemRequestDto test = ItemRequestDto.builder().name("name").description("").available(true).build();

            List<ConstraintViolation<ItemRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Description cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        void validationItem_notValidName_descriptionIsNull() {
            ItemRequestDto test = ItemRequestDto.builder().name("name").description(null).available(true).build();

            List<ConstraintViolation<ItemRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Description cannot be empty or null", validationSet.get(0).getMessage())
            );
        }

        @Test
        void validationItem_notValidName_availableIsNull() {
            ItemRequestDto test = ItemRequestDto.builder().name("name").description("description").available(null).build();

            List<ConstraintViolation<ItemRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Available cannot be null", validationSet.get(0).getMessage())
            );
        }

        @Test
        void validationItem_notValidTest_textCommentIsNull() {
            CommentRequestDto test = CommentRequestDto.builder().text(null).build();

            List<ConstraintViolation<CommentRequestDto>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Text cannot be null or empty", validationSet.get(0).getMessage())
            );
        }
    }

    @Test
    void getAllItemsByUserId_statusOk_userExistAndItemsDoNotExist() throws Exception {
        when(itemService.getAllItemsByUserId(anyLong(), any(), any()))
                .thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .header(userIdHeader, 1))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").doesNotExist());
    }

    @Test
    void getAllItemsByUserId_statusNotFound_userDoesNotExist() throws Exception {
        doThrow(NoFoundObjectException.class)
                .when(itemService)
                .getAllItemsByUserId(anyLong(), any(), any());

        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .header(userIdHeader, 1))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getItem_statusNotFound_itemDoesNotExist() throws Exception {
        doThrow(NoFoundObjectException.class)
                .when(itemService)
                .getItemById(anyLong(), anyLong());

        mvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .header(userIdHeader, 1))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getItem_statusIsOk_itemExist() throws Exception {
        ItemResponseDto itemResponse = ItemResponseDto.builder()
                .id(1L)
                .name("Book")
                .description("Good old book")
                .build();

        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemResponse);

        mvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .header(userIdHeader, 1))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Book"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Good old book"));
    }

    @Test
    void updateItem_statusNotFound_itemDoesNotFound() throws Exception {
        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .id(1L)
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();


        doThrow(NoFoundObjectException.class)
                .when(itemService)
                .updateItemById(any(ItemRequestDto.class), anyLong(), anyLong());

        mvc.perform(patch("/items/1")
                        .header(userIdHeader, 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItem_statusIsForbidden_userIsNotOwnerItem() throws Exception {
        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .id(1L)
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        doThrow(AccessException.class)
                .when(itemService)
                .updateItemById(any(ItemRequestDto.class), anyLong(), anyLong());

        mvc.perform(patch("/items/1")
                        .header(userIdHeader, 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void updateItem_statusIsOk_userIsNotOwnerItem() throws Exception {
        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .id(1L)
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        ItemResponseDto itemResponse = ItemResponseDto.builder()
                .id(1L)
                .name("Book")
                .description("Good old book")
                .build();

        when(itemService.updateItemById(any(ItemRequestDto.class), anyLong(), anyLong()))
                .thenReturn(itemResponse);

        mvc.perform(patch("/items/1")
                        .header(userIdHeader, 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void createItem_statusOk_itemExist() throws Exception {
        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .id(1L)
                .name("Book")
                .description("Good old book")
                .available(true)
                .build();

        ItemResponseDto itemResponse = ItemResponseDto.builder()
                .id(1L)
                .name("Book")
                .description("Good old book")
                .build();

        when(itemService.createItem(any(ItemRequestDto.class), anyLong()))
                .thenReturn(itemResponse);

        mvc.perform(post("/items")
                        .header(userIdHeader, 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Book"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Good old book"));
    }

    @Test
    void searchByText_statusOk_textIsNotEmpty() throws Exception {
        when(itemService.searchItemByText(anyString(), any(), any()))
                .thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", ""))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").doesNotExist());
    }

    @Test
    void searchByText_statusOk_textIsEmpty() throws Exception {
        ItemResponseDto itemResponse = ItemResponseDto.builder()
                .id(1L)
                .name("Book")
                .description("Good old book")
                .build();

        when(itemService.searchItemByText(anyString(), any(), any()))
                .thenReturn(List.of(itemResponse));

        mvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", "book"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Book"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Good old book"));
    }

    @Test
    void createComment_statusOk_itemExist() throws Exception {
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("Good item")
                .build();

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .authorName("User001")
                .text("Good item")
                .created(LocalDateTime.of(2023, 5, 11, 17, 10, 36))
                .build();

        when(itemService.createComment(any(CommentRequestDto.class), anyLong(), anyLong()))
                .thenReturn(commentResponseDto);

        mvc.perform(post("/items/1/comment")
                        .header(userIdHeader, 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.text").value("Good item"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created").value(commentResponseDto.getCreated().toString()));
    }

    @Test
    void createComment_statusForbidden_itemDoesNotExist() throws Exception {
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("Good item")
                .build();

        doThrow(AccessException.class)
                .when(itemService)
                .createComment(any(CommentRequestDto.class), anyLong(), anyLong());

        mvc.perform(post("/items/1/comment")
                        .header(userIdHeader, 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
    }
}