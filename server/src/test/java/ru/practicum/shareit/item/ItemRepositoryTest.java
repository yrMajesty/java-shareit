package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Sql("classpath:repository/data.sql")
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    void findByText_notEmptyOptional_itemExist() {
        List<Item> results = itemRepository.findByText("book", PageRequest.of(0, 20));

        assertFalse(results.isEmpty());
        Assertions.assertThat(results.get(0).getName()).isEqualTo("Book");
        Assertions.assertThat(results.get(0).getOwner().getId()).isEqualTo(1L);
    }

    @Test
    void findAllByOwnerId_notEmptyList_itemsExist() {
        List<Item> results = itemRepository.findAllByOwnerId(1L, PageRequest.of(0, 20));

        assertFalse(results.isEmpty());
        Assertions.assertThat(results.get(0).getName()).isEqualTo("Book");
        Assertions.assertThat(results.get(0).getOwner().getId()).isEqualTo(1L);
    }

    @Test
    void findAllByRequestIdIn_notEmptyList_itemsExist() {
        List<Item> results = itemRepository.findAllByRequestIdIn(Set.of(1L));

        assertFalse(results.isEmpty());
        Assertions.assertThat(results.get(0).getName()).isEqualTo("TV box");
        Assertions.assertThat(results.get(0).getOwner().getId()).isEqualTo(2L);
    }

    @Test
    void findByRequestId_notEmptyList_itemExist() {
        Item result = itemRepository.findByRequestId(1L);

        assertNotNull(result);
        Assertions.assertThat(result.getName()).isEqualTo("TV box");
        Assertions.assertThat(result.getOwner().getId()).isEqualTo(2L);
    }

}