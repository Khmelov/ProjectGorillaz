package com.javarush.lesson12.hibernate;

import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.config.SessionCreator;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ClientForTypeDemo {
    public static void main(String[] args) {
        SessionCreator sessionCreator = NanoSpring.find(SessionCreator.class);
        Session session = sessionCreator.getSession();
        Transaction tx = session.beginTransaction();
        try (sessionCreator; session) {
            Person person = session.find(Person.class, 1);
            System.out.println(person);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        }
    }
}
