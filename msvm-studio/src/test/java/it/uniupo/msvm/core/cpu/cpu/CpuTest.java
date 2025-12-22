package it.uniupo.msvm.core.cpu.cpu;


import it.uniupo.msvm.core.cpu.Cpu;
import it.uniupo.msvm.core.instructions.Instruction;
import it.uniupo.msvm.core.instructions.Opcode;
import it.uniupo.msvm.core.memory.Memory;
import it.uniupo.msvm.core.memory.OperandStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
* Test suite per la CPU
* Black-box testing del ciclo Fetch-Decode-Execute
* Usiamo "Anonymous Classes " o lambda per simulare le istruzioni
* in modo tale da testare la cpu isolata dalle implementazioni reali (ADD,PUSH eccecc)
*/
public class CpuTest {
    private Cpu cpu;
    private Memory memory;
    private OperandStack stack;

    //constatne per il test
    private final int MEM_SIZE=256;
    @BeforeEach()
    void setUp() {
        //1. Setup del ambien(Fixture)
        //per adesso uso i stubs che ho definito  di mem e stack
        memory=new Memory(MEM_SIZE);
        stack=new OperandStack();
        //DI : injetto la memoria e stack in CPU
        cpu=new Cpu(memory,stack);
    }
    @Test()
    @DisplayName("Fetch: IP should increment after reading opcode")
    void testFetchIncrement()
    {
        //Arrange
        //Registriamo istruzioni dummy (NOP-No operation) per PUSH
        cpu.registerInstruction(Opcode.PUSH,(c,m,s)->{/*do nothing*/});
        //Scrive lopcode in memoria all indirizzo 0
        memory.write(0,Opcode.PUSH.getCode());
        //ACT
        cpu.step();
        //ASsert
        assertEquals(1,cpu.getIp(),"L'instruction pointer deve avanzare di 1 byte dopo fetch");
    }
    @Test
    @DisplayName("Execute: Should delegate logic to the registered Instruction")
    void testExecutionDelegation()
    {
        //Arrange
        //Creiamo side-effect controllato per vedere se l'istruzione viene eseguita
        //simuliamo che l'istruzione HALT metta anche un vaore in stack
        cpu.registerInstruction(Opcode.HALT,(c,m,s)->{s.push(999);//Side effect
            c.halt(); });
        memory.write(0,Opcode.HALT.getCode());
        cpu.step();
        assertEquals(999,stack.pop(),"La cpu deve invocare il metodo execute dell esecuzione corretta");
    }
    @Test
    @DisplayName("Run Loop: Should stop when Halted flag is set")
    void testRunLoop()
    {
        Instruction nop=(c, m, s)->{};
        cpu.registerInstruction(Opcode.PUSH,nop);
        cpu.registerInstruction(Opcode.HALT, (c, m, s) -> c.halt());
        memory.write(0, Opcode.PUSH.getCode());
        memory.write(1, Opcode.PUSH.getCode());
        memory.write(2, Opcode.HALT.getCode());
        cpu.run();//dovrebbe eseguire i 3 step e fermarsi
        assertTrue(cpu.isHalted(),"La CPU deve essere in stato HALTED alla fine del programma");
        assertEquals(3,cpu.getIp(),"L'IP deve essere avanzato di 3 posizioni (0->1->2=->3)");
    }
    @Test
    @DisplayName("Robustness: Should throw exception on Segmentation Fault")
    void testSegmentationFault()
    {
        cpu.setIp(MEM_SIZE+10);
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cpu.step();
        });
        assertTrue(exception.getMessage().contains("fault") || exception.getMessage().contains("bounds"),
                "Deve lanciare un SegFault se IP esce dalla memoria");
    }
    @Test
    @DisplayName("Robustness: Should throw exception on Unknown Opcode")
    void testUnknownOpcode() {
        // Scriviamo un byte (0xEE) che non esiste nell'Enum Opcode
        memory.write(0, 0xEE);
        assertThrows(RuntimeException.class, () -> cpu.step(),
                "La CPU non deve funzionare se trova un byte che non è un opcode valido");
    }

    @Test
    @DisplayName("Robustness: Should throw exception on Unimplemented Instruction")
    void testUnimplementedInstruction() {
        // Scriviamo un opcode valido (es. ADD) ma NON lo registriamo nella CPU map
        memory.write(0, Opcode.ADD.getCode());
        //non chiamo cpu.registerInstruction(Opcode.ADD, ...)
        Exception exception = assertThrows(RuntimeException.class, () -> cpu.step());
        assertTrue(exception.getMessage().contains("Not implemented") || exception.getMessage().contains("null"),
                "Deve segnalare se un opcode esiste ma non ha un'implementazione associata");
    }
}
