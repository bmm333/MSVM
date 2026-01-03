package it.uniupo.msvm.core.impl;

import it.uniupo.msvm.core.instructions.impl.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di unit test per le istruzioni aritmetiche della macchina virtuale.
 * <p>
 * Verifica le operazioni matematiche di base e la gestione delle eccezioni.
 * </p>
 *
 * @author Luca Lupi
 */
@DisplayName("Test Unità: Istruzioni Aritmetiche (CPU)")
public class ArithmeticTest extends InstructionTestBase {

    /**
     * Testa l'istruzione di addizione (ADD).
     */
    @Test
    @DisplayName("ADD - Verifica Addizione (10 + 20 = 30)")
    void testAdd() {
        // Setup
        cpu.push(10);
        cpu.push(20);

        // Execute
        new Add().execute(ctx);

        // Verify
        assertEquals(30, cpu.pop(), "La somma dovrebbe essere 30");
    }

    /**
     * Testa l'istruzione di sottrazione (SUB).
     * Nota: Verifica il corretto ordine degli operandi (b - a).
     */
    @Test
    @DisplayName("SUB - Verifica Sottrazione e Ordine Stack (20 - 10 = 10)")
    void testSub() {
        cpu.push(20); // b (sotto)
        cpu.push(10); // a (sopra)

        new Sub().execute(ctx);

        assertEquals(10, cpu.pop(), "La sottrazione dovrebbe essere 10");
    }

    /**
     * Testa l'istruzione di divisione intera (DIV).
     */
    @Test
    @DisplayName("DIV - Verifica Divisione Intera (10 / 2 = 5)")
    void testDiv() {
        cpu.push(10); // b
        cpu.push(2);  // a

        new Div().execute(ctx);

        assertEquals(5, cpu.pop(), "Il quoziente dovrebbe essere 5");
    }

    /**
     * Testa l'istruzione di moltiplicazione (MUL).
     */
    @Test
    @DisplayName("MUL - Verifica Moltiplicazione (4 * 2 = 8)")
    void testMul() {
        cpu.push(4);
        cpu.push(2);

        new Mul().execute(ctx);

        assertEquals(8, cpu.pop(), "Il prodotto dovrebbe essere 8");
    }

    /**
     * Testa il comportamento in caso di divisione per zero.
     */
    @Test
    @DisplayName("Eccezione - Divisione per Zero (ArithmeticException)")
    void testDivException() {
        cpu.push(10);
        cpu.push(0);

        assertThrows(ArithmeticException.class, () -> new Div().execute(ctx),
                "Deve lanciare ArithmeticException se il divisore è 0");
    }
}