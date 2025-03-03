package com.javarush.lesson10;

import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.entity.Quest;
import com.javarush.khmelov.exception.AppException;
import com.javarush.khmelov.repository.Repository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class QuestRepo implements Repository<Quest> {

    private final SessionCreator sessionCreator;

    @Override
    public Collection<Quest> getAll() {
        Session session = sessionCreator.getSession();
        try (session) {
            Transaction tx = session.beginTransaction();
            try {
                Query<Quest> queryQuest = session.createQuery("SELECT u FROM Quest u", Quest.class);
                List<Quest> list = queryQuest.list();
                tx.commit();
                return list;
            } catch (Exception e) {
                tx.rollback();
                throw new AppException(e);
            }
        }
    }

    @Override
    public Stream<Quest> find(Quest pattern) {
        //Criteria API
        return null;
    }

    @Override
    public Quest get(long id) {
        Session session = sessionCreator.getSession();
        try (session) {
            Transaction tx = session.beginTransaction();
            try {
                Quest quest = session.find(Quest.class, id);
                tx.commit();
                return quest;
            } catch (Exception e) {
                tx.rollback();
                throw new AppException(e);
            }
        }
    }

    @Override
    public void create(Quest quest) {
        Session session = sessionCreator.getSession();
        try (session) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(quest);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw new AppException(e);
            }
        }
    }

    @Override
    public void update(Quest quest) {
        Session session = sessionCreator.getSession();
        try (session) {
            Transaction tx = session.beginTransaction();
            try {
                session.merge(quest);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw new AppException(e);
            }
        }
    }

    @Override
    public void delete(Quest quest) {
        Session session = sessionCreator.getSession();
        try (session) {
            Transaction tx = session.beginTransaction();
            try {
                session.remove(quest);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw new AppException(e);
            }
        }
    }
}
