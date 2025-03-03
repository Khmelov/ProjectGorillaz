package com.javarush.khmelov.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserTo {
    Long id;
    String login;
    String password;
    Role role;

    public String getImage() {
        return "user-" + id;
    }
}