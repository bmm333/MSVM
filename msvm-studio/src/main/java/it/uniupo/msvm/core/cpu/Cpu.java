package it.uniupo.msvm.core.cpu;
import it.uniupo.msvm.core.exceptions.MemoryAccessException;
import it.uniupo.msvm.core.instructions.Instruction;
import it.uniupo.msvm.core.instructions.Opcode;
import it.uniupo.msvm.core.memory.Memory;
import it.uniupo.msvm.core.memory.OperandStack;

import java.util.Map;
import java.util.HashMap;

//Test for jira

public class Cpu {
    private final Memory memory;
    private final OperandStack stack;
    //Registri
    private int ip=0; //Instruction Pointer
    private boolean isHalted=false;

    //Strategy Map per decodificare le istruzioni.
    private final Map<Opcode, Instruction>instructionSet=new HashMap<>();

    //Inject la memoria in costruttore per il testing.
    public Cpu(Memory memory, OperandStack stack) {
        this.memory = memory;
        this.stack = stack;
    }
    public void registerInstruction(Opcode opcode,Instruction implementation)
    {
        instructionSet.put(opcode,implementation);
    }
    //Esegue tutto fino a halt oppure Errore
    public void run() throws MemoryAccessException {
        while(!isHalted)
        {
            step();
        }
    }

    //Eseguirà un step atomico (1.Fetch->2.Decode->3.Execute)
    public void step() throws MemoryAccessException {
        if(isHalted) return;

        //1.Fetch
        //Se passa lancera un eccezione di tipo Address out of bounds (Guarda key MSVM-5 Jira)
        if(ip>=memory.sizeMemory())
        {
            throw new RuntimeException("Segmentation fault\n");
        }
        int opcodeByte=memory.read(ip);
        ip++; //incr puntatore
        //2.Decode
        Opcode opcode;
        try{
            opcode=Opcode.fromByte(opcodeByte); 
        }catch(IllegalArgumentException e)
        {
            throw new RuntimeException("Illegal Instruction on Address:" + (ip-1));
        }
        Instruction instruction=instructionSet.get(opcode);
        if(instruction==null)
        {
            throw new RuntimeException("Not implemented");
        }
        instruction.execute(this,memory,stack);
    }

    public void halt(){this.isHalted=true;}
    public boolean isHalted(){return this.isHalted;}
    public int getIp(){return this.ip;}
    public void setIp(int ip){this.ip=ip;}
}
