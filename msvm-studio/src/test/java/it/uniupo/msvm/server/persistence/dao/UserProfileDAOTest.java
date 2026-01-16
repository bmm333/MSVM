package it.uniupo.msvm.server.persistence.dao;

import it.uniupo.msvm.common.model.User;
import it.uniupo.msvm.common.model.UserProfile;
import it.uniupo.msvm.server.persistence.DatabaseManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test DAO: UserProfileDAO")
class UserProfileDAOTest {

    private static UserProfileDAO profileDAO;
    private static UserDAO userDAO;
    private static DatabaseManager dbManager;
    private Long testUserId;

    @BeforeAll
    static void setup() {
        dbManager = DatabaseManager.getInstance();
        dbManager.initialize();
        profileDAO = new UserProfileDAO();
        userDAO = new UserDAO();
    }

    @BeforeEach
    void cleanAndPrepareUser() throws SQLException {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM user_profiles");
            stmt.execute("DELETE FROM users");
        }
        
        // Creiamo un utente per associare il profilo
        User user = new User(null, "profileOwner", "pass", "owner@test.com");
        userDAO.insertNewUser(user);
        testUserId = userDAO.findByUsername("profileOwner").getId();
    }

    @Test
    @DisplayName("Inserimento e ricerca profilo")
    void testInsertAndFind() throws SQLException {
        UserProfile profile = new UserProfile(testUserId, "Mario", "Rossi", "Bio di prova", "123456");
        boolean success = profileDAO.insertNewProfile(profile);
        
        assertTrue(success);
        
        UserProfile found = profileDAO.findByUserId(testUserId);
        assertNotNull(found);
        assertEquals("Mario", found.getFirstName());
        assertEquals("Rossi", found.getLastName());
    }

    @Test
    @DisplayName("Aggiornamento profilo")
    void testUpdate() throws SQLException {
        UserProfile profile = new UserProfile(testUserId, "Mario", "Rossi", "Bio", "123");
        profileDAO.insertNewProfile(profile);
        
        profile.setFirstName("Luigi");
        profile.setBio("Nuova bio");
        
        boolean success = profileDAO.updateProfile(profile);
        assertTrue(success);
        
        UserProfile updated = profileDAO.findByUserId(testUserId);
        assertEquals("Luigi", updated.getFirstName());
        assertEquals("Nuova bio", updated.getBio());
    }

    @Test
    @DisplayName("Verifica esistenza profilo")
    void testExists() throws SQLException {
        assertFalse(profileDAO.exists(testUserId));
        
        UserProfile profile = new UserProfile(testUserId, "A", "B", "C", "D");
        profileDAO.insertNewProfile(profile);
        
        assertTrue(profileDAO.exists(testUserId));
    }

    @Test
    @DisplayName("Cancellazione profilo")
    void testDelete() throws SQLException {
        UserProfile profile = new UserProfile(testUserId, "A", "B", "C", "D");
        profileDAO.insertNewProfile(profile);
        
        boolean success = profileDAO.deleteProfile(testUserId);
        assertTrue(success);
        assertNull(profileDAO.findByUserId(testUserId));
    }
}
