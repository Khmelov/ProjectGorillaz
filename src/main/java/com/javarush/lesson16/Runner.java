package com.javarush.lesson16;

import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.dto.UserTo;

public class Runner {
    public static void main(String[] args) {
        TestSevice testSevice = NanoSpring.find(TestSevice.class);
        UserTo user = testSevice.get(1L);
        System.out.println(user);
    }
}
