package it.uniupo.msvm.core.impl;

import it.uniupo.msvm.core.instructions.impl.Push;
import it.uniupo.msvm.core.instructions.impl.Pop;
import it.uniupo.msvm.core.instructions.impl.Swap;
import it.uniupo.msvm.core.instructions.impl.Dup;
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

    /**
     * Verifica il corretto funzionamento dell'istruzione {@link Swap}.
     * <p>
     * <strong>Scenario:</strong>
     * <ol>
     * <li>Inserisce manualmente due valori nello stack (10 e 20).</li>
     * <li>Esegue l'istruzione Swap che dovrebbe scambiare i due valori.</li>
     * </ol>
     * <strong>Risultato atteso:</strong> I valori devono essere scambiati, con 10 in cima e 20 sotto.
     */
    @Test
    @DisplayName("Test Swap: scambio due valori nello stack")
    void testSwap() {
        // Setup: Riempiamo lo stack
        cpu.push(10);
        cpu.push(20);

        // Esecuzione: Scambia i due elementi in cima
        new Swap().execute(ctx);

        // Verifica: Ora in cima ci deve essere 10, poi 20
        assertEquals(10, cpu.pop(), "Primo valore non scambiato correttamente");
        assertEquals(20, cpu.pop(), "Secondo valore non scambiato correttamente");
    }

    /**
     * Verifica il corretto funzionamento dell'istruzione {@link Dup}.
     * <p>
     * <strong>Scenario:</strong>
     * <ol>
     * <li>Inserisce manualmente un valore nello stack (10).</li>
     * <li>Esegue l'istruzione Dup che dovrebbe duplicare il valore in cima.</li>
     * </ol>
     * <strong>Risultato atteso:</strong> Il valore deve essere duplicato, con due 10 in cima allo stack.
     */
    @Test
    @DisplayName("Test Dup: duplico il valore in cima allo stack")
    void testDup() {
        // Setup: Inseriamo un valore nello stack
        cpu.push(10);

        // Esecuzione: Duplica l'elemento in cima
        new Dup().execute(ctx);

        // Verifica: Ora in cima ci devono essere due 10
        assertEquals(10, cpu.pop(), "Valore duplicato non corretto");
        assertEquals(10, cpu.pop(), "Valore originale non preservato");
    }
}