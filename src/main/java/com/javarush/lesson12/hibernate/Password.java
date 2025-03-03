package com.javarush.lesson12.hibernate;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Password {

    private String password;

    @Override
    public String toString() {
        return password;
    }
}
