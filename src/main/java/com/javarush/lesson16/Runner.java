package com.javarush.lesson16;

import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.entity.User;

public class Runner {
    public static void main(String[] args) {
        TestSevice testSevice = NanoSpring.find(TestSevice.class);
        User user = testSevice.get(1l);
        System.out.println(user);
    }
}
