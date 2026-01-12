package it.uniupo.msvm.model;

import it.uniupo.msvm.common.model.UserProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per {@link UserProfile}.
 * <p>
 * Verifica la gestione dei dati anagrafici dell'utente.
 * </p>
 */
@DisplayName("Test Suite: UserProfile (Dettagli Anagrafici)")
class UserProfileTest {

    /**
     * Verifica i setter e getter per i dati anagrafici.
     */
    @Test
    @DisplayName("Test Costruttore Vuoto e Setter/Getter")
    void testNoArgsConstructorAndSetters() {
        // Arrange
        UserProfile profile = new UserProfile();

        // Act
        profile.setUserId(50L);
        profile.setFirstName("Anna");
        profile.setLastName("Bianchi");
        profile.setBio("Developer");
        profile.setPhoneNumber("333000000");

        // Assert
        assertEquals(50L, profile.getUserId());
        assertEquals("Anna", profile.getFirstName());
        assertEquals("Bianchi", profile.getLastName());
        assertEquals("Developer", profile.getBio());
        assertEquals("333000000", profile.getPhoneNumber());
    }

    /**
     * Verifica il costruttore completo.
     */
    @Test
    @DisplayName("Test Costruttore Completo")
    void testAllArgsConstructor() {
        // Act
        UserProfile profile = new UserProfile(60L, "Paolo", "Neri", "Manager", "333111222");

        // Assert
        assertEquals(60L, profile.getUserId());
        assertEquals("Paolo", profile.getFirstName());
        assertEquals("Neri", profile.getLastName());
        assertEquals("Manager", profile.getBio());
        assertEquals("333111222", profile.getPhoneNumber());
    }

    /**
     * Verifica che il toString contenga i dati essenziali (ID e Nome completo).
     */
    @Test
    @DisplayName("Test rappresentazione toString()")
    void testToString() {
        // Arrange
        UserProfile profile = new UserProfile(70L, "Test", "User", "Bio", "000");

        // Act
        String result = profile.toString();

        // Assert
        assertTrue(result.contains("70"));
        assertTrue(result.contains("Test User"));
    }
}