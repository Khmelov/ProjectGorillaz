package com.javarush.lesson18;

import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.dto.GameState;
import com.javarush.khmelov.entity.Game;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class C_NonRepeteableRead {
    public static void main(String[] args) {
        SessionCreator sessionCreator = NanoSpring.find(SessionCreator.class);
        //init
        Session session = sessionCreator.getSession();
        Transaction tx = session.beginTransaction();
        session.find(Game.class,1).setGameState(GameState.PLAY);
        tx.commit();
        session.close();

        Session session1 = sessionCreator.getSession();
            Session session2 = sessionCreator.getSession();
        Transaction tx1 = session1.beginTransaction();
            Transaction tx2 = session2.beginTransaction();
        Game game1 = session1.find(Game.class, 1L);
            Game game2 = session2.find(Game.class, 1L);
        System.out.println("1."+game1);
            System.out.println("2."+game2);
            game2.setGameState(GameState.LOST);
            tx2.commit();
        //
        session1.refresh(game1);
        Game gameAfterUpdate = session1.find(Game.class, 1L);
        System.out.println("1.2."+gameAfterUpdate);

        System.out.println("1.3."+sessionCreator.getSession().find(Game.class, 1L));


    }
}
