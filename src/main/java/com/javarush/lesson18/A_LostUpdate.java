package com.javarush.lesson18;

import com.javarush.khmelov.config.ApplicationProperties;
import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.dto.Role;
import com.javarush.khmelov.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class A_LostUpdate {
    public static void main(String[] args) {
        SessionCreator sessionCreator = new SessionCreator(new ApplicationProperties());

        Session session = sessionCreator.getSession();
        Transaction tx = session.beginTransaction();
        session.find(User.class,1L).setRole(Role.ADMIN);
        tx.commit();

        Session s1 = sessionCreator.getSession();
        Session s2 = sessionCreator.getSession();
        Transaction tx1 = s1.beginTransaction();
        Transaction tx2 = s2.beginTransaction();
        //LostUpdate
        User admin = s1.get(User.class, 1L);
        admin.setRole(Role.GUEST);
        User admin2 = s2.get(User.class, 1L);
        admin2.setRole(Role.USER);
        tx2.commit(); //LOST
        tx1.rollback();

        tx = session.beginTransaction();
        System.out.println(session.find(User.class, 1L));
        tx.commit();

    }
}
