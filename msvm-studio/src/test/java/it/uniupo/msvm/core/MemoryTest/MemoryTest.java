package it.uniupo.msvm.core.MemoryTest;

import it.uniupo.msvm.core.exceptions.MemoryAccessException;
import it.uniupo.msvm.core.memory.Memory;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test JUnit per il componente Memory (RAM).
 * <p>
 * Questa classe verifica il corretto funzionamento delle operazioni di lettura,
 * scrittura, gestione dei limiti (bounds checking) e caricamento dei programmi.
 * Include test per i "happy path" (funzionamento normale) e per gli "edge cases" (errori e limiti).
 * </p>
 */
@DisplayName("Test Suite: Componente RAM (Memory)")
public class MemoryTest {

    private Memory testMemory;
    private final int MEMORY_SIZE = 512;

    /**
     * Configurazione iniziale eseguita prima di ogni singolo test.
     * Inizializza una nuova istanza di Memory pulita per garantire isolamento tra i test.
     */
    @BeforeEach
    public void setUp() {
        testMemory = new Memory(MEMORY_SIZE);
        assertNotNull(testMemory, "L'istanza della memoria non è stata creata correttamente");
    }

    /**
     * Verifica le operazioni base di lettura e scrittura.
     * Controlla che i dati scritti vengano recuperati correttamente e che
     * le sovrascritture funzionino come previsto.
     */
    @Test
    @DisplayName("Test: Scrittura e Lettura (Happy Path)")
    public void testWriteAndReadMemory() throws Exception {
        // Test scrittura semplice
        testMemory.write(0, 10);
        assertEquals(10, testMemory.read(0), "Il valore all'indirizzo 0 dovrebbe essere 10");

        // Test scrittura su altro indirizzo
        testMemory.write(1, 20);
        assertEquals(20, testMemory.read(1), "Il valore all'indirizzo 1 dovrebbe essere 20");

        // Test sovrascrittura
        testMemory.write(1, 30);
        assertEquals(30, testMemory.read(1), "Il valore all'indirizzo 1 dovrebbe essere aggiornato a 30 dopo sovrascrittura");
    }

    /**
     * Verifica che la memoria lanci l'eccezione corretta quando si tenta di
     * LEGGERE da indirizzi non validi (negativi o fuori limite).
     */
    @Test
    @DisplayName("Test: Eccezioni su Lettura (Bounds Checking)")
    public void testExceptionRead() throws Exception {
        // Setup iniziale
        testMemory.write(0, 10);

        // Caso 1: Indirizzo negativo
        assertThrows(MemoryAccessException.class,
                () -> testMemory.read(-1),
                "Dovrebbe lanciare MemoryAccessException per indirizzo negativo (-1)");

        // Caso 2: Indirizzo uguale alla dimensione (Off-by-one error)
        // Se la size è 512, gli indici validi sono 0..511. Quindi 512 è fuori.
        assertThrows(MemoryAccessException.class,
                () -> testMemory.read(MEMORY_SIZE),
                "Dovrebbe lanciare MemoryAccessException per indirizzo uguale alla dimensione (Out of Bounds)");

        // Caso 3: Indirizzo molto oltre il limite
        assertThrows(MemoryAccessException.class,
                () -> testMemory.read(1024),
                "Dovrebbe lanciare MemoryAccessException per indirizzo 1024");
    }

    /**
     * Verifica che la memoria lanci l'eccezione corretta quando si tenta di
     * SCRIVERE su indirizzi non validi.
     */
    @Test
    @DisplayName("Test: Eccezioni su Scrittura (Bounds Checking)")
    public void testExceptionWrite() throws Exception {
        // Caso 1: Scrittura su indirizzo negativo
        assertThrows(MemoryAccessException.class,
                () -> testMemory.write(-1, 10),
                "Dovrebbe lanciare eccezione per scrittura su indirizzo negativo");

        // Caso 2: Scrittura fuori limite
        assertThrows(MemoryAccessException.class,
                () -> testMemory.write(MEMORY_SIZE, 10),
                "Dovrebbe lanciare eccezione per scrittura su indirizzo fuori limite");
    }

    /**
     * Verifica che il metodo sizeMemory() restituisca la dimensione corretta configurata nel costruttore.
     */
    @Test
    @DisplayName("Test: Dimensione Memoria (GetSize)")
    public void testGetSize() {
        assertEquals(MEMORY_SIZE, testMemory.sizeMemory(),
                "La dimensione della memoria restituita non corrisponde a quella configurata nel costruttore");
    }

    /**
     * Verifica la funzionalità di pulizia (Clear).
     * Scrive dati in memoria, chiama clear() e verifica che tutto sia tornato a 0.
     */
    @Test
    @DisplayName("Test: Reset della Memoria (Clear)")
    public void testClearMemory() throws MemoryAccessException {
        // Popoliamo la memoria
        for (int i = 0; i < MEMORY_SIZE; i++) {
            testMemory.write(i, i + 10);
        }

        // Verifichiamo che sia stata scritta
        assertEquals(10, testMemory.read(0), "Pre-condizione fallita: scrittura iniziale non riuscita");

        // Eseguiamo il reset
        testMemory.clear();

        // Verifichiamo che sia tutto a zero
        for (int i = 0; i < MEMORY_SIZE; i++) {
            assertEquals(0, testMemory.read(i), "La cella " + i + " dovrebbe essere 0 dopo il clear");
        }
    }

    /**
     * Verifica il caricamento di un programma valido (array di int) in memoria.
     */
    @Test
    @DisplayName("Test: Caricamento Programma (LoadProgram)")
    public void testLoadProgram() throws MemoryAccessException {
        int[] program = new int[]{1, 2, 3, 4};

        testMemory.loadProgram(program);

        // Verifica che i dati siano stati copiati correttamente agli indici giusti
        for (int i = 0; i < program.length; i++) {
            assertEquals(program[i], testMemory.read(i), "Il dato all'indirizzo " + i + " non corrisponde al programma caricato");
        }
    }

    /**
     * Verifica il caso limite: Caricamento di un programma che riempie ESATTAMENTE tutta la memoria.
     * Nota: Questo test serve a verificare che non ci siano errori "Off-by-one" nel controllo della dimensione.
     */
    @Test
    @DisplayName("Test: Caricamento Programma Full Size (Edge Case)")
    public void testLoadFullSizeProgram() throws MemoryAccessException {
        int[] fullProgram = new int[MEMORY_SIZE];
        Arrays.fill(fullProgram, 99); // Riempiamo con un valore dummy

        // Non deve lanciare eccezioni se il programma è grande ESATTAMENTE come la memoria
        assertDoesNotThrow(() -> testMemory.loadProgram(fullProgram),
                "Dovrebbe essere possibile caricare un programma grande esattamente quanto la RAM");

        // Verifica l'ultimo byte
        assertEquals(99, testMemory.read(MEMORY_SIZE - 1), "L'ultimo byte della memoria dovrebbe essere stato scritto");
    }

    /**
     * Verifica che venga lanciata un'eccezione se si prova a caricare un programma
     * più grande della dimensione disponibile della RAM.
     */
    @Test
    @DisplayName("Test: Eccezione Caricamento Programma Troppo Grande")
    public void testLoadProgramException() {
        int[] hugeProgram = new int[1024]; // Molto più grande di 512
        Arrays.fill(hugeProgram, 1);

        assertThrows(MemoryAccessException.class,
                () -> testMemory.loadProgram(hugeProgram),
                "Il caricamento di un programma più grande della RAM deve lanciare MemoryAccessException");
    }
}