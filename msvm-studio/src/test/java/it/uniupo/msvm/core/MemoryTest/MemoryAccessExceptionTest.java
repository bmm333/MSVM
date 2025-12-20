package it.uniupo.msvm.core.MemoryTest;
import it.uniupo.msvm.core.exceptions.MemoryAccessException;
import it.uniupo.msvm.core.memory.*;
import it.uniupo.msvm.core.memory.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test Suite: MemoryAccessException")
class MemoryAccessExceptionTest {

    @Test
    @DisplayName("Test: Costruttore Automatico (Formatta Messaggio)")
    void testConstructorWithAutoMessage() {
        int address = 255; // 0x00FF in esadecimale
        MemoryAccessException.AccessType type = MemoryAccessException.AccessType.READ;

        // Creiamo l'eccezione
        MemoryAccessException exception = new MemoryAccessException(address, type);

        // 1. Verifichiamo che i campi siano stati settati
        assertEquals(address, exception.getAddress(), "L'indirizzo salvato non è corretto");
        assertEquals(type, exception.getType(), "Il tipo di accesso salvato non è corretto");

        // 2. Verifichiamo la logica di formattazione del messaggio (String.format)
        String message = exception.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("Segmentation Fault"), "Il messaggio dovrebbe contenere 'Segmentation Fault'");
        assertTrue(message.contains("READ"), "Il messaggio dovrebbe contenere il tipo di accesso");

        // Verifica cruciale: controlliamo che 255 sia stato convertito in 00FF
        // Il formato era: [0x%04X]
        assertTrue(message.contains("00FF"), "Il messaggio dovrebbe contenere l'indirizzo in esadecimale (00FF)");
        assertTrue(message.contains("255"), "Il messaggio dovrebbe contenere l'indirizzo in decimale");
    }

    @Test
    @DisplayName("Test: Costruttore con Messaggio Custom")
    void testConstructorWithCustomMessage() {
        String customMsg = "Errore critico personalizzato";
        int address = 100;
        MemoryAccessException.AccessType type = MemoryAccessException.AccessType.WRITE;

        MemoryAccessException exception = new MemoryAccessException(customMsg, address, type);

        // Verifiche
        assertEquals(customMsg, exception.getMessage(), "Il messaggio custom non è stato preservato");
        assertEquals(address, exception.getAddress());
        assertEquals(type, exception.getType());
    }

    @Test
    @DisplayName("Test: Enumerazione AccessType")
    void testAccessTypeEnum() {
        // Test necessario per avere il 100% di coverage sull'Enum (metodi values() e valueOf())

        // Test valueOf
        assertEquals(MemoryAccessException.AccessType.READ, MemoryAccessException.AccessType.valueOf("READ"));
        assertEquals(MemoryAccessException.AccessType.WRITE, MemoryAccessException.AccessType.valueOf("WRITE"));
        assertEquals(MemoryAccessException.AccessType.EXECUTE, MemoryAccessException.AccessType.valueOf("EXECUTE"));

        // Test values (controlliamo che ci siano tutti e 3)
        MemoryAccessException.AccessType[] types = MemoryAccessException.AccessType.values();
        assertEquals(3, types.length, "L'enum AccessType dovrebbe avere 3 valori");
    }
}