package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ExistEmailException;
import ru.practicum.shareit.user.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryUserRepository {

    private final AtomicLong id = new AtomicLong(0);

    private final Map<Long, User> usersData = new HashMap<>();


    public User save(User user) {
        if (isExistEmail(user.getEmail())) {
            String error = String.format("Email %s already exist", user.getEmail());
            throw new ExistEmailException(error);
        }

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
        long resultCount = usersData.values()
                .stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .count();

        return resultCount > 0;
    }

    public List<User> findAll() {
        return new ArrayList<>(usersData.values());
    }

    public void deleteById(Long id) {
        usersData.remove(id);
    }
}
