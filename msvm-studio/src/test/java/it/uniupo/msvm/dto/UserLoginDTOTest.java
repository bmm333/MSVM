package it.uniupo.msvm.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import it.uniupo.msvm.common.dto.*;

/**
 * Classe di test per {@link UserLoginDTO}.
 * <p>
 * Verifica il corretto funzionamento dei costruttori, dei metodi di accesso (getter/setter)
 * e la gestione della sicurezza nella rappresentazione a stringa.
 * </p>
 */
@DisplayName("Test Suite: UserLoginDTO (DTO Autenticazione)")
class UserLoginDTOTest {

    /**
     * Verifica che il costruttore vuoto permetta l'istanziazione e che i setter
     * impostino correttamente i valori recuperabili tramite i getter.
     */
    @Test
    @DisplayName("Test Costruttore Vuoto e Setter/Getter")
    void testNoArgsConstructorAndSetters() {
        // Arrange
        UserLoginDTO dto = new UserLoginDTO();
        String expectedUsername = "mario.rossi";
        String expectedPassword = "secret123";

        // Act
        dto.setUsername(expectedUsername);
        dto.setPassword(expectedPassword);

        // Assert
        assertEquals(expectedUsername, dto.getUsername(), "Lo username non corrisponde a quello impostato.");
        assertEquals(expectedPassword, dto.getPassword(), "La password non corrisponde a quella impostata.");
    }

    /**
     * Verifica che il costruttore completo inizializzi correttamente tutti i campi.
     */
    @Test
    @DisplayName("Test Costruttore Completo")
    void testAllArgsConstructor() {
        // Arrange
        String expectedUsername = "luigi.verdi";
        String expectedPassword = "pass456";

        // Act
        UserLoginDTO dto = new UserLoginDTO(expectedUsername, expectedPassword);

        // Assert
        assertEquals(expectedUsername, dto.getUsername(), "Lo username inizializzato dal costruttore non è corretto.");
        assertEquals(expectedPassword, dto.getPassword(), "La password inizializzata dal costruttore non è corretta.");
    }

    /**
     * Verifica che il metodo toString() oscuri la password per motivi di sicurezza.
     * <p>
     * Come specificato nel file sorgente, la password non deve apparire nei log.
     * </p>
     */
    @Test
    @DisplayName("Test Sicurezza: toString() non deve mostrare la password")
    void testToStringSecurity() {
        // Arrange
        String username = "admin";
        String sensitivePassword = "superSecretPassword";
        UserLoginDTO dto = new UserLoginDTO(username, sensitivePassword);

        // Act
        String stringRepresentation = dto.toString();

        // Assert
        assertNotNull(stringRepresentation, "Il metodo toString non deve restituire null.");
        assertTrue(stringRepresentation.contains(username), "Il toString dovrebbe contenere lo username.");
        assertFalse(stringRepresentation.contains(sensitivePassword), "ERRORE SICUREZZA: La password in chiaro è visibile nel toString!");
    }
}