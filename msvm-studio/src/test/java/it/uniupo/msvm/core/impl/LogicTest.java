package it.uniupo.msvm.core.impl;

import it.uniupo.msvm.core.instructions.impl.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di unit test per le istruzioni logiche e bitwise.
 * <p>
 * Verifica il corretto funzionamento delle operazioni bit a bit:
 * AND, OR, NOT e SHL (Shift Left).
 * </p>
 *
 * @author Luca Lupi
 */
@DisplayName("Test Unità: Istruzioni Logiche (Bitwise)")
public class LogicTest extends InstructionTestBase {

    /**
     * Testa l'istruzione AND (Bitwise AND).
     * <p>
     * Scenario: 12 (1100) AND 10 (1010) = 8 (1000).
     * </p>
     */
    @Test
    @DisplayName("AND - Verifica Bitwise AND (12 & 10 = 8)")
    void testAnd() {
        cpu.push(12); // 1100
        cpu.push(10); // 1010

        new And().execute(ctx);

        assertEquals(8, cpu.pop(), "12 & 10 deve dare 8");
    }

    /**
     * Testa l'istruzione OR (Bitwise OR).
     * <p>
     * Scenario: 12 (1100) OR 10 (1010) = 14 (1110).
     * </p>
     */
    @Test
    @DisplayName("OR - Verifica Bitwise OR (12 | 10 = 14)")
    void testOr() {
        cpu.push(12); // 1100
        cpu.push(10); // 1010

        new Or().execute(ctx);

        assertEquals(14, cpu.pop(), "12 | 10 deve dare 14");
    }

    /**
     * Testa l'istruzione NOT (Bitwise Complement).
     * <p>
     * Scenario: ~0 = -1 (Tutti i bit a 1).
     * Nota: È un'operazione unaria, consuma solo 1 elemento dallo stack.
     * </p>
     */
    @Test
    @DisplayName("NOT - Verifica Complemento (~0 = -1)")
    void testNot() {
        cpu.push(0);

        new Not().execute(ctx);

        assertEquals(-1, cpu.pop(), "Il complemento di 0 deve essere -1");
    }

    /**
     * Testa l'istruzione SHL (Shift Left).
     * <p>
     * Scenario: 10 << 2 = 40.
     * <br>
     * <strong>Ordine Stack:</strong>
     * <ol>
     * <li>POP (cima): Quantità di shift (2)</li>
     * <li>POP (sotto): Valore da shiftare (10)</li>
     * </ol>
     * </p>
     */
    @Test
    @DisplayName("SHL - Verifica Shift Left (10 << 2 = 40)")
    void testShl() {
        cpu.push(10); // Valore
        cpu.push(2);  // Quantità (Cima dello stack)

        new Shl().execute(ctx);

        assertEquals(40, cpu.pop(), "10 shiftato a sinistra di 2 deve dare 40");
    }

    /**
     * Testa l'istruzione SHR (Arithmetic Shift Right).
     * <p>
     * Scenario: 40 >> 2 = 10.
     * <br>
     * <strong>Nota:</strong> Questa operazione equivale a una divisione intera per potenze di 2
     * ($40 / 2^2 = 10$) mantenendo il segno.
     * </p>
     */
    @Test
    @DisplayName("SHR - Verifica Shift Right (40 >> 2 = 10)")
    void testShr() {
        cpu.push(40); // Valore da spostare
        cpu.push(2);  // Quantità di spostamento (Cima dello stack)

        new Shr().execute(ctx);

        assertEquals(10, cpu.pop(), "40 shiftato a destra di 2 deve dare 10");
    }
}