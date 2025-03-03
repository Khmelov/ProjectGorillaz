package com.javarush.lesson10;

import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.entity.Quest;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuestRepoTest {

    private Session session;
    private SessionCreator sessionCreator;
    private QuestRepo questRepo;

    @BeforeEach
    void setUp() {
        sessionCreator = NanoSpring.find(SessionCreator.class);
        session = sessionCreator.getSession();
        questRepo = new QuestRepo(sessionCreator);
    }

    @Test
    void getQueryWithParam() {
//        Transaction tx = session.beginTransaction();
//        Query<Quest> questQuery = session.createNamedQuery(Quest.ID_MORE, Quest.class);
//        questQuery.setParameter("id", 1);
//        questQuery.list().forEach(System.out::println);
//        tx.rollback();
    }

    @Test
    void getQueryNativeSql() {
        Transaction tx = session.beginTransaction();
        Query<Quest> questQuery = session.createNativeQuery("SELECT * FROM public.quest", Quest.class);
        questQuery.list().forEach(System.out::println);
        tx.rollback();
    }
}