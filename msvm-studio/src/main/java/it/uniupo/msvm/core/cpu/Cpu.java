package it.uniupo.msvm.core.cpu;
/*
import it.uniupo.msvm.core.instructions.Instruction;
import it.uniupo.msvm.core.instructions.Opcode;
import it.uniupo.msvm.core.memory.Memory;
import it.uniupo.msvm.core.memory.OperandStack;

import che servono per il funzionamento del cpu
Ancora da definire TODO @lucalupi -> Memory
*/
import java.util.Map;
import java.util.HashMap;



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
    public void registerInstruction(OpCode opcdoe,Instruction implementation)
    {
        instructionSet.put(opcdoe,implementation);
    }
    //Esegue tutto fino a halt oppure Errore
    public void run()
    {
        while(!isHalted)
        {
            step();
        }
    }

    //Eseguira un step atomico (1.Fetch->2.Decode->3.Execute)
    public void step()
    {
        if(isHalted) return;

        //1.Fetch
        //Se passa lanciera un eccezione di tipo Address out of bounds (Guarda key MSVM-5 Jira)
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
            throw new RuntimeException("Illegal Instruction on Address:" + ip-1);
        }
        Instruction instruction=instructionSet.get(opcode);
        if(!instruction)
        {
            throw new RuntimeException("Not implemented");
        }
        instruction.execute(this,memory,stack);
    }

    public void halt(){this.isHalted=true;}
    public int getIp(){return this.ip;}
    public int setIp(int ip){this.ip=ip;}
}
