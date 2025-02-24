package com.javarush.lesson18;

import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.entity.Game;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class C_NonRepeteableReadLastCommitWins {
    public static void main(String[] args) {
        SessionCreator sessionCreator = NanoSpring.find(SessionCreator.class);
        Session session1 = sessionCreator.getSession();
        Session session2 = sessionCreator.getSession();
        Transaction tx1 = session1.beginTransaction();
        Transaction tx2 = session2.beginTransaction();
        Game game1 = session1.find(Game.class, 1L);
        Game game2 = session2.find(Game.class, 1L);
        System.out.println("1."+game1);
        game1.setCurrentQuestionId(111L);
        game2.setCurrentQuestionId(222L);
        tx1.commit();
        tx2.commit();

        System.out.println("3."+sessionCreator.getSession().find(Game.class, 1L));


    }
}
