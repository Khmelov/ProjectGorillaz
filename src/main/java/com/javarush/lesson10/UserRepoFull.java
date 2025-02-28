package com.javarush.lesson10;

import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.entity.User;
import com.javarush.khmelov.exception.AppException;
import com.javarush.khmelov.repository.Repository;
import jakarta.persistence.*;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@AllArgsConstructor
public class UserRepoFull implements Repository<User> {

    private final SessionCreator sessionCreator;

    @Override
    public Collection<User> getAll() {
        Session session = sessionCreator.getSession();
        try (session) {
            Transaction tx = session.beginTransaction();
            try {
                List<User> list = session.createQuery("SELECT u FROM User u", User.class).list();
                tx.commit();
                return list;
            } catch (Exception e) {
                tx.rollback();
                throw new AppException(e);
            }
        }
    }


    @Override
    /* session->cb->cq->root
     * c <- filter fields and add cb.equals(root.get(name), value)
     * cq.select(root).where(predicates);
     * result <- session.createQuery(cq).list(); */
    public Stream<User> find(User pattern) {
        Session session = sessionCreator.getSession();
        try (session) {
            HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            JpaCriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);
            Field[] fields = pattern.getClass().getDeclaredFields();
            List<Predicate> predicates = new ArrayList<>();
            for (Field field : fields) {
                if (field.trySetAccessible()) {
                    String name = field.getName();
                    Object value = field.get(pattern);
                    if (!(value == null) && notSkipField(field)) {
                        Predicate predicate = criteriaBuilder.equal(root.get(name), value);
                        predicates.add(predicate);
                    }
                }
            }
            criteriaQuery.select(root);
            criteriaQuery.where(predicates.toArray(new Predicate[0]));
            Query<User> query = session.createQuery(criteriaQuery);
            List<User> list = query.list();
            return list.stream();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean notSkipField(Field field) {
        return !field.isAnnotationPresent(Transient.class)
                && !field.isAnnotationPresent(OneToMany.class)
                && !field.isAnnotationPresent(ManyToOne.class)
                && !field.isAnnotationPresent(ManyToMany.class)
                && !field.isAnnotationPresent(OneToOne.class)
                ;
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
