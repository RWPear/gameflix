package com.gameflix.gameflix.service;

import com.gameflix.gameflix.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void usernameExists_ShouldReturnFalse_WhenUserNotCreated() {
        boolean exists = userService.usernameExists("notreal");
        Assertions.assertFalse(exists);
    }

    @Test
    void register_ShouldCreateUser() {
        User u = userService.register("testuser", "password123");

        Assertions.assertNotNull(u);
        Assertions.assertNotNull(u.getId());
        Assertions.assertEquals("testuser", u.getUsername());
        Assertions.assertNotNull(u.getPasswordHash());
    }

    @Test
    void login_ShouldReturnTrue_WhenCorrectPassword() {
        userService.register("bob", "secret123");

        boolean result = userService.login("bob", "secret123");

        Assertions.assertTrue(result);
    }
}
