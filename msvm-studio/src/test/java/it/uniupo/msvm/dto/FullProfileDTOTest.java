package it.uniupo.msvm.dto;

import it.uniupo.msvm.common.dto.FullProfileDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per {@link FullProfileDTO}.
 * <p>
 * Verifica l'aggregazione corretta dei dati di account e dei dati di profilo.
 * </p>
 */
@DisplayName("Test Suite: FullProfileDTO (Profilo Completo)")
class FullProfileDTOTest {

    /**
     * Verifica che tutti i campi (sia base che dettaglio) siano impostabili e recuperabili.
     */
    @Test
    @DisplayName("Test Setter e Getter per tutti i campi")
    void testNoArgsConstructorAndSetters() {
        // Arrange
        FullProfileDTO fp = new FullProfileDTO();

        // Act
        fp.setUserId(1L);
        fp.setUsername("mario");
        fp.setEmail("mario@email.com");
        fp.setFirstName("Mario");
        fp.setLastName("Rossi");
        fp.setBio("Hello World");
        fp.setPhoneNumber("123456789");

        // Assert
        assertAll("Verifica integrità dati",
                () -> assertEquals(1L, fp.getUserId()),
                () -> assertEquals("mario", fp.getUsername()),
                () -> assertEquals("mario@email.com", fp.getEmail()),
                () -> assertEquals("Mario", fp.getFirstName()),
                () -> assertEquals("Rossi", fp.getLastName()),
                () -> assertEquals("Hello World", fp.getBio()),
                () -> assertEquals("123456789", fp.getPhoneNumber())
        );
    }

    /**
     * Verifica l'inizializzazione massiva tramite costruttore.
     */
    @Test
    @DisplayName("Test Costruttore Completo")
    void testAllArgsConstructor() {
        // Act
        FullProfileDTO fp = new FullProfileDTO(
                2L, "luigi", "luigi@email.com",
                "Luigi", "Verdi", "Bio Luigi", "987654321"
        );

        // Assert
        assertEquals(2L, fp.getUserId());
        assertEquals("luigi", fp.getUsername());
        assertEquals("Luigi", fp.getFirstName());
        assertEquals("Verdi", fp.getLastName());
        assertEquals("Bio Luigi", fp.getBio());
        assertEquals("987654321", fp.getPhoneNumber());
    }

    /**
     * Verifica il formato stringa del DTO aggregato.
     */
    @Test
    @DisplayName("Test rappresentazione toString()")
    void testToString() {
        // Arrange
        FullProfileDTO fp = new FullProfileDTO();
        fp.setUserId(5L);
        fp.setUsername("user5");
        fp.setFirstName("Nome");
        fp.setLastName("Cognome");

        // Act
        String s = fp.toString();

        // Assert
        assertTrue(s.contains("user5"));
        assertTrue(s.contains("Nome") && s.contains("Cognome"));
    }
}