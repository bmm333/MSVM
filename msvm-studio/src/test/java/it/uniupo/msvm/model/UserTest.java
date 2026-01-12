package it.uniupo.msvm.model;

import it.uniupo.msvm.common.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per l'entità {@link User}.
 * <p>
 * Verifica la persistenza dei dati nell'oggetto e la sicurezza della rappresentazione testuale.
 * </p>
 */
@DisplayName("Test Suite: User (Modello Utente)")
class UserTest {

    /**
     * Verifica il funzionamento dei Setter e dei Getter su un oggetto creato con costruttore vuoto.
     */
    @Test
    @DisplayName("Test Costruttore Vuoto e Setter/Getter")
    void testNoArgsConstructorAndSetters() {
        // Arrange
        User user = new User();
        Long expectedId = 10L;
        String expectedUsername = "testUser";
        String expectedPassword = "hashedPass";
        String expectedEmail = "test@example.com";

        // Act
        user.setId(expectedId);
        user.setUsername(expectedUsername);
        user.setPassword(expectedPassword);
        user.setEmail(expectedEmail);

        // Assert
        assertEquals(expectedId, user.getId());
        assertEquals(expectedUsername, user.getUsername());
        assertEquals(expectedPassword, user.getPassword());
        assertEquals(expectedEmail, user.getEmail());
    }

    /**
     * Verifica l'inizializzazione corretta tramite costruttore con parametri.
     */
    @Test
    @DisplayName("Test Costruttore Completo")
    void testAllArgsConstructor() {
        // Arrange
        Long id = 5L;
        String username = "jane.doe";
        String password = "hash123";
        String email = "jane@example.com";

        // Act
        User user = new User(id, username, password, email);

        // Assert
        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
    }

    /**
     * Verifica che la rappresentazione testuale dell'Utente non esponga la password.
     */
    @Test
    @DisplayName("Test Sicurezza: toString() non deve mostrare l'hash della password")
    void testToStringSecurity() {
        // Arrange
        User user = new User(1L, "admin", "secretHashXYZ", "admin@test.com");

        // Act
        String result = user.toString();

        // Assert
        assertTrue(result.contains("admin"), "Lo username deve essere presente.");
        assertTrue(result.contains("admin@test.com"), "L'email deve essere presente.");
        assertFalse(result.contains("secretHashXYZ"), "ERRORE SICUREZZA: L'hash della password è visibile!");
    }
}