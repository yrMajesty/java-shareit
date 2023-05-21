package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryUserRepository {

    private final AtomicLong id = new AtomicLong(0);

    private final Map<Long, User> usersData = new HashMap<>();
    private final Map<Long, String> emails = new HashMap<>();

    public User save(User user) {
        user.setId(id.incrementAndGet());
        usersData.put(user.getId(), user);
        emails.put(user.getId(), user.getEmail());

        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(usersData.get(id));
    }

    public User updateById(User newUser, Long userId) {
        usersData.put(userId, newUser);
        emails.put(userId, newUser.getEmail());

        return usersData.get(userId);
    }

    public boolean isExistEmail(String email) {
        return emails.containsValue(email);
    }

    public List<User> findAll() {
        return new ArrayList<>(usersData.values());
    }

    public void deleteById(Long id) {
        usersData.remove(id);
        emails.remove(id);
    }
}