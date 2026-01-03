package it.uniupo.msvm.core.instructions;

import it.uniupo.msvm.core.cpu.Cpu;
import it.uniupo.msvm.core.memory.Memory;
import it.uniupo.msvm.core.memory.OperandStack;

public interface Instruction {
    /**
     * Esegue l'istruzione.
     * @param ctx Il contesto di esecuzione (accesso sicuro a CPU/Memoria).
     * @throws it.uniupo.msvm.core.exceptions.VmException In caso di errori di runtime (memoria, stack, ecc).
     */
    void execute(ExecutionContext ctx);
}