package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@Sql("classpath:repository/data.sql")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void findByEmail_notEmptyOptional_emailExist() {
        Optional<User> resultOptional = userRepository.findByEmail("tom@mail.ru");

        assertTrue(resultOptional.isPresent());
        assertThat(resultOptional.get().getId()).isNotNull();
        assertThat(resultOptional.get().getName()).isEqualTo("Tom");
        assertThat(resultOptional.get().getEmail()).isEqualTo("tom@mail.ru");
    }
}