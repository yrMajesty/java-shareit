package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.UserException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final MemoryItemRepository itemRepository;
    private final UserServiceImpl userService;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userService.findUserById(userId);

        Item newItem = ItemMapper.dtoToObject(itemDto);
        newItem.setOwner(user);

        return ItemMapper.objectToDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Item with id='%s' not found", id)));
        return ItemMapper.objectToDto(item);
    }

    @Override
    public ItemDto updateById(ItemDto itemDto, Long id, Long userId) {
        Item foundItem = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Item with id='%s' not found", id)));

        if (!Objects.equals(foundItem.getOwner().getId(), userId)) {
            throw new UserException(String.format("Owner of item with id='%s' is another", foundItem.getId()));
        }

        if (StringUtils.hasLength(itemDto.getName())) {
            foundItem.setName(itemDto.getName());
        }

        if (StringUtils.hasLength(itemDto.getDescription())) {
            foundItem.setDescription(itemDto.getDescription());
        }

        if ((itemDto.getAvailable() != null)) {
            foundItem.setAvailable(itemDto.getAvailable());
        }

        itemRepository.updateById(foundItem, id);
        return ItemMapper.objectToDto(foundItem);
    }

    @Override
    public List<ItemDto> getAllByUserId(Long userId) {
        List<Item> items = itemRepository.findByUserId(userId);

        return ItemMapper.objectToDto(items);
    }

    public List<ItemDto> searchByText(String text) {
        if (!StringUtils.hasLength(text)) {
            return List.of();
        }
        return ItemMapper.objectToDto(itemRepository.findByText(text));
    }
}
