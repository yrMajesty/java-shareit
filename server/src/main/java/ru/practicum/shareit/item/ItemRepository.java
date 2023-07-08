package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            "OR upper(i.description) like upper(concat('%', ?1, '%')) AND i.available=true")
    List<Item> findByText(String text, Pageable pageable);

    List<Item> findAllByOwnerId(Long id, Pageable pageable);

    List<Item> findAllByRequestIdIn(Set<Long> ids);

    Item findByRequestId(Long requestId);

    List<Item> findAllByOwnerId(Long id);
}
