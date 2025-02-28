package com.javarush.lesson13;

import com.javarush.khmelov.config.ApplicationProperties;
import com.javarush.khmelov.config.Config;
import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.entity.Quest;
import com.javarush.khmelov.entity.Question;
import com.javarush.khmelov.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.lang.reflect.Proxy;
import java.util.Collection;

public class AssociationDemo {

    public static void main(String[] args) {
        SessionCreator sessionCreator = new SessionCreator(new ApplicationProperties());
        Session session = sessionCreator.getSession();
        Transaction tx = session.beginTransaction();
        try (session; sessionCreator) {
            Quest quest = session.get(Quest.class, 1L);
            System.out.println(quest);
            System.out.println("=".repeat(100));
            System.out.println("Many to One, Author=" + quest.getAuthor());

            System.out.println("=".repeat(100));
            User user = session.get(User.class, 1L);
            System.out.println("One to Many, Quests size=" + user.getQuests().size());
            Collection<Question> questions = quest.getQuestions();
            System.out.println("One to Many, Questions size=" + questions.size());
            Question oneQuestion = questions.stream().findFirst().orElseThrow();
            System.out.println("One to Many, Answer size=" + oneQuestion.getAnswers().size());

            System.out.println("=".repeat(100));
            //System.out.println("One to One, UserInfo=" + user.getUserInfo());

            System.out.println("=".repeat(100));
            System.out.println("Many to Many, User play Quests (In Game)=" + user.getQuestsInGame());
            System.out.println("Many to Many, Quest Players (In Game)=" + quest.getUsers());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
    }

    static {
        AssociationDemo.init();
    }

    public static void init() {
        //stupid init (like tomcat without NanoSpring)
        SessionCreator sessionCreator = magicSessionCreator();
        try (sessionCreator) {
            Config config = NanoSpring.find(Config.class);
            config.fillEmptyRepository();
        }
    }

    private static SessionCreator magicSessionCreator() {
        //stupid Transactional Mode
        return new SessionCreator(new ApplicationProperties()) {
            private Session magicSession;
            private Session session;

            @Override
            public synchronized Session getSession() {
                if (magicSession == null) {
                    session = super.getSession();
                    magicSession = (Session) Proxy.newProxyInstance(
                            Session.class.getClassLoader(),
                            new Class[]{Session.class},
                            (proxy, method, args) -> {
                                if (!method.getName().equals("close")) {
                                    return method.invoke(session, args);
                                } else {
                                    System.out.println("Skip Method: " + method.getName());
                                    return null;
                                }
                            });
                }
                return magicSession;
            }

            @Override
            public void close() {
                super.close();
            }
        };
    }
}
