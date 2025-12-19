package it.uniupo.msvm.core.memory;

import java.util.Arrays;
import it.uniupo.msvm.core.exceptions.MemoryAccessException;

public class Memory implements Memoryinterface{
    private int size;
    private int[] data;

    public Memory() {
        this.size = 512;
        this.data = new int[this.size];
    }


    @Override
    public int read(int address) throws MemoryAccessException {
        if(address < 0 || address >= this.size){
            throw new MemoryAccessException("Invalid index",address, MemoryAccessException.AccessType.READ);
        }
        else{
            return this.data[address];
        }
    }

    @Override
    public void write(int address, int value) throws MemoryAccessException {
        if(address < 0 || address >= this.size){
            throw new MemoryAccessException("Invalid index",address, MemoryAccessException.AccessType.WRITE);
        }
        else{
            this.data[address] = value;
        }
    }


    @Override
    public int sizeMemory() {return size;}

    @Override
    public void clear() {
       Arrays.fill(data,0);
    }

    @Override
    public void loadProgram(int[] program) {
        if(program.length != this.size) {
            throw new IndexOutOfBoundsException("invalid program size");
        }
        else {
            System.arraycopy(program, 0, this.data, 0, this.size);
        }

    }
}
