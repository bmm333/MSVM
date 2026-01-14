package it.uniupo.msvm.core.memory;

import java.util.Arrays;
import it.uniupo.msvm.core.exceptions.MemoryAccessException;

/**
 * Gestisce la memoria virtuale della nostra macchina virtuale.
 * La classe implementa un'interfaccia con istruzioni base utilizzate dalla CPU
 * per manipolare i dati.
 */
public class Memory implements MemoryInterface {
    /** Dimensione della memoria. */
    private final int size;
    /** Array che contiene i dati della memoria. */
    private final int[] data;

    /**
     * Costruttore che inizializza la memoria virtuale.
     *
     * @param size dimensione della memoria RAM.
     */
    public Memory(int size) {
        this.size = size;
        this.data = new int[this.size];
    }

    /**
     * Legge un dato all'indirizzo specificato.
     *
     * @param address indirizzo di memoria da leggere.
     * @return il valore letto.
     * @throws MemoryAccessException se l'indirizzo non è valido (negativo o fuori dai limiti).
     */
    @Override
    public int read(int address) throws MemoryAccessException {
        if (address < 0 || address >= this.size) {
            throw new MemoryAccessException("Indice non valido", address, MemoryAccessException.AccessType.READ);
        } else {
            return this.data[address];
        }
    }

    /**
     * Restituisce una copia sicura dei dati della memoria (dump).
     *
     * @return un array contenente una copia dei dati della memoria.
     */
    public int[] getMemoryDump() {
        return data.clone();
    }

    /**
     * Scrive un valore all'indirizzo specificato.
     *
     * @param address indirizzo di memoria dove scrivere.
     * @param value   il valore da scrivere.
     * @throws MemoryAccessException se l'indirizzo non è valido (negativo o fuori dai limiti).
     */
    @Override
    public void write(int address, int value) throws MemoryAccessException {
        if (address < 0 || address >= this.size) {
            throw new MemoryAccessException("Indice non valido", address, MemoryAccessException.AccessType.WRITE);
        } else {
            this.data[address] = value;
        }
    }

    /**
     * Restituisce la dimensione della memoria.
     *
     * @return la dimensione della memoria.
     */
    @Override
    public int sizeMemory() {
        return size;
    }

    /**
     * Pulisce la memoria impostando tutti i valori a zero.
     */
    @Override
    public void clear() {
        Arrays.fill(data, 0);
    }

    /**
     * Carica un programma in memoria.
     *
     * @param program array contenente le istruzioni del programma.
     * @throws MemoryAccessException se il programma è troppo grande per la memoria disponibile.
     */
    @Override
    public void loadProgram(int[] program) throws MemoryAccessException {
        if (program.length > this.size) {
            throw new MemoryAccessException(-1, MemoryAccessException.AccessType.EXECUTE);
        } else {
            System.arraycopy(program, 0, this.data, 0, program.length);
        }
    }
}
