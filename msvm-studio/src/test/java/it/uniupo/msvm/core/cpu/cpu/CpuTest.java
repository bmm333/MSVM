package it.uniupo.msvm.core.cpu.cpu;

import it.uniupo.msvm.core.cpu.Cpu;
import it.uniupo.msvm.core.exceptions.MemoryAccessException;
import it.uniupo.msvm.core.exceptions.OpcodeException;
import it.uniupo.msvm.core.instructions.Instruction;
import it.uniupo.msvm.core.instructions.Opcode;
import it.uniupo.msvm.core.memory.Memory;
import it.uniupo.msvm.core.memory.OperandStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CpuTest {
    private Cpu cpu;
    private Memory memory;
    private OperandStack stack;

    private final int MEM_SIZE = 256;

    @BeforeEach
    void setUp() {
        memory = new Memory(MEM_SIZE);
        stack = new OperandStack();
        cpu = new Cpu(memory, stack);
    }

    @Test
    @DisplayName("Fetch: IP should increment after reading opcode")
    void testFetchIncrement() {
        cpu.registerInstruction(Opcode.PUSH, ctx -> { /* do nothing */ });
        memory.write(0, Opcode.PUSH.getCode());

        cpu.step();

        assertEquals(1, cpu.getIp());
    }

    @Test
    @DisplayName("Execute: Should delegate logic to the registered Instruction")
    void testExecutionDelegation() {
        cpu.registerInstruction(Opcode.HALT, ctx -> {
            ctx.push(999);
            ctx.halt();
        });
        memory.write(0, Opcode.HALT.getCode());

        cpu.step();

        assertEquals(999, stack.pop());
        assertTrue(cpu.isHalted());
    }


    @Test
    @DisplayName("Context: FetchNextByte should read argument and increment IP")
    void testFetchNextByte() {
        // Simuliamo una vera PUSH: Legge opcode, poi legge il valore successivo
        cpu.registerInstruction(Opcode.PUSH, ctx -> {
            int arg = ctx.fetchNextByte(); // <--- Questo metodo non era testato!
            ctx.push(arg);
        });

        memory.write(0, Opcode.PUSH.getCode()); // Opcode
        memory.write(1, 42);                    // Argomento (valore)

        cpu.step();

        assertEquals(42, stack.pop(), "Il valore letto deve finire nello stack");
        assertEquals(2, cpu.getIp(), "IP deve avanzare di 2 (1 opcode + 1 argomento)");
    }

    @Test
    @DisplayName("Context: Should delegate Memory and Stack operations correctly")
    void testContextDelegation() {
        // Testiamo readMemory, writeMemory e peek tramite una istruzione custom
        cpu.registerInstruction(Opcode.ADD, ctx -> {
            // Test Write
            ctx.writeMemory(50, 100);
            // Test Read
            int val = ctx.readMemory(50);
            // Test Stack Peek
            ctx.push(val);
            int peeked = ctx.peek();

            if (peeked == 100) ctx.halt(); // Flag di successo
        });

        memory.write(0, Opcode.ADD.getCode());
        cpu.step();

        assertEquals(100, memory.read(50), "WriteMemory deve scrivere in RAM");
        assertTrue(cpu.isHalted(), "Peek deve aver letto il valore corretto");
    }
    @Test
    @DisplayName("Robustness: FetchNextByte out of bounds")
    void testFetchNextByteOutOfBounds() {
        cpu.registerInstruction(Opcode.PUSH, ctx -> ctx.fetchNextByte());

        memory.write(MEM_SIZE - 1, Opcode.PUSH.getCode());
        cpu.setIp(MEM_SIZE - 1);

        MemoryAccessException ex = assertThrows(MemoryAccessException.class, () -> cpu.step());
        assertEquals(MemoryAccessException.AccessType.READ, ex.getType(), "Dovrebbe fallire leggendo l'argomento");
    }

    @Test
    @DisplayName("Run Loop: Should stop when Halted flag is set")
    void testRunLoop() {
        cpu.registerInstruction(Opcode.PUSH, ctx -> {});
        cpu.registerInstruction(Opcode.HALT, ctx -> ctx.halt());
        memory.write(0, Opcode.PUSH.getCode());
        memory.write(1, Opcode.PUSH.getCode());
        memory.write(2, Opcode.HALT.getCode());

        cpu.run();

        assertTrue(cpu.isHalted());
        assertEquals(3, cpu.getIp());
    }

    @Test
    @DisplayName("Robustness: Should throw exception on Segmentation Fault")
    void testSegmentationFault() {
        int badAddress = MEM_SIZE + 10;

        MemoryAccessException ex = assertThrows(MemoryAccessException.class, () -> {
            cpu.setIp(badAddress);
        });
        assertEquals(MemoryAccessException.AccessType.EXECUTE, ex.getType());
    }

    @Test
    @DisplayName("Robustness: Should throw OpcodeException on Unknown Opcode")
    void testUnknownOpcode() {
        memory.write(0, 0xEE);
        OpcodeException ex = assertThrows(OpcodeException.class, () -> cpu.step());
        assertEquals(0xEE, ex.getIllegalByte());
    }

    @Test
    @DisplayName("Robustness: Should throw OpcodeException on Unimplemented Instruction")
    void testUnimplementedInstruction() {
        memory.write(0, Opcode.ADD.getCode());
        assertThrows(OpcodeException.class, () -> cpu.step());
    }
}