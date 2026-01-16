package it.uniupo.msvm.server.persistence.dao;

import it.uniupo.msvm.common.model.User;
import it.uniupo.msvm.server.persistence.DatabaseManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di integrazione per {@link UserDAO}.
 * Utilizza il DatabaseManager reale (che punta a SQLite).
 * NOTA: In un ambiente reale si userebbe un database H2 in memoria o un database di test separato.
 */
@DisplayName("Test DAO: UserDAO")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDAOTest {

    private static UserDAO userDAO;
    private static DatabaseManager dbManager;

    @BeforeAll
    static void setup() {
        dbManager = DatabaseManager.getInstance();
        dbManager.initialize();
        userDAO = new UserDAO();
    }

    @BeforeEach
    void cleanDatabase() throws SQLException {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users");
        }
    }

    @Test
    @Order(1)
    @DisplayName("Inserimento di un nuovo utente")
    void testInsertUser() throws SQLException {
        User user = new User(null, "testuser", "password123", "test@example.com");
        boolean success = userDAO.insertNewUser(user);
        
        assertTrue(success, "L'inserimento dell'utente dovrebbe avere successo");
        
        Optional<User> found = userDAO.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    @Order(2)
    @DisplayName("Ricerca utente per username")
    void testFindByUsername() throws SQLException {
        User user = new User(null, "searchMe", "pass", "search@test.com");
        userDAO.insertNewUser(user);
        
        User found = userDAO.findByUsername("searchMe");
        assertNotNull(found);
        assertEquals("search@test.com", found.getEmail());
    }

    @Test
    @Order(3)
    @DisplayName("Aggiornamento utente")
    void testUpdateUser() throws SQLException {
        User user = new User(null, "oldName", "pass", "update@test.com");
        userDAO.insertNewUser(user);
        
        User savedUser = userDAO.findByUsername("oldName");
        savedUser.setUsername("newName");
        
        boolean success = userDAO.updateUser(savedUser);
        assertTrue(success);
        
        User updatedUser = userDAO.findById(savedUser.getId());
        assertEquals("newName", updatedUser.getUsername());
    }

    @Test
    @Order(4)
    @DisplayName("Cancellazione utente")
    void testDeleteUser() throws SQLException {
        User user = new User(null, "deleteMe", "pass", "delete@test.com");
        userDAO.insertNewUser(user);
        
        User savedUser = userDAO.findByUsername("deleteMe");
        boolean success = userDAO.deleteUser(savedUser.getId());
        
        assertTrue(success);
        assertNull(userDAO.findById(savedUser.getId()));
    }

    @Test
    @Order(5)
    @DisplayName("Verifica esistenza username ed email")
    void testExists() throws SQLException {
        User user = new User(null, "uniqueUser", "pass", "unique@test.com");
        userDAO.insertNewUser(user);
        
        assertTrue(userDAO.existsByUsername("uniqueUser"));
        assertTrue(userDAO.existsByEmail("unique@test.com"));
        assertFalse(userDAO.existsByUsername("nonExistent"));
    }
}
