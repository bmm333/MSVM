package it.uniupo.msvm.core.memory;

import it.uniupo.msvm.core.exceptions.MemoryAccessException;

public class Memory {
    private final int[] data;

    public Memory(int size) {
        this.data = new int[size];
    }

    public int read(int address) {
        if (address < 0 || address >= data.length) {
            throw new MemoryAccessException(address, MemoryAccessException.AccessType.READ);
        }
        return data[address];
    }

    public void write(int address, int value) {
        if (address < 0 || address >= data.length) {
            throw new MemoryAccessException(address, MemoryAccessException.AccessType.WRITE);
        }
        data[address] = value;
    }

    private void checkAddress(int address) {
        if (address < 0 || address >= data.length) {
            throw new MemoryAccessException(address, MemoryAccessException.AccessType.READ);
        }
    }
    public int getSize() {
        return data.length;
    }
}