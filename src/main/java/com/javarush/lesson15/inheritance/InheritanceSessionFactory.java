package com.javarush.lesson15.inheritance;

import com.javarush.lesson15.inheritance.model.BasePerson;
import com.javarush.lesson15.inheritance.model.Customer;
import com.javarush.lesson15.inheritance.model.Seller;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

enum InheritanceSessionFactory {

    SESSION;

    private Session session;

    Session open() {
        session = sessionFactory.openSession();
        return session;
    }

    void close() {
        session.close();
    }

    void closeFactory() {
        sessionFactory.close();
    }

    private final SessionFactory sessionFactory;

    InheritanceSessionFactory() {
        LogManager logManager = LogManager.getLogManager();
        Logger logger = logManager.getLogger("");
        logger.setLevel(Level.WARNING);

        Configuration configure = new Configuration()
                .setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/game")
                .setProperty("hibernate.connection.username", "postgres")
                .setProperty("hibernate.connection.password", "postgres")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.format_sql", "true")
                .setProperty("hibernate.hbm2ddl.auto", "create");

        configure.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
        configure.addAnnotatedClass(BasePerson.class);
        configure.addAnnotatedClass(Customer.class);
        configure.addAnnotatedClass(Seller.class);
        sessionFactory = configure.buildSessionFactory();
    }
}
