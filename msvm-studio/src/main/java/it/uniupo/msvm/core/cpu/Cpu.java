package it.uniupo.msvm.core.cpu;
import it.uniupo.msvm.core.exceptions.MemoryAccessException;
import it.uniupo.msvm.core.exceptions.OpcodeException;
import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;
import it.uniupo.msvm.core.instructions.Opcode;
import it.uniupo.msvm.core.memory.Memory;
import it.uniupo.msvm.core.memory.OperandStack;
import it.uniupo.msvm.core.runtime.VmBackend;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Rappresentera l'unita centrale di elaborazione (CPU) dela macchina virtuale
 * <p>
 *     La cpu implemnta il ciclo fetch-decode-execute ed agisce come {@link it.uniupo.msvm.core.instructions.ExecutionContext }
 *     per le istruzioni, fornendo accesso controllato a memoria e stack.
 * </p>
 * */

public class Cpu implements ExecutionContext, VmBackend {
    private final Memory memory;
    private final OperandStack stack;
    //Registri
    private int ip=0; //Instruction Pointer
    private boolean isHalted=false;
    //Stato per UI (Snapshot)
    private int lastWrittenAddress=-1; //-1 indica nessuna scrittura recente
    //Strategy Map per decodificare le istruzioni.
    private final Map<Opcode, Instruction>instructionSet=new HashMap<>();

    /**
     * Inizializza la CPU con i componenti necessari.
     *
     * @param memory L'istanza della memoria condivisa.
     * @param stack  Lo stack degli operandi.
     */
    public Cpu(Memory memory, OperandStack stack) {
        this.memory = memory;
        this.stack = stack;
    }
    /**
     * Registra una nuova istruzione nel set della CPU.
     * @param opcode L'opcode associato all'istruzione.
     * @param implementation La logica dell'istruzione.
     */
    public void registerInstruction(Opcode opcode,Instruction implementation)
    {
        instructionSet.put(opcode,implementation);
    }
    /**
     * Avvia il ciclo di esecuzione continuo.
     * Si ferma solo quando viene incontrata un'istruzione HALT o si verifica un errore critico.
     */
    public void run()
    {
        while(!isHalted)
        {
            step();
        }
    }

    /**
     * Esegue un singolo ciclo Fetch-Decode-Execute (Atomico).
     *
     * @throws it.uniupo.msvm.core.exceptions.VmException Se si verifica un errore durante l'esecuzione (es. Memory Fault, Opcode illegale).
     */
    public void step() {
        if (isHalted) return;

        //resta il tracking della scrittura all inizio di ogni step
        //se questa istr non scrive in memoria la ui non deve evidenzionare nulla.
        this.lastWrittenAddress=-1;

        // 1. FETCH
        if (ip < 0 || ip >= memory.sizeMemory()) {
            throw new MemoryAccessException("Segmentation Fault: IP out of bounds",ip, MemoryAccessException.AccessType.READ);
        }
        int opcodeByte = memory.read(ip);
        ip++; // Incremento IP dopo la lettura dell'opcode

        // 2. DECODE
        Opcode opcode;
        try {
            opcode = Opcode.fromByte(opcodeByte);
        } catch (IllegalArgumentException e) {
            throw new OpcodeException(opcodeByte);
        }

        Instruction instruction = instructionSet.get(opcode);
        if (instruction == null) {
            throw new OpcodeException(opcodeByte); // O una NotImplementedException specifica
        }

        // 3. EXECUTE
        // Passiamo 'this' perché Cpu implementa ExecutionContext
        instruction.execute(this);
    }
    /**
     * Resetta lo stato della CPU utile per il tasto Stop/Reset
     * */
    public void reset()
    {
        this.ip=0;
        this.isHalted=false;
        this.lastWrittenAddress=-1;
        this.stack.clear(); //aggiungo anche il metodo
        // Nota: La memoria di solito non si resetta qui, ma la si sovrascrive caricando un nuovo programma.
    }

    // --- IMPLEMENTAZIONE EXECUTION CONTEXT ---

    @Override
    public void push(int value) {
        stack.push(value);
    }

    @Override
    public int pop() {
        return stack.pop();
    }

    @Override
    public int peek() {
        return stack.peek();
    }

    @Override
    public int readMemory(int address) {
        // Memory.read dovrebbe già lanciare MemoryAccessException, ma per sicurezza:
        return memory.read(address);
    }

    @Override
    public void writeMemory(int address, int value) {
        memory.write(address, value);
        this.lastWrittenAddress=address;
    }

    @Override
    public int fetchNextByte() {
        if (ip >= memory.sizeMemory()) {
            throw new MemoryAccessException(ip, MemoryAccessException.AccessType.READ);
        }
        int value = memory.read(ip);
        ip++;
        return value;
    }

    @Override
    public void halt() {
        this.isHalted = true;
    }

    @Override
    public int getIp() {
        return ip;
    }

    /**
     * @return
     */
    @Override
    public List<Integer> getStackSnapshot() {
        return stack.getElements();
    }

    /**
     * @return
     */
    @Override
    public int[] getMemorySnapshot() {
        return memory.getMemoryDump();
    }

    @Override
    public void setIp(int address) {
        if (address < 0 || address >= memory.sizeMemory()) {
            throw new MemoryAccessException("Jump target invalido", address, MemoryAccessException.AccessType.EXECUTE);
        }
        this.ip = address;
    }
    /**
     * Restituisce lo stato di arresto della cpu
     * Fondamentale per il loop del VmRunner e per i test
     * */
    public boolean isHalted() {
        return isHalted;
    }

    // --Metodi Per Snapshot & UI
    public int[] getMemoryCopy()
    {
        return memory.getMemoryDump();
    }
    public List<Integer> getStackCopy()
    {
        return stack.getElements();
    }
    public int getLastWrittenAddress(){
        return lastWrittenAddress;
    }


}
