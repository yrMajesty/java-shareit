package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Sql("classpath:repository/data.sql")
class RequestRepositoryTest {
    @Autowired
    RequestRepository requestRepository;

    @Test
    void findAllByOwnerId_notEmptyList_itemRequestExist() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("created").descending());

        List<Request> results = requestRepository.findAllByOwnerId(3L, pageable);

        assertFalse(results.isEmpty());
        Assertions.assertThat(results.get(0).getDescription()).isEqualTo("i need book");
    }

    @Test
    void findAllByRequestorId_notEmptyList_itemRequestExist() {
        List<Request> results = requestRepository.findAllByRequestorId(3L);

        assertFalse(results.isEmpty());
        Assertions.assertThat(results.get(0).getDescription()).isEqualTo("i need tv box");
    }
}