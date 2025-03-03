package com.javarush.lesson18;

import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.entity.Game;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class D_PhantomRead {
    public static void main(String[] args) {
        SessionCreator sessionCreator = NanoSpring.find(SessionCreator.class);
        Session session1 = sessionCreator.getSession();
        Session session2 = sessionCreator.getSession();
        Transaction tx1 = session1.beginTransaction();
        Transaction tx2 = session2.beginTransaction();

        System.out.println("Size1: " + session1
                .createQuery("SELECT g FROM Game g", Game.class).list().size()
        );

        Game game = session2.find(Game.class, 3L);
        session2.remove(game);
        tx2.commit();

        System.out.println("Size2: " + session1
                .createQuery("SELECT g FROM Game g", Game.class).list().size()
        );
        tx1.commit();
    }
}
