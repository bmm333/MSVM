package it.uniupo.msvm.core.impl;

import it.uniupo.msvm.core.instructions.impl.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IfTest extends InstructionTestBase{

    /**
     * Testa l'istruzione IFGT (Branch if Greater Than).
     * <p>
     * Scenario: 20 > 10.
     * <br>
     * <strong>Ordine Stack:</strong>
     * <ul>
     * <li>PUSH 20 (b - sotto)</li>
     * <li>PUSH 10 (a - cima)</li>
     * </ul>
     * Condizione: <code>b > a</code> (20 > 10) -> <strong>VERO</strong>. Deve saltare.
     * </p>
     */
    @Test
    @DisplayName("IFGT - Salta se Maggiore (20 > 10)")
    void testIfgtTrue() {
        // SETUP
        cpu.push(20); // b
        cpu.push(10); // a

        cpu.setIp(10);
        memory.write(10, 5); // Offset di salto

        // EXECUTE
        new Ifgt().execute(ctx);

        // VERIFY
        // Deve saltare: IP = (10 + 1) + 5 = 16 (oppure 17 se fetchNextByte incrementa prima)
        // Verifichiamo la tua logica: fetchNextByte() porta IP a 11. Poi 11 + 5 = 16.
        // Se hai corretto fetchNextByte come suggerito, l'IP base è quello DOPO l'argomento.
        assertEquals(16, cpu.getIp(), "20 > 10 è vero, doveva saltare (+5)");
        assertTrue(cpu.getStackCopy().isEmpty(), "Deve consumare i due operandi dallo stack");
    }

    /**
     * Testa IFGT quando la condizione è FALSA.
     * <p>
     * Scenario: 10 > 20 -> FALSO.
     * </p>
     */
    @Test
    @DisplayName("IFGT - NON Salta se Minore (10 > 20 False)")
    void testIfgtFalse() {
        cpu.push(10); // b
        cpu.push(20); // a

        cpu.setIp(10);
        memory.write(10, 5);

        new Ifgt().execute(ctx);

        // Non deve saltare, ma deve aver consumato l'argomento del salto (byte 10).
        // Quindi IP deve essere 11.
        assertEquals(11, cpu.getIp(), "Non doveva saltare, solo avanzare");
        assertTrue(cpu.getStackCopy().isEmpty(), "Deve consumare gli operandi anche se non salta");
    }

    /**
     * Testa l'istruzione IFLT (Branch if Less Than).
     * <p>
     * Scenario: 10 < 20.
     * Condizione: <code>b < a</code> (10 < 20) -> <strong>VERO</strong>. Deve saltare.
     * </p>
     */
    @Test
    @DisplayName("IFLT - Salta se Minore (10 < 20)")
    void testIfltTrue() {
        // SETUP
        cpu.push(10); // b (sotto)
        cpu.push(20); // a (cima)

        cpu.setIp(10);
        memory.write(10, 5);

        // EXECUTE
        new Iflt().execute(ctx);

        // VERIFY
        assertEquals(16, cpu.getIp(), "10 < 20 è vero, doveva saltare");
    }

    /**
     * Testa IFLT quando la condizione è FALSA.
     * <p>
     * Scenario: 20 < 10 -> FALSO.
     * </p>
     */
    @Test
    @DisplayName("IFLT - NON Salta se Maggiore (20 < 10 False)")
    void testIfltFalse() {
        cpu.push(20);
        cpu.push(10);

        cpu.setIp(10);
        memory.write(10, 5);

        new Iflt().execute(ctx);

        assertEquals(11, cpu.getIp(), "Non doveva saltare");
    }
}
