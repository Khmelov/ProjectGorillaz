package com.javarush.lesson15.cache2;

import com.javarush.khmelov.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class CacheLevel2Test {

    static {
        //AssociationDemo.init(); //ON THE STATEMENT if DB not found
    }

    @Test
    public void test() {
        for (int i = 0; i < 10; i++) {
            Session session = CacheDemoSessionFactory.SESSION.open();
            try (session) {
                Transaction tx = session.beginTransaction();
                Object o = session.find(User.class, 1L);
                System.out.println(o);
                tx.commit();
            }
        }
    }

    @AfterEach
    public void after() {
        //CacheDemoSessionFactory.SESSION.closeFactory();
    }


}