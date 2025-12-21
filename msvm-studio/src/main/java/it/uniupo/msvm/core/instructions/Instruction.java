package it.uniupo.msvm.core.instructions;

import it.uniupo.msvm.core.cpu.Cpu;
import it.uniupo.msvm.core.memory.Memory;
import it.uniupo.msvm.core.memory.OperandStack;

public interface Instruction {
    void execute(Cpu cpu, Memory memory, OperandStack stack);
}