package com.javarush.lesson09;

import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.entity.User;
import com.javarush.khmelov.exception.AppException;
import com.javarush.khmelov.repository.Repository;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@AllArgsConstructor
public class UserRepo implements Repository<User> {

    private final SessionCreator sessionCreator;

    @Override
    public Collection<User> getAll() {
        //HQL
        return List.of();
    }

    @Override
    public Stream<User> find(User pattern) {
        //Criteria API
        return Stream.empty();
    }

    @Override
    public User get(long id) {
        Session session = sessionCreator.getSession();
        try (session) {
            Transaction tx = session.beginTransaction();
            try {
                User user = session.find(User.class, 1L);
                tx.commit();
                return user;
            } catch (Exception e) {
                tx.rollback();
                throw new AppException("not found User with id " + id, e);
            }
        }
    }

    @Override
    public void create(User user) {
        Session session = sessionCreator.getSession();
        try (session) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(user);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw new AppException("error while creating user", e);
            }
        }
    }

    @Override
    public void update(User user) {
        Session session = sessionCreator.getSession();
        try (session) {
            Transaction tx = session.beginTransaction();
            try {
                session.merge(user);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw new AppException("error while creating user", e);
            }
        }
    }

    @Override
    public void delete(User user) {
        Session session = sessionCreator.getSession();
        try (session) {
            Transaction tx = session.beginTransaction();
            try {
                session.remove(user);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw new AppException("error while creating user", e);
            }
        }
    }
}
