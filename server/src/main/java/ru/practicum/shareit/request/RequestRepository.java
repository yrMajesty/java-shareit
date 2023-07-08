package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequestorId(Long userId);

    @Query("SELECT ir FROM Request ir WHERE ir.requestor.id <> ?1")
    List<Request> findAllByOwnerId(Long userId, Pageable pageable);
}
