package it.uniupo.msvm.core.exceptions;

public class MemoryAccessException extends Exception { // Meglio Exception (Checked) di RuntimeException

    // Enumerazione interna per definire il tipo di operazione
    public enum AccessType {
        READ,
        WRITE,
        EXECUTE // Opzionale, se gestisci codice e dati separatamente
    }

    private final int address;
    private final AccessType type;

    /**
     * Costruttore per l'eccezione della nostra memoria
     * @param address indirizzo dove da il problema
     * @param type Tipo di errore( READ,WRITE o EXECUTE).
     */
    public MemoryAccessException(int address, AccessType type) {
        // Genera un messaggio automatico dettagliato
        super(String.format("Segmentation Fault: Tentativo di %s all'indirizzo non valido [0x%04X] (%d)",
                type.name(), address, address));

        this.address = address;
        this.type = type;
    }

    /**
     * Costruttore per l'eccezione della nostra memoria
     * @param message Messaggio di errore
     * @param address indirizzo dove da il problema
     * @param type Tipo di errore( READ,WRITE o EXECUTE).
     */
    public MemoryAccessException(String message, int address, AccessType type) {
        super(message);
        this.address = address;
        this.type = type;
    }

    /**
     * Getters: permette di recuperare l'indirizzo
     */
    public int getAddress() {
        return address;
    }

    /**
     * Getter: permette di recuperare il type
     */
    public AccessType getType() {
        return type;
    }
}
