package it.uniupo.msvm.core.runtime;


import it.uniupo.msvm.core.exceptions.VmException;

import java.util.List;

/**
 * Interfaccia di astrazione per il backend di esecuzione.
 * <p>
 * Disaccoppia il {@link VmRunner} dall'implementazione concreta della CPU.
 * Permette di pilotare qualsiasi entità che supporti il ciclo di fetch-decode-execute.
 * </p>
 */
public interface VmBackend {
    /** Esegue un singolo ciclo macchina. */
    void step() throws VmException;

    /** Verifica se la macchina si è arrestata (HALT). */
    boolean isHalted();

    int getIp();

    List<Integer> getStackSnapshot();

    int[] getMemorySnapshot();

    int getLastWrittenAddress();

    void reset();
}
