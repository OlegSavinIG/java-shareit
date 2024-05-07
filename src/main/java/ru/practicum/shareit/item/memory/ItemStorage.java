package ru.practicum.shareit.item.memory;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item addItem(Item item);

    Optional<Item> getItemById(Long id);

    Item updateItem(ItemDto item);

    void deleteItemById(Long id);

    List<Item> getAllItems(List<Long> itemIds);

    List<Item> searchByNameOrDescription(String text, Long userId);
}