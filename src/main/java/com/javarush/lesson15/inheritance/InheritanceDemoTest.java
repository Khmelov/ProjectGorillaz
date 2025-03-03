package com.javarush.lesson15.inheritance;

import com.javarush.lesson15.inheritance.model.BasePerson;
import com.javarush.lesson15.inheritance.model.Customer;
import com.javarush.lesson15.inheritance.model.Seller;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class InheritanceDemoTest {

    @Test
    public void test() {
        Session session = InheritanceSessionFactory.SESSION.open();
        try (session) {
            Transaction tx = session.beginTransaction();
            BasePerson basePerson = BasePerson.builder()
                    .name("Base Person")
                    .email("base@person.com")
                    .build();
            session.persist(basePerson);

            Customer customer = Customer.builder()
                    .name("Customer Person")
                    .email("customer@person.com")
                    .firstName("First Name Customer")
                    .lastName("Last Name Customer")
                    .build();
            session.persist(customer);

            Seller seller = Seller.builder()
                    .name("Seller Name")
                    .email("seller@person.com")
                    .profit(123456d)
                    .build();
            session.persist(seller);

            tx.commit();
        }
    }

    @AfterEach
    public void afterEach() {
        InheritanceSessionFactory.SESSION.close();
    }
}

