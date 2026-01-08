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
 * Le istruzioni utilizzano indirizzamento ASSOLUTO.
 * </p>
 *
 * @author Luca Lupi
 */
@DisplayName("Test Unità: Controllo Flusso (Jump/Branch)")
public class ControlFlowTest extends InstructionTestBase {

    /**
     * Testa l'istruzione JUMP.
     * Deve impostare l'IP all'indirizzo assoluto specificato come argomento.
     */
    @Test
    @DisplayName("JUMP - Salto Incondizionato (Indirizzo Assoluto)")
    void testJumpAbsolute() {
        int startIp = 10;
        int targetAddress = 16;

        injectInstructionArgument(startIp, targetAddress);

        new Jump().execute(ctx);

        assertEquals(targetAddress, cpu.getIp(), "L'IP deve essere impostato all'indirizzo target assoluto");
    }

    /**
     * Testa l'istruzione JZ (Jump if Zero) quando la condizione è VERA.
     * Stack: [0] -> Salto eseguito.
     */
    @Test
    @DisplayName("JZ - Salta se Zero (Condizione VERA)")
    void testJzJumpWhenZero() {
        int startIp = 10;
        int targetAddress = 25;

        // Setup condizione: 0 in cima allo stack
        cpu.push(0);
        injectInstructionArgument(startIp, targetAddress);

        new Jz().execute(ctx);

        assertEquals(targetAddress, cpu.getIp(), "Doveva saltare all'indirizzo target perché lo stack aveva 0");
        assertTrue(cpu.getStackCopy().isEmpty(), "JZ deve consumare il valore dallo stack (pop)");
    }

    /**
     * Testa l'istruzione JZ (Jump if Zero) quando la condizione è FALSA.
     * Stack: [99] -> Salto NON eseguito.
     */
    @Test
    @DisplayName("JZ - NON Salta se diverso da Zero (Condizione FALSA)")
    void testJzNoJumpWhenNonZero() {
        int startIp = 10;
        int targetAddress = 50; // Indirizzo che NON deve essere raggiunto

        // Setup condizione: Valore != 0
        cpu.push(99);
        injectInstructionArgument(startIp, targetAddress);

        new Jz().execute(ctx);

        // L'istruzione legge l'argomento (1 byte), quindi IP avanza di 1
        int expectedIp = startIp + 1;
        assertEquals(expectedIp, cpu.getIp(), "Non doveva eseguire il salto, ma l'IP deve avanzare dopo l'argomento");
        assertTrue(cpu.getStackCopy().isEmpty(), "JZ deve consumare il valore dallo stack anche se non salta");
    }

    /**
     * Helper method per preparare lo stato della CPU prima dell'esecuzione manuale di un'istruzione.
     * Imposta l'IP e scrive l'argomento dell'istruzione nella memoria.
     *
     * @param address Indirizzo corrente dell'istruzione (dove si trova l'argomento)
     * @param argumentValue Valore dell'argomento (es. target del salto)
     */
    private void injectInstructionArgument(int address, int argumentValue) {
        cpu.setIp(address);
        memory.write(address, argumentValue);
    }
}