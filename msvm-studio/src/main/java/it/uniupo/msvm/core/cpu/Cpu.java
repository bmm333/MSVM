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
 * Rappresenta l'unità centrale di elaborazione (CPU) della macchina virtuale.
 * <p>
 *     La CPU implementa il ciclo fetch-decode-execute ed agisce come {@link it.uniupo.msvm.core.instructions.ExecutionContext}
 *     per le istruzioni, fornendo accesso controllato a memoria e stack.
 * </p>
 */
public class Cpu implements ExecutionContext, VmBackend {
    /** Memoria della macchina virtuale. */
    private final Memory memory;
    /** Stack degli operandi. */
    private final OperandStack stack;
    /** Registro Instruction Pointer (Puntatore all'istruzione successiva). */
    private int ip = 0;
    /** Indica se la CPU è in stato di arresto. */
    private boolean isHalted = false;
    /** Ultimo indirizzo di memoria scritto (utilizzato per l'interfaccia grafica). */
    private int lastWrittenAddress = -1;
    /** Insieme delle istruzioni supportate dalla CPU. */
    private final Map<Opcode, Instruction> instructionSet = new HashMap<>();

    /**
     * Inizializza la CPU con i componenti necessari.
     *
     * @param memory l'istanza della memoria condivisa.
     * @param stack  lo stack degli operandi.
     */
    public Cpu(Memory memory, OperandStack stack) {
        this.memory = memory;
        this.stack = stack;
    }

    /**
     * Registra una nuova istruzione nel set della CPU.
     *
     * @param opcode         l'opcode associato all'istruzione.
     * @param implementation la logica dell'istruzione.
     */
    public void registerInstruction(Opcode opcode, Instruction implementation) {
        instructionSet.put(opcode, implementation);
    }

    /**
     * Avvia il ciclo di esecuzione continuo.
     * Si ferma solo quando viene incontrata un'istruzione HALT o si verifica un errore critico.
     */
    public void run() {
        while (!isHalted) {
            step();
        }
    }

    /**
     * Esegue un singolo ciclo Fetch-Decode-Execute (atomico).
     *
     * @throws it.uniupo.msvm.core.exceptions.VmException se si verifica un errore durante l'esecuzione.
     */
    public void step() {
        if (isHalted) return;

        // Reset del tracking della scrittura all'inizio di ogni step
        this.lastWrittenAddress = -1;

        // 1. FETCH
        if (ip < 0 || ip >= memory.sizeMemory()) {
            throw new MemoryAccessException("Segmentation Fault: IP fuori dai limiti", ip, MemoryAccessException.AccessType.READ);
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
            throw new OpcodeException(opcodeByte);
        }

        // 3. EXECUTE
        instruction.execute(this);
    }

    /**
     * Resetta lo stato della CPU (IP, stato di halt, stack).
     */
    public void reset() {
        this.ip = 0;
        this.isHalted = false;
        this.lastWrittenAddress = -1;
        this.stack.clear();
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
