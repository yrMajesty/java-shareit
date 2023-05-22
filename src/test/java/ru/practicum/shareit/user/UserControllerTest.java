package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    Validator validator;

    @BeforeEach
    void prepare() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void createItem_correctSizeValidationList_completelyIncorrectItem() {
        UserDto test = new UserDto();

        assertEquals(2, validator.validate(test).size());
    }

    @Test
    void createUser_notValidName_nameIsEmpty() {
        UserDto test = UserDto.builder().name("").email("test@mail.ru").build();

        List<ConstraintViolation<UserDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Name cannot be empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createUser_notValidName_nameIsNull() {
        UserDto test = UserDto.builder().name(null).email("test@mail.ru").build();

        List<ConstraintViolation<UserDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Name cannot be empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createUser_notValidEmail_emailIsEmpty() {
        UserDto test = UserDto.builder().name("Test").email("").build();

        List<ConstraintViolation<UserDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(2, validationSet.size()),
                () -> assertEquals("Email cannot empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createUser_notValidEmail_emailIsNull() {
        UserDto test = UserDto.builder().name("Test").email(null).build();

        List<ConstraintViolation<UserDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Email cannot empty or null", validationSet.get(0).getMessage())
        );
    }


    @Test
    void createUser_notValidEmail_incorrectEmail1() {
        UserDto test = UserDto.builder().name("Test").email("email").build();

        List<ConstraintViolation<UserDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Email is not format as email (email@email.com)", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createUser_notValidEmail_incorrectEmail2() {
        UserDto test = UserDto.builder().name("Test").email("email@").build();

        List<ConstraintViolation<UserDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Email is not format as email (email@email.com)", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createUser_notValidEmail_incorrectEmail3() {
        UserDto test = UserDto.builder().name("Test").email("email@.").build();

        List<ConstraintViolation<UserDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Email is not format as email (email@email.com)", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createUser_notValidEmail_incorrectEmail4() {
        UserDto test = UserDto.builder().name("Test").email("email@com").build();

        List<ConstraintViolation<UserDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Email is not format as email (email@email.com)", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createUser_notValidEmail_incorrectEmail5() {
        UserDto test = UserDto.builder().name("Test").email("email@com.").build();

        List<ConstraintViolation<UserDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Email is not format as email (email@email.com)", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createUser_notValidEmail_incorrectEmail6() {
        UserDto test = UserDto.builder().name("Test").email("email@com.r").build();

        List<ConstraintViolation<UserDto>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Email is not format as email (email@email.com)", validationSet.get(0).getMessage())
        );
    }
}