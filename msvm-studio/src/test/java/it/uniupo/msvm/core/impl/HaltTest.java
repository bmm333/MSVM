package it.uniupo.msvm.core.impl;

import it.uniupo.msvm.core.instructions.impl.Halt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test per l'istruzione di arresto HALT.
 * <p>
 * Verifica che l'istruzione modifichi correttamente lo stato interno della CPU,
 * portandola in una condizione di arresto (Halted).
 * </p>
 *
 * @author Luca Lupi
 */
@DisplayName("Test Unità: Istruzione HALT (Stop)")
public class HaltTest extends InstructionTestBase {

    /**
     * Testa l'istruzione HALT.
     * <p>
     * Scenario:
     * <ol>
     * <li>Stato iniziale: La CPU è attiva (isHalted = false).</li>
     * <li>Esecuzione: Viene invocata l'istruzione HALT.</li>
     * <li>Stato finale: La CPU deve risultare ferma (isHalted = true).</li>
     * </ol>
     * Questo flag è fondamentale perché è la condizione di uscita del loop principale del VmRunner.
     * </p>
     */
    @Test
    @DisplayName("HALT - Verifica Arresto CPU")
    void testHalt() {
        // 1. Verifica preliminare: Appena accesa, la CPU NON deve essere in halt
        assertFalse(cpu.isHalted(), "La CPU dovrebbe essere attiva (Running) all'avvio del test");

        // 2. EXECUTE
        new Halt().execute(ctx);

        // 3. VERIFY
        // Verifichiamo che il flag 'isHalted' nella CPU sia diventato true
        assertTrue(cpu.isHalted(), "L'istruzione HALT deve impostare lo stato della CPU su HALTED");
    }
}