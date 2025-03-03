package com.javarush.khmelov.repository;

import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.entity.AbstractEntity;
import jakarta.persistence.*;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@AllArgsConstructor
public class BaseRepository<Entity extends AbstractEntity> implements Repository<Entity> {

    private final SessionCreator sessionCreator;

    private final Class<Entity> entityClass;

    @Override
    public Collection<Entity> getAll() {
        Session session = sessionCreator.getSession();
        return session.createQuery("SELECT e FROM %s e".formatted(entityClass.getName()), entityClass).list();
    }


    @Override
    /* session->cb->cq->root
     * c <- filter fields and add cb.equals(root.get(name), value)
     * cq.select(root).where(predicates);
     * result <- session.createQuery(cq).list(); */
    public Stream<Entity> find(Entity pattern) {
        try {
            Session session = sessionCreator.getSession();
            HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            JpaCriteriaQuery<Entity> criteriaQuery = criteriaBuilder.createQuery(entityClass);
            Root<Entity> root = criteriaQuery.from(entityClass);
            Field[] fields = pattern.getClass().getDeclaredFields();
            List<Predicate> predicates = new ArrayList<>();
            for (Field field : fields) {
                if (field.trySetAccessible()) {
                    String name = field.getName();
                    Object value = field.get(pattern);
                    if (isPredacate(field, value)) {
                        Predicate predicate = criteriaBuilder.equal(root.get(name), value);
                        predicates.add(predicate);
                    }
                }
            }
            criteriaQuery.select(root);
            criteriaQuery.where(predicates.toArray(new Predicate[0]));
            Query<Entity> query = session.createQuery(criteriaQuery);
            List<Entity> list = query.list();
            return list.stream();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isPredacate(Field field, Object value) {
        return Objects.nonNull(value)
               && !field.isAnnotationPresent(Transient.class)
               && !field.isAnnotationPresent(OneToMany.class)
               && !field.isAnnotationPresent(ManyToOne.class)
               && !field.isAnnotationPresent(OneToOne.class)
               && !field.isAnnotationPresent(ManyToMany.class);
    }

    @Override
    public Entity get(long id) {
        Session session = sessionCreator.getSession();
        return session.find(entityClass, id);
    }

    @Override
    public void create(Entity entity) {
        Session session = sessionCreator.getSession();
        session.persist(entity);
    }

    @Override
    public void update(Entity entity) {
        Session session = sessionCreator.getSession();
        session.merge(entity);
    }

    @Override
    public void delete(Entity entity) {
        Session session = sessionCreator.getSession();
        session.remove(entity);
    }
}
