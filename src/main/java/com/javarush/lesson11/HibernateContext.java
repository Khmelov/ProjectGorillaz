package com.javarush.lesson11;

import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.dto.Role;
import com.javarush.khmelov.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.stat.SessionStatistics;

public class HibernateContext {

    public static void main(String[] args) {
        SessionCreator sessionCreator = NanoSpring.find(SessionCreator.class);
        Session session = sessionCreator.getSession();
        Transaction tx = session.beginTransaction();

        try (sessionCreator; session) {
            show(session);
            User user1 = session.find(User.class, 1);
            show(session);
            User user2 = session.find(User.class, 1);
            User newUser = User.builder().login("test").password("test").role(Role.GUEST).build();
            session.save(newUser);
            show(session);
            session.remove(newUser);
            show(session);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }

    }

    private static void show(Session session) {
        String name = session.toString();
        SessionStatistics statistics = session.getStatistics();
        boolean isDirty = session.isDirty();
        String line = "-".repeat(50);
        System.out.printf("%s%nname: %s%nisDirty=%s%nstat=%s%n%s%n%n ",
                line, name, isDirty, statistics, line);
    }
}
