package com.javarush.khmelov.cmd;

import com.javarush.khmelov.BaseIT;
import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.dto.Role;
import com.javarush.khmelov.dto.UserTo;
import com.javarush.khmelov.service.UserService;
import com.javarush.khmelov.util.Key;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

class EditUserIT extends BaseIT {

    private final EditUser editUser = NanoSpring.find(EditUser.class);
    private final UserService userService = NanoSpring.find(UserService.class);

    @Test
    void whenOpenPage_thenCommandReturnJspPage() {
        UserTo user = userService.getAll().stream().findFirst().orElseThrow();
        Mockito.when(request.getParameter(Key.ID)).thenReturn(user.getId().toString());
        String view = editUser.doGet(request);
        assertEquals("edit-user", view);
        verify(request).setAttribute(eq(Key.USER), eq(user));
    }

    @Test
    void whenUpdateUser_thenGetPageByUserId() throws Exception {
        Mockito.when(request.getParameter(Key.LOGIN)).thenReturn("TestName");
        Mockito.when(request.getParameter(Key.PASSWORD)).thenReturn("TestPassword");
        Mockito.when(request.getParameter(Key.ROLE)).thenReturn(Role.GUEST.toString());
        Mockito.when(request.getParameter(Key.ID)).thenReturn("3");
        String page = editUser.doPost(request);
        assertTrue(page.endsWith("?id=3"));
    }
}