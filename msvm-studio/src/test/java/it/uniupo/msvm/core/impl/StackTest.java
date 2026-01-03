package it.uniupo.msvm.core.impl;

import it.uniupo.msvm.core.instructions.impl.Push;
import it.uniupo.msvm.core.instructions.impl.Pop;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitari per le istruzioni di manipolazione dello stack.
 * <p>
 * Questa classe verifica il corretto funzionamento delle istruzioni
 * {@link Push} e {@link Pop} all'interno della macchina virtuale.
 * Estende {@link InstructionTestBase} per ereditare il contesto di esecuzione (CPU e Memoria).
 * </p>
 *
 * @see it.uniupo.msvm.core.instructions.impl.Push
 * @see it.uniupo.msvm.core.instructions.impl.Pop
 */
public class StackTest extends InstructionTestBase {

    /**
     * Verifica il corretto funzionamento dell'istruzione {@link Push}.
     * <p>
     * <strong>Scenario:</strong>
     * <ol>
     * <li>Imposta l'Instruction Pointer (IP) a 1.</li>
     * <li>Scrive il valore 10 nella cella di memoria 1 (l'argomento della Push).</li>
     * <li>Esegue l'istruzione Push.</li>
     * </ol>
     * <strong>Risultato atteso:</strong> Il valore 10 deve essere presente in cima allo stack della CPU.
     */
    @Test
    @DisplayName("Test Push: inserisco il numero 10 nello stack")
    void testPush() {
        // Setup: IP punta all'argomento, Memoria contiene il valore 10
        cpu.setIp(1);
        memory.write(1, 10);

        // Esecuzione
        new Push().execute(ctx);

        // Verifica
        assertEquals(10, cpu.pop(), "Valore non inserito correttamente nello stack");
    }

    /**
     * Verifica il corretto funzionamento dell'istruzione {@link Pop}.
     * <p>
     * <strong>Scenario:</strong>
     * <ol>
     * <li>Inserisce manualmente due valori nello stack (10 e 20).</li>
     * <li>Esegue l'istruzione Pop (che dovrebbe rimuovere il 20).</li>
     * </ol>
     * <strong>Risultato atteso:</strong> Il valore rimosso deve essere 20, lasciando il 10 in cima allo stack
     * (verificato estraendo il valore successivo).
     */
    @Test
    @DisplayName("Test Pop: controllo se fa la pop nel modo giusto")
    void testpop() {
        // Setup: Riempiamo lo stack
        cpu.push(10);
        cpu.push(20);

        // Esecuzione: Rimuove l'elemento in cima (20)
        new Pop().execute(ctx);

        // Verifica: Ora in cima ci deve essere il 10
        assertEquals(10, cpu.pop(), "Valore non eliminato nel modo corretto o stack inconsistente");
    }
}