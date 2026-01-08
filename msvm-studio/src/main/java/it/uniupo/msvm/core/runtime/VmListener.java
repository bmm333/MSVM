package it.uniupo.msvm.core.runtime;

public interface VmListener {
    /**
     * Chiamato ogni volta che la VM completa un ciclo (step).
     * @param snapshot La foto dello stato attuale (safe per la UI).
     */
    void onVmUpdate(VmStateSnapshot snapshot);

    /**
     * Chiamato se la VM crasha o incontra un errore critico.
     */
    void onVmError(String message);

    /**
     * Chiamato quando la CPU incontra l'istruzione HALT
     * e termina l'esecuzione regolarmente.
     */
    void onVmHalt();
}