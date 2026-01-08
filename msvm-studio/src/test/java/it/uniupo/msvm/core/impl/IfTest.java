package it.uniupo.msvm.core.impl;

import it.uniupo.msvm.core.instructions.impl.Ifgt;
import it.uniupo.msvm.core.instructions.impl.Iflt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test per le istruzioni di Branching Condizionale (IFGT, IFLT).
 * <p>
 * Verifica la logica di confronto e salto.
 * Le istruzioni utilizzano indirizzamento ASSOLUTO.
 * </p>
 *
 * @author Luca Lupi  modificato da Arben Mema
 */
@DisplayName("Test Unità: Branching (IFGT/IFLT)")
public class IfTest extends InstructionTestBase {

    /**
     * Testa l'istruzione IFGT (Branch if Greater Than).
     * <p>
     * Scenario: 20 > 10.
     * Stack: [20 (b), 10 (a)] -> b > a -> VERO.
     * Deve saltare all'indirizzo assoluto specificato.
     * </p>
     */
    @Test
    @DisplayName("IFGT - Salta se Maggiore (20 > 10)")
    void testIfgtJumpWhenGreater() {
        int startIp = 10;
        int targetAddress = 25;

        // SETUP
        cpu.push(20); // b (valore sotto)
        cpu.push(10); // a (valore sopra)

        injectInstructionArgument(startIp, targetAddress);

        // EXECUTE
        new Ifgt().execute(ctx);

        // VERIFY
        assertEquals(targetAddress, cpu.getIp(), "20 > 10 è vero, doveva saltare all'indirizzo target assoluto");
        assertTrue(cpu.getStackCopy().isEmpty(), "Deve consumare i due operandi dallo stack");
    }

    /**
     * Testa l'istruzione IFGT quando la condizione è FALSA.
     * <p>
     * Scenario: 10 > 20 -> FALSO.
     * Non deve saltare, ma l'IP deve avanzare dopo l'argomento.
     * </p>
     */
    @Test
    @DisplayName("IFGT - NON Salta se Minore (10 > 20 -> Falso)")
    void testIfgtNoJumpWhenSmaller() {
        int startIp = 10;
        int targetAddress = 25;

        cpu.push(10); // b
        cpu.push(20); // a

        injectInstructionArgument(startIp, targetAddress);

        new Ifgt().execute(ctx);

        // IP deve avanzare di 1 per aver letto l'argomento
        assertEquals(startIp + 1, cpu.getIp(), "Non doveva saltare, ma avanzare di 1 dopo l'argomento");
        assertTrue(cpu.getStackCopy().isEmpty());
    }

    /**
     * Testa l'istruzione IFLT (Branch if Less Than).
     * <p>
     * Scenario: 10 < 20.
     * Stack: [10 (b), 20 (a)] -> b < a -> VERO.
     * Deve saltare.
     * </p>
     */
    @Test
    @DisplayName("IFLT - Salta se Minore (10 < 20)")
    void testIfltJumpWhenSmaller() {
        int startIp = 10;
        int targetAddress = 30;

        cpu.push(10); // b
        cpu.push(20); // a

        injectInstructionArgument(startIp, targetAddress);

        new Iflt().execute(ctx);

        assertEquals(targetAddress, cpu.getIp(), "10 < 20 è vero, doveva saltare all'indirizzo target assoluto");
        assertTrue(cpu.getStackCopy().isEmpty());
    }

    /**
     * Helper per impostare IP e scrivere l'argomento dell'istruzione (target del salto).
     */
    private void injectInstructionArgument(int address, int argumentValue) {
        cpu.setIp(address);
        memory.write(address, argumentValue);
    }
}