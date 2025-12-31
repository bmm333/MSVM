package it.uniupo.msvm.core.assembler;
import it.uniupo.msvm.core.instructions.Opcode;
import it.uniupo.msvm.core.exceptions.VmException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AssemblerTest {
    private final Assembler assembler = new Assembler();
    @Test
    void testAssembleBasicProgram() {
        // Un programma semplice: PUSH 10, PUSH 20, ADD, HALT
        String source = """
            PUSH 10
            PUSH 0x14  ; 20 in hex
            ADD
            HALT
            """;

        int[] bytecode = assembler.assemble(source);
        assertNotNull(bytecode);
        assertEquals(6, bytecode.length); // 2(PUSH+arg) + 2(PUSH+arg) + 1(ADD) + 1(HALT)
        assertEquals(Opcode.PUSH.getCode(), bytecode[0]);
        assertEquals(10, bytecode[1]);
        assertEquals(Opcode.PUSH.getCode(), bytecode[2]);
        assertEquals(20, bytecode[3]); // 0x14 deve essere 20
        assertEquals(Opcode.ADD.getCode(), bytecode[4]);
        assertEquals(Opcode.HALT.getCode(), bytecode[5]);
    }
    @Test
    void testJumpAndBranchInstructions() {
        // Questo test è FONDAMENTALE dopo il merge con Luca.
        // Verifica che JUMP, JZ, IFGT e IFLT vengano riconosciuti come istruzioni con argomento.
        String source = """
            JUMP 5
            JZ 10
            IFGT 0x02
            IFLT 20
            """;

        int[] bytecode = assembler.assemble(source);
        assertEquals(8, bytecode.length); // 4 istruzioni * 2 (opcode + arg) = 8 interi
        assertEquals(Opcode.JUMP.getCode(), bytecode[0]);
        assertEquals(5, bytecode[1]);
        assertEquals(Opcode.JZ.getCode(), bytecode[2]);
        assertEquals(10, bytecode[3]);
        assertEquals(Opcode.IFGT.getCode(), bytecode[4]);
        assertEquals(2, bytecode[5]); // 0x02 hex
    }

    @Test
    void testNegativeNumbers() {
        // Edge Case: Gestione dei numeri negativi
        String source = "PUSH -10";
        int[] bytecode = assembler.assemble(source);
        assertEquals(Opcode.PUSH.getCode(), bytecode[0]);
        assertEquals(-10, bytecode[1]);
    }

    @Test
    void testCaseInsensitivity() {
        // Usability: L'assembler deve accettare minuscole e maiuscole miste
        String source = """
            push 10
            Add
            halt
            """;

        int[] bytecode = assembler.assemble(source);
        assertEquals(Opcode.PUSH.getCode(), bytecode[0]);
        assertEquals(Opcode.ADD.getCode(), bytecode[2]); // Indice 2 perché PUSH occupa 0 e 1
        assertEquals(Opcode.HALT.getCode(), bytecode[3]);
    }
    @Test
    void testCommentsAndEmptyLines() {
        String source = """
            ; Inizio programma
            
            PUSH 5 ; Commento inline
            
            HALT
            """;

        int[] bytecode = assembler.assemble(source);
        assertEquals(3, bytecode.length); // PUSH, 5, HALT
    }

    @Test
    void testSyntaxErrorUnknownOpcode() {
        String source = "PUSH 10\n PIPPO"; // PIPPO non esiste
        VmException ex = assertThrows(VmException.class, () -> {
            assembler.assemble(source);
        });
        assertTrue(ex.getMessage().contains("line 2")); // Deve dire la riga giusta
    }

    @Test
    void testMissingArgument() {
        assertThrows(VmException.class, () -> assembler.assemble("PUSH"));
    }
    //Test per il v2.0
    @Test
    void testLabelsBackwardReference() {
        // Test Fondamentale 1: Salto all'indietro (Loop)
        // START: (addr 0)
        //   PUSH 10 (addr 0, 1)
        //   JUMP START (addr 2, 3) -> Deve saltare a 0
        String source = """
            START:
            PUSH 10
            JUMP START
            """;

        int[] bytecode = assembler.assemble(source);

        assertEquals(4, bytecode.length);
        assertEquals(Opcode.JUMP.getCode(), bytecode[2]);
        assertEquals(0, bytecode[3], "La label START deve risolversi all'indirizzo 0");
    }

    @Test
    void testLabelsForwardReference() {
        // Test Fondamentale 2: Salto in avanti (Il vero motivo del Two-Pass)
        //   JUMP FINE (addr 0, 1) -> Deve saltare a 4
        //   PUSH 5    (addr 2, 3)
        // FINE:
        //   HALT      (addr 4)
        String source = """
            JUMP FINE
            PUSH 5
            FINE:
            HALT
            """;

        int[] bytecode = assembler.assemble(source);
        assertEquals(5, bytecode.length); // 2(JUMP) + 2(PUSH) + 1(HALT)
        assertEquals(Opcode.JUMP.getCode(), bytecode[0]);
        assertEquals(4, bytecode[1], "La label FINE deve risolversi all'indirizzo 4 (Forward Jump)");
        assertEquals(Opcode.HALT.getCode(), bytecode[4]);
    }
    @Test
    void testUndefinedLabel() {
        // Test Robustezza: Cosa succede se salto a una label che non esiste?
        String source = "JUMP NONESISTE";

        VmException ex = assertThrows(VmException.class, () -> {
            assembler.assemble(source);
        });
        assertTrue(ex.getMessage().contains("Undefined label"), "Deve segnalare che la label non esiste");
    }

    @Test
    void testLabelCaseInsensitivity() {
        // Test Usabilità: Loop: e JUMP LOOP devono matchare
        String source = """
            Loop:
            PUSH 1
            JUMP LOOP
            """;

        int[] bytecode = assembler.assemble(source);
        assertEquals(0, bytecode[3], "Deve gestire maiuscole/minuscole nelle label");
    }
}