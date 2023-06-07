package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoFoundObjectException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequest getItemRequestById(Long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new NoFoundObjectException(String.format("ItemRequest with id='%s' not found", id)));
    }
}
