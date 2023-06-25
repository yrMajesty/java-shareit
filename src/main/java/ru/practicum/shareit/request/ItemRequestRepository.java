package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorId(Long userId);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id <> ?1")
    List<ItemRequest> findAllByOwnerId(Long userId, Pageable pageable);
}
