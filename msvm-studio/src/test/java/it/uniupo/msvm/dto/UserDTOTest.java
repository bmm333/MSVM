package it.uniupo.msvm.dto;

import it.uniupo.msvm.common.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per {@link UserDTO}.
 * <p>
 * Verifica il corretto trasporto dei dati utente non sensibili (senza password).
 * </p>
 */
@DisplayName("Test Suite: UserDTO (DTO Sicuro)")
class UserDTOTest {

    /**
     * Verifica i metodi di accesso base.
     */
    @Test
    @DisplayName("Test Costruttore Vuoto e Setter/Getter")
    void testNoArgsConstructorAndSetters() {
        // Arrange
        UserDTO dto = new UserDTO();

        // Act
        dto.setId(100L);
        dto.setUsername("clientUser");
        dto.setEmail("client@test.it");

        // Assert
        assertEquals(100L, dto.getId());
        assertEquals("clientUser", dto.getUsername());
        assertEquals("client@test.it", dto.getEmail());
    }

    /**
     * Verifica il costruttore parametrizzato.
     */
    @Test
    @DisplayName("Test Costruttore Completo")
    void testAllArgsConstructor() {
        // Act
        UserDTO dto = new UserDTO(200L, "serverUser", "server@test.it");

        // Assert
        assertAll("Verifica campi costruttore",
                () -> assertEquals(200L, dto.getId()),
                () -> assertEquals("serverUser", dto.getUsername()),
                () -> assertEquals("server@test.it", dto.getEmail())
        );
    }

    /**
     * Verifica che il toString fornisca una rappresentazione leggibile dei dati pubblici.
     */
    @Test
    @DisplayName("Test output metodo toString()")
    void testToString() {
        // Arrange
        UserDTO dto = new UserDTO(300L, "printMe", "print@test.it");

        // Act
        String s = dto.toString();

        // Assert
        assertTrue(s.contains("300") && s.contains("printMe") && s.contains("print@test.it"),
                "Il toString deve contenere ID, Username ed Email");
    }
}