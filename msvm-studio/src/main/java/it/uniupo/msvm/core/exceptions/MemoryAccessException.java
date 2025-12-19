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

    // Costruttore principale
    public MemoryAccessException(int address, AccessType type) {
        // Genera un messaggio automatico dettagliato
        super(String.format("Segmentation Fault: Tentativo di %s all'indirizzo non valido [0x%04X] (%d)",
                type.name(), address, address));

        this.address = address;
        this.type = type;
    }

    // Costruttore per messaggi custom (se serve qualcosa di specifico)
    public MemoryAccessException(String message, int address, AccessType type) {
        super(message);
        this.address = address;
        this.type = type;
    }

    // Getters: permettono alla CPU o al Debugger di recuperare i dati dell'errore
    public int getAddress() {
        return address;
    }

    public AccessType getType() {
        return type;
    }
}
