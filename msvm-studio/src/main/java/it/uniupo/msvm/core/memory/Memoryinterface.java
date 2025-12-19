package it.uniupo.msvm.core.memory;

import it.uniupo.msvm.core.exceptions.MemoryAccessException;

/**
 * Classe interfaccia dove dichiaro i metodi il quale la nsota cpu puo usare
 * per manipolare i dati della memoria virtuale
 */
public interface Memoryinterface {
    public int read(int address) throws MemoryAccessException;
    public void write(int address, int value) throws MemoryAccessException;
    public int sizeMemory();
    public void clear();
    public void loadProgram(int []program) throws MemoryAccessException;

}
