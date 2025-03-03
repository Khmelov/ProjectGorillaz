package com.javarush.lesson14;

import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.entity.Quest;
import com.javarush.khmelov.entity.Question;
import com.javarush.khmelov.entity.User;
import com.javarush.lesson13.AssociationDemo;
import jakarta.persistence.Subgraph;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

public class PerformanceDemo {

    static {
        AssociationDemo.init(); //ON THE STATEMENT if DB not found
    }

    private SessionCreator sessionCreator;
    private Session session;
    private Transaction tx;

    @BeforeEach
    void setUp() {
        sessionCreator = NanoSpring.find(SessionCreator.class);
        session = sessionCreator.getSession();
        tx = session.beginTransaction();
    }

    @Test
    void nPlusOneProblem() {
        //run without tuning @Fetch or @Batch
        Query<Quest> allQuestQuery = session.createQuery("SELECT q FROM Quest q", Quest.class);
        List<Quest> list = allQuestQuery.list();
        for (Quest quest : list) {
            System.out.println(quest.getName());
            for (Question question : quest.getQuestions()) {
                System.out.println("\t" + question.getText() + " | count answers = " + question.getAnswers().size());
            }
        }
    }

    @Test
    void getQuestsWithFetchOrBath() {
        //run with @Fetch or @Batch(size=3)
        User admin = session.find(User.class, 1L);
        Collection<Quest> quests = admin.getQuests();
        for (Quest quest : quests) {
            System.out.println(quest);
            for (Question question : quest.getQuestions()) {
                System.out.println("\t" + question);
            }
        }
    }

    @Test
    void getQuestsWithFetchHql() {
        //run without tuning @Fetch or @Batch
        String queryString = "SELECT DISTINCT q FROM Quest q " +
                             "LEFT JOIN FETCH q.questions qs "; //delete FETCH for lazy load
        Collection<Quest> quests = session.createQuery(queryString, Quest.class).list();
        for (Quest quest : quests) {
            System.out.println(quest);
            for (Question question : quest.getQuestions()) {
                System.out.println("\t" + question);
            }
        }
    }

    @Test
    void getQuestsWithFetchProfile() {
        session.enableFetchProfile(Quest.LAZY_QUESTIONS_AND_JOIN_AUTHOR_FETCH);//comment for test
        User admin = session.find(User.class, 1L);
        Collection<Quest> quests = admin.getQuests();
        for (Quest quest : quests) {
            System.out.println(quest);
            for (Question question : quest.getQuestions()) {
                System.out.println("\t" + question);
            }
        }
    }

    @Test
    void getQuestsWithEntityGraph() {
        Query<Quest> questQuery = session.createQuery("SELECT q from Quest q", Quest.class);
        String hintName = GraphSemantic.FETCH.getJakartaHintName();
        RootGraph<?> annoGraph = session.createEntityGraph(Quest.GRAPH_QUEST_QUESTIONS_AUTHOR_FETCH);
        questQuery.setHint(hintName, annoGraph);//comment for test
        Collection<Quest> quests = questQuery.list();
        for (Quest quest : quests) {
            System.out.println(quest);
            for (Question question : quest.getQuestions()) {
                System.out.println("\t" + question);
            }
        }
    }

    @Test
    void getQuestsWithCodeEntityGraph() {

        Query<Quest> questQuery = session.createQuery("SELECT q from Quest q", Quest.class);
        String hintName = GraphSemantic.FETCH.getJakartaHintName();

        RootGraph<Quest> codeGraph = session.createEntityGraph(Quest.class);
        codeGraph.addAttributeNodes("questions", "author");
        Subgraph<User> userSubgraph = codeGraph.addSubgraph("author", User.class);
        userSubgraph.addAttributeNodes("userInfo");

        questQuery.setHint(hintName, codeGraph);//comment for test
        Collection<Quest> quests = questQuery.list();
        for (Quest quest : quests) {
            System.out.println(quest);
            for (Question question : quest.getQuestions()) {
                System.out.println("\t" + question);
            }
        }
    }


    @AfterEach
    void tearDown() {
        try {
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
        session.close();
    }
}
