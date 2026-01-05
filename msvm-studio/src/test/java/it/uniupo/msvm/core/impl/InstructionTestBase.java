package it.uniupo.msvm.core.impl;

import it.uniupo.msvm.core.cpu.Cpu;
import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.memory.Memory;
import it.uniupo.msvm.core.memory.OperandStack;
import org.junit.jupiter.api.BeforeEach;

/**
 * Classe padre per estenderle a tutte le operazioni ISa chge ci sono
 */
public class InstructionTestBase {

    protected ExecutionContext ctx;
    protected Memory memory;
    protected OperandStack stack;
    protected Cpu cpu;

    @BeforeEach
    void setUp(){
        memory=new Memory(256);
        stack=new OperandStack();
        cpu=new Cpu(memory,stack);

        ctx=cpu;
    }
}
