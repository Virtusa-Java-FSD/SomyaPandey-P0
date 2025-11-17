package org.example.service;

import org.example.dao.UserDAO;
import org.example.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private MockedConstruction<UserDAO> userDaoConstruction;
    private MockedStatic<BCrypt> bCryptStatic;

    @AfterEach
    void tearDown() {
        if (userDaoConstruction != null) userDaoConstruction.close();
        if (bCryptStatic != null) bCryptStatic.close();
    }

    @Test
    void register_success_whenEmailAndUsernameAvailable() throws Exception {
        userDaoConstruction = mockConstruction(UserDAO.class, (mock, ctx) -> {
            when(mock.findByEmail("a@b.com")).thenReturn(null);
            when(mock.findByUsername("alice")).thenReturn(null);
            when(mock.save(any(User.class))).thenReturn(true);
        });
        bCryptStatic = mockStatic(BCrypt.class);
        bCryptStatic.when(() -> BCrypt.gensalt()).thenReturn("salt");
        bCryptStatic.when(() -> BCrypt.hashpw("secret", "salt")).thenReturn("hash");

        UserService svc = new UserService();
        User u = new User();
        u.setEmail("a@b.com");
        u.setUsername("alice");

        boolean ok = svc.register(u, "secret");
        assertTrue(ok);
        assertEquals("hash", u.getPasswordHash());
        // Verify save called once
        UserDAO mockDao = userDaoConstruction.constructed().get(0);
        verify(mockDao, times(1)).save(any(User.class));
    }

    @Test
    void register_fails_whenEmailExists() throws Exception {
        userDaoConstruction = mockConstruction(UserDAO.class, (mock, ctx) -> {
            when(mock.findByEmail("a@b.com")).thenReturn(new User());
        });

        UserService svc = new UserService();
        User u = new User();
        u.setEmail("a@b.com");
        u.setUsername("alice");

        boolean ok = svc.register(u, "secret");
        assertFalse(ok);
        UserDAO mockDao = userDaoConstruction.constructed().get(0);
        verify(mockDao, never()).save(any());
    }

    @Test
    void register_fails_whenUsernameExists() throws Exception {
        userDaoConstruction = mockConstruction(UserDAO.class, (mock, ctx) -> {
            when(mock.findByEmail("a@b.com")).thenReturn(null);
            when(mock.findByUsername("alice")).thenReturn(new User());
        });

        UserService svc = new UserService();
        User u = new User();
        u.setEmail("a@b.com");
        u.setUsername("alice");

        boolean ok = svc.register(u, "secret");
        assertFalse(ok);
        UserDAO mockDao = userDaoConstruction.constructed().get(0);
        verify(mockDao, never()).save(any());
    }

    @Test
    void login_success_whenPasswordMatches() throws Exception {
        User dbUser = new User();
        dbUser.setPasswordHash("hash");

        userDaoConstruction = mockConstruction(UserDAO.class, (mock, ctx) -> {
            when(mock.findByEmail("a@b.com")).thenReturn(dbUser);
        });
        bCryptStatic = mockStatic(BCrypt.class);
        bCryptStatic.when(() -> BCrypt.checkpw("secret", "hash")).thenReturn(true);

        UserService svc = new UserService();
        User out = svc.login("a@b.com", "secret");
        assertNotNull(out);
        assertEquals(dbUser, out);
    }

    @Test
    void login_returnsNull_whenPasswordWrong() throws Exception {
        User dbUser = new User();
        dbUser.setPasswordHash("hash");

        userDaoConstruction = mockConstruction(UserDAO.class, (mock, ctx) -> {
            when(mock.findByEmail("a@b.com")).thenReturn(dbUser);
        });
        bCryptStatic = mockStatic(BCrypt.class);
        bCryptStatic.when(() -> BCrypt.checkpw("bad", "hash")).thenReturn(false);

        UserService svc = new UserService();
        User out = svc.login("a@b.com", "bad");
        assertNull(out);
    }

    @Test
    void getById_delegatesToDao() throws Exception {
        User dbUser = new User();
        userDaoConstruction = mockConstruction(UserDAO.class, (mock, ctx) -> {
            when(mock.findById(42)).thenReturn(dbUser);
        });

        UserService svc = new UserService();
        User out = svc.getById(42);
        assertEquals(dbUser, out);
    }
}
