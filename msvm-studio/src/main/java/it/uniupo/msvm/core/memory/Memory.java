package it.uniupo.msvm.core.memory;

import java.util.Arrays;
import it.uniupo.msvm.core.exceptions.MemoryAccessException;

/**
 * Classe dove crea e gestisce la memoria virtuale della nostra macchina virtuale.
 * La classe implementa un interfaccia con delle istruzioni base che servono alla nostra CPU
 * per manipolare i dati presenti.
 */
public class Memory implements MemoryInterface{
    private final int size;
    private final int[] data;

    /**
     * Costruttore che fa il setup della nostra memoria virtuale
     * @param size Grandezza della nostra memoria Ram
     */
    public Memory(int size) {
        this.size = size;
        this.data = new int[this.size];
    }


    /**
     * Istruzione read dove reperisce il dato attraverso il suo address
     * @param address indirizzo di memoria che vuoi leggere
     * @return Ritorna il dato che vuoi leggere
     * @throws MemoryAccessException indirizzo non valido o valore negativo o maggiore della size
     */
    @Override
    public int read(int address) throws MemoryAccessException {
        if(address < 0 || address >= this.size){
            throw new MemoryAccessException("Invalid index",address, MemoryAccessException.AccessType.READ);
        }
        else{
            return this.data[address];
        }
    }

    /**
     * Istruzione write dove scrive il dato attraverso il suo address
     * @param address indirizzo di memoria che vuoi leggere
     * @param value il valore che vuoi scrivere
     * @throws MemoryAccessException ndirizzo non valido o valore negativo o maggiore della size
     */
    @Override
    public void write(int address, int value) throws MemoryAccessException {
        if(address < 0 || address >= this.size){
            throw new MemoryAccessException("Invalid index",address, MemoryAccessException.AccessType.WRITE);
        }
        else{
            this.data[address] = value;
        }
    }


    /**
     * Ritorna la grandezza della memoria virtuale
     * @return restituisce la size della memoria
     */
    @Override
    public int sizeMemory() {return size;}

    /**
     * Pulisce la memoria da i tutti i dati presenti
     */
    @Override
    public void clear() {
       Arrays.fill(data,0);
    }

    /**
     * Carica la memoria con il programma che vuoi eseguire
     * @param program programma con le istruzioni esdecimali
     * @throws MemoryAccessException Programma troppo grande per la nostra memoria virtuale
     */
    @Override
    public void loadProgram(int[] program) throws MemoryAccessException {
        if(program.length > this.size) {
            throw new MemoryAccessException(-1,MemoryAccessException.AccessType.EXECUTE);
        }
        else {
            System.arraycopy(program, 0, this.data, 0, program.length);
        }

    }
}
