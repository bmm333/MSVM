package it.uniupo.msvm.core.impl;

import it.uniupo.msvm.core.instructions.impl.Jump;
import it.uniupo.msvm.core.instructions.impl.Jz;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test per le istruzioni di Controllo di Flusso (Salti e Branching).
 * <p>
 * Verifica la corretta manipolazione del Program Counter (IP) da parte delle istruzioni
 * di salto incondizionato (JUMP) e condizionato (JZ).
 * </p>
 *
 * @author Luca Lupi
 */
@DisplayName("Test Unità: Controllo Flusso (Jump/Branch)")
public class ControlFlowTest extends InstructionTestBase {

    /**
     * Testa l'istruzione JUMP con un offset positivo (Salto in avanti).
     * <p>
     * <strong>Scenario:</strong>
     * <ul>
     * <li>IP iniziale: 10</li>
     * <li>Argomento salto (mem[10]): +5</li>
     * </ul>
     * <strong>Calcolo atteso:</strong>
     * <ol>
     * <li>Fetch dell'argomento: IP avanza da 10 a 11.</li>
     * <li>Esecuzione JUMP: <code>(IP_corrente + 1) + offset</code></li>
     * <li>Risultato: <code>(11 + 1) + 5 = 17</code></li>
     * </ol>
     * </p>
     */
    @Test
    @DisplayName("JUMP - Salto Incondizionato in Avanti (+5)")
    void testJumpForward() {
        cpu.setIp(10);
        memory.write(10, 5); // Scriviamo l'offset di salto

        new Jump().execute(ctx);

        assertEquals(17, cpu.getIp(), "L'IP deve avanzare a 17 (11 + 1 + 5)");
    }

    /**
     * Testa l'istruzione JUMP con un offset negativo (Salto all'indietro / Loop).
     * <p>
     * <strong>Scenario:</strong>
     * <ul>
     * <li>IP iniziale: 20</li>
     * <li>Argomento salto (mem[20]): -5</li>
     * </ul>
     * <strong>Calcolo atteso:</strong>
     * <br>
     * <code>(21 + 1) - 5 = 17</code>
     * </p>
     */
    @Test
    @DisplayName("JUMP - Salto all'Indietro (Loop -5)")
    void testJumpBackward() {
        cpu.setIp(20);
        memory.write(20, -5); // Scriviamo un offset negativo

        new Jump().execute(ctx);

        assertEquals(17, cpu.getIp(), "L'IP dovrebbe essere tornato indietro a 17");
    }

    /**
     * Testa l'istruzione JZ (Jump if Zero) quando la condizione è VERA.
     * <p>
     * Poiché nello stack c'è 0, il salto DEVE essere eseguito.
     * Il comportamento atteso è identico a un JUMP normale.
     * </p>
     */
    @Test
    @DisplayName("JZ - Salta se Zero (Condizione VERA)")
    void testJzTrue() {
        // Setup condizione: 0 in cima allo stack
        cpu.push(0);

        cpu.setIp(10);
        memory.write(10, 5);

        new Jz().execute(ctx);

        assertEquals(17, cpu.getIp(), "Doveva saltare a 17 perché lo stack aveva 0");
        assertTrue(cpu.getStackCopy().isEmpty(), "JZ deve consumare il valore dallo stack (pop)");
    }

    /**
     * Testa l'istruzione JZ (Jump if Zero) quando la condizione è FALSA.
     * <p>
     * Poiché nello stack c'è 99 (diverso da 0), il salto NON deve avvenire.
     * </p>
     * <strong>Nota sul Program Counter:</strong>
     * Verifica che l'IP rimanga coerente con l'istruzione successiva.
     */
    @Test
    @DisplayName("JZ - NON Salta se diverso da Zero (Condizione FALSA)")
    void testJzFalse() {
        // Setup condizione: Valore != 0
        cpu.push(99);

        cpu.setIp(10);
        memory.write(10, 5);

        new Jz().execute(ctx);

        //Perchè deve fare la cpu lo step in avanti non il jz
        assertEquals(10, cpu.getIp(), "Non doveva eseguire il salto (offset ignorato)");
        assertTrue(cpu.getStackCopy().isEmpty(), "JZ deve consumare il valore dallo stack anche se non salta");
    }
}