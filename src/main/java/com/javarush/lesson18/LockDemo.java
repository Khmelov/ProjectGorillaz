package com.javarush.lesson18;

import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.entity.Game;
import jakarta.persistence.LockModeType;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class LockDemo {
    public static void main(String[] args) {
        //Reset field
        SessionCreator sessionCreator = NanoSpring.find(SessionCreator.class);
        Session session = sessionCreator.getSession();
        Transaction tx = session.beginTransaction();
        session.find(Game.class, 1L).setCurrentQuestionId(123L);
        tx.commit();
        //Create two sessions and start tx1, tx2
        Session session1 = sessionCreator.getSession(); //model thread1
            Session session2 = sessionCreator.getSession(); //model thread2
        Transaction tx1 = session1.beginTransaction();
            Transaction tx2 = session2.beginTransaction();

        Game game1 = session1.find(Game.class, 1L, LockModeType.PESSIMISTIC_WRITE);
            Game game2 = session2.find(Game.class, 1L, LockModeType.PESSIMISTIC_WRITE);
        System.out.println("1."+game1);
            System.out.println("2."+game2);
        game1.setCurrentQuestionId(1111L);
        session1.persist(game1);
        System.out.println("1."+game1);
        tx1.commit(); //Ok
        game2.setCurrentQuestionId(2222L);
        session2.persist(game2);
        System.out.println("2."+game2);
        tx2.commit(); //throw OptimisticLockException

        System.out.println("DB."+sessionCreator.getSession().find(Game.class, 1L)); //tx1 OK

    }
}
