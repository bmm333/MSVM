package it.uniupo.msvm.core.cpu;
/*
import it.uniupo.msvm.core.instructions.Instruction;
import it.uniupo.msvm.core.instructions.Opcode;
import it.uniupo.msvm.core.memory.Memory;
import it.uniupo.msvm.core.memory.OperandStack;

import che servono per il funzionamento del cpu
Ancora da definire TODO @lucalupi -> Memory
*/
public class CPU{
    private final Memory memory;
    private final OperandStack stack;
    //Registri
    private int ip=0; //Instruction Pointer
    private boolean isHalted=false;

    //Strategy Map per decodificare le istruzioni.
    private final Map<Opcode,Instruction>instructionSet=new HashMap<>();

    //Inject la memoria in construttore per il testing.
    public Cpu(Memory memory,OperandStack stack) {
        this.memory = memory;
        this.stack = stack;
    }
    //Bisognia di un metodo che reg una nuova istr nel set del cpu in modo tale da rispettare Open Closed Principle OCP
    // Metodo che esegue tutto il ciclo fino a -> Halt || Error
    //Metodo Step (OP atomiche f->d->e)
    //e i getters e setters
}
