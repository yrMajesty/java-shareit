package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserRequest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        UserRequest test = new UserRequest();

        assertEquals(2, validator.validate(test).size());
    }

    @Test
    void createUser_notValidName_nameIsEmpty() {
        UserRequest test = UserRequest.builder().name("").email("test@mail.ru").build();

        List<ConstraintViolation<UserRequest>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Name cannot be empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createUser_notValidName_nameIsNull() {
        UserRequest test = UserRequest.builder().name(null).email("test@mail.ru").build();

        List<ConstraintViolation<UserRequest>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Name cannot be empty or null", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createUser_notValidEmail_emailIsEmpty() {
        UserRequest test = UserRequest.builder().name("Test").email("").build();

        List<ConstraintViolation<UserRequest>> validationSet = new ArrayList<>(validator.validate(test));
        assertEquals(2, validationSet.size());
    }

    @Test
    void createUser_notValidEmail_emailIsNull() {
        UserRequest test = UserRequest.builder().name("Test").email(null).build();

        List<ConstraintViolation<UserRequest>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Email cannot empty or null", validationSet.get(0).getMessage())
        );
    }


    @Test
    void createUser_notValidEmail_incorrectEmail1() {
        UserRequest test = UserRequest.builder().name("Test").email("email").build();

        List<ConstraintViolation<UserRequest>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Email is not format as email (email@email.com)", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createUser_notValidEmail_incorrectEmail2() {
        UserRequest test = UserRequest.builder().name("Test").email("email@").build();

        List<ConstraintViolation<UserRequest>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Email is not format as email (email@email.com)", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createUser_notValidEmail_incorrectEmail3() {
        UserRequest test = UserRequest.builder().name("Test").email("email@.").build();

        List<ConstraintViolation<UserRequest>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Email is not format as email (email@email.com)", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createUser_notValidEmail_incorrectEmail4() {
        UserRequest test = UserRequest.builder().name("Test").email("email@com").build();

        List<ConstraintViolation<UserRequest>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Email is not format as email (email@email.com)", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createUser_notValidEmail_incorrectEmail5() {
        UserRequest test = UserRequest.builder().name("Test").email("email@com.").build();

        List<ConstraintViolation<UserRequest>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Email is not format as email (email@email.com)", validationSet.get(0).getMessage())
        );
    }

    @Test
    void createUser_notValidEmail_incorrectEmail6() {
        UserRequest test = UserRequest.builder().name("Test").email("email@com.r").build();

        List<ConstraintViolation<UserRequest>> validationSet = new ArrayList<>(validator.validate(test));
        assertAll(
                () -> assertEquals(1, validationSet.size()),
                () -> assertEquals("Email is not format as email (email@email.com)", validationSet.get(0).getMessage())
        );
    }
}