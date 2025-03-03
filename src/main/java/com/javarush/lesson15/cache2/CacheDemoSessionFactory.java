package com.javarush.lesson15.cache2;

import com.javarush.khmelov.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

enum CacheDemoSessionFactory {

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

    CacheDemoSessionFactory() {
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
                //.setProperty("hibernate.hbm2ddl.auto", "create")

                //@see https://howtodoinjava.com/hibernate/configuring-ehcache-3-with-hibernate-6/
                .setProperty("hibernate.javax.cache.uri", "/ehcache.xml") //config
                .setProperty("hibernate.cache.region.factory_class", "jcache")
                .setProperty("hibernate.javax.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider")
                .setProperty("hibernate.cache.use_second_level_cache", "true")
                .setProperty("hibernate.javax.cache.missing_cache_strategy", "fail")
                //.setProperty("hibernate.cache.use_query_cache", "true")
                .setProperty("hibernate.generate_statistics", "false");
        //.configure(); //skip hibernate.cfg.xml

        configure.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
        configure.addAnnotatedClass(User.class);
        configure.addAnnotatedClass(Quest.class);
        configure.addAnnotatedClass(Question.class);
        configure.addAnnotatedClass(Answer.class);
        configure.addAnnotatedClass(Game.class);
        configure.addAnnotatedClass(UserInfo.class);
        sessionFactory = configure.buildSessionFactory();
    }
}
