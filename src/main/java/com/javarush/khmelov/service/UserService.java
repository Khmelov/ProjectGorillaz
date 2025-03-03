package com.javarush.khmelov.service;

import com.javarush.khmelov.dto.UserTo;
import com.javarush.khmelov.entity.User;
import com.javarush.khmelov.exception.AppException;
import com.javarush.khmelov.mapping.Dto;
import com.javarush.khmelov.repository.Repository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.Optional;

@AllArgsConstructor
@Transactional
public class UserService {

    private final Repository<User> userRepository;
    private final Dto dto;

    public void create(UserTo userTo) {
        User loginPattern = User.builder().login(userTo.getLogin()).build();
        if (userRepository.find(loginPattern).findAny().isEmpty()) {
            userRepository.create(dto.from(userTo));
        } else {
            throw new AppException("User with login " + userTo.getLogin() + " already exists");
        }
    }

    public void update(UserTo userTo) {
        User user = dto.from(userTo);
        User userInDb = userRepository.get(userTo.getId());
        userInDb.setLogin(userTo.getLogin());
        userInDb.setPassword(userTo.getPassword());
        userRepository.update(userInDb);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public Collection<UserTo> getAll() {
        return userRepository
                .getAll()
                .stream()
                .map(dto::from)
                .toList();
    }

    public Optional<UserTo> get(long id) {
        return Optional
                .ofNullable(userRepository.get(id))
                .map(dto::from);
    }

    public Optional<UserTo> get(String login, String password) {
        User patternUser = User
                .builder()
                .login(login)
                .password(password)
                .build();
        return userRepository
                .find(patternUser)
                .map(dto::from)
                .findAny();
    }
}
