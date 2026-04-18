package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginItemTest {

    private LoginItem loginItem;

    @BeforeEach
    void setUp() {
        loginItem = new LoginItem();

        setField(loginItem, "loginHeaderText", "Welcome Back");
        setField(loginItem, "loginText", "Sign In");
        setField(loginItem, "loginIcon", "/content/dam/mvw/login-icon.png");
        setField(loginItem, "loginAlt", "Login Icon");
        setField(loginItem, "loginHeadline", "Member Login");
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetLoginHeaderText() {
        assertEquals("Welcome Back", loginItem.getLoginHeaderText());
    }

    @Test
    void testGetLoginText() {
        assertEquals("Sign In", loginItem.getLoginText());
    }

    @Test
    void testGetLoginIcon() {
        assertEquals("/content/dam/mvw/login-icon.png", loginItem.getLoginIcon());
    }

    @Test
    void testGetLoginAlt() {
        assertEquals("Login Icon", loginItem.getLoginAlt());
    }

    @Test
    void testGetLoginHeadline() {
        assertEquals("Member Login", loginItem.getLoginHeadline());
    }

    @Test
    void testLoginTextRegister() {
        setField(loginItem, "loginText", "Register");
        assertEquals("Register", loginItem.getLoginText());
    }

    @Test
    void testLoginHeadlineVariation() {
        setField(loginItem, "loginHeadline", "Owner Login");
        assertEquals("Owner Login", loginItem.getLoginHeadline());
    }

    @Test
    void testNullValues() {
        LoginItem emptyItem = new LoginItem();
        assertNull(emptyItem.getLoginHeaderText());
        assertNull(emptyItem.getLoginText());
        assertNull(emptyItem.getLoginIcon());
        assertNull(emptyItem.getLoginAlt());
        assertNull(emptyItem.getLoginHeadline());
    }
}
