package com.javarush.lesson18;

import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.entity.Role;
import com.javarush.khmelov.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class B_DirtyRead {
    public static void main(String[] args) {
        SessionCreator sessionCreator = NanoSpring.find(SessionCreator.class);

        Session session = sessionCreator.getSession();
        Transaction tx = session.beginTransaction();
        session.find(User.class,1L).setRole(Role.ADMIN);
        tx.commit();

        Session s1 = sessionCreator.getSession();
        Session s2 = sessionCreator.getSession();

        Transaction tx1 = s1.beginTransaction();
        Transaction tx2 = s2.beginTransaction();
        User admin = s1.get(User.class, 1L);
        admin.setRole(Role.GUEST);
        s1.flush();
        User admin2 = s1.get(User.class, 1L);
        tx2.commit();
        tx1.rollback();
        System.out.println(admin2);
    }
}
