package it.uniupo.msvm.core.exceptions;

import it.uniupo.msvm.exceptions.MsvmException;

/**
 * Classe base astratta per tutti gli errori di esecuzione della Virtual Machine.
 * <p>
 *     Ragruppatutte le anomalie che possono verificarsi durante il ciclo fetch-decode-execute.
 * (es. violazione memoria, stack overflow, opcode illegale).
 * Il {@code VmRunner} catturera questa classe generica per fermare l'esecuzione.
 * e notificera l'interfacia utente in caso di crash.
 *  </p>
 * */

public class VmException extends MsvmException {
    /**
     * Costruisce un'eccezione relativa al runtime della VM.
     *
     * @param message Descrizione dell'errore di esecuzione.
     */
    public VmException(String message) {
        super(message);
    }
}
