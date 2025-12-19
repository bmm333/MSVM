package it.uniupo.msvm.core.memory;

import it.uniupo.msvm.core.exceptions.MemoryAccessException;

public interface Memoryinterface {
    public int read(int address) throws MemoryAccessException;
    public void write(int address, int value) throws MemoryAccessException;
    public int sizeMemory();
    public void clear();
    public void loadProgram(int []program);

}
