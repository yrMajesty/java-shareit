package ru.practicum.shareit.item.comment;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Sql("classpath:repository/data.sql")
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Test
    void findAllByItemId_notEmptyResultList_commentsExist() {
        List<Comment> results = commentRepository.findAllByItemId(1L);

        assertFalse(results.isEmpty());
        Assertions.assertThat(results.get(0).getText()).isEqualTo("super interesting good book");
        Assertions.assertThat(results.get(0).getAuthor().getId()).isEqualTo(3L);
    }
}