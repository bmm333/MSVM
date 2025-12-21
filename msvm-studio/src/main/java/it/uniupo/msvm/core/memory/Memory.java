package it.uniupo.msvm.core.memory;

public class Memory {
    private final int[] data;

    public Memory(int size) {
        this.data = new int[size];
    }

    public int read(int address) {
        if (address < 0 || address >= data.length) throw new IndexOutOfBoundsException();
        return data[address];
    }

    public void write(int address, int value) {
        if (address < 0 || address >= data.length) throw new IndexOutOfBoundsException();
        data[address] = value;
    }

    public int getSize() {
        return data.length;
    }
}