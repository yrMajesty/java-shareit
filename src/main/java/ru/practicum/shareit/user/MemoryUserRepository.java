package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryUserRepository {

    private final AtomicLong id = new AtomicLong(0);

    private final Map<Long, User> usersData = new HashMap<>();

    public User save(User user) {
        user.setId(id.incrementAndGet());
        usersData.put(user.getId(), user);

        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(usersData.get(id));
    }

    public User updateById(User newUser, Long userId) {
        usersData.put(userId, newUser);

        return usersData.get(userId);
    }

    public boolean isExistEmail(String email) {
        return usersData.values().stream().map(User::getEmail).anyMatch(email::equals);
    }

    public List<User> findAll() {
        return new ArrayList<>(usersData.values());
    }

    public void deleteById(Long id) {
        usersData.remove(id);
    }
}
