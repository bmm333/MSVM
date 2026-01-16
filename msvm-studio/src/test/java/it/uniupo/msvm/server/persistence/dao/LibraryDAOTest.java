package it.uniupo.msvm.server.persistence.dao;

import it.uniupo.msvm.common.model.Library;
import it.uniupo.msvm.server.persistence.DatabaseManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test DAO: LibraryDAO")
class LibraryDAOTest {

    private static LibraryDAO libraryDAO;
    private static DatabaseManager dbManager;

    @BeforeAll
    static void setup() {
        dbManager = DatabaseManager.getInstance();
        dbManager.initialize();
        libraryDAO = new LibraryDAO();
    }

    @BeforeEach
    void cleanAndSeed() throws SQLException {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM libraries");
            stmt.execute("INSERT INTO libraries (name, content, description, version) VALUES ('testLib', 'PUSH 1\nHALT', 'Test Description', 1)");
        }
    }

    @Test
    @DisplayName("Ricerca libreria per nome")
    void testFindByName() {
        Optional<Library> lib = libraryDAO.findByName("testLib");
        assertTrue(lib.isPresent());
        assertEquals("PUSH 1\nHALT", lib.get().getContent());
    }

    @Test
    @DisplayName("Recupero tutti i nomi delle librerie")
    void testFindAllNames() {
        List<String> names = libraryDAO.findAllNames();
        assertFalse(names.isEmpty());
        assertTrue(names.contains("testLib"));
    }

    @Test
    @DisplayName("Libreria non esistente")
    void testNotFound() {
        Optional<Library> lib = libraryDAO.findByName("nonExistent");
        assertTrue(lib.isEmpty());
    }
}
