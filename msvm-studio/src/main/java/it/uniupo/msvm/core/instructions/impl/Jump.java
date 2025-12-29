package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione JUMP (Salto Incondizionato Relativo).
 * <p>
 * Modifica il Program Counter (IP) aggiungendo un offset letto dal bytecode.
 * Il salto è relativo all'istruzione successiva.
 * </p>
 * * <strong>Funzionamento:</strong>
 * <ol>
 * <li>Legge il byte successivo come offset (signed).</li>
 * <li>Calcola: <code>nuovo_IP = (IP_corrente + 1) + offset</code>.</li>
 * <li>Aggiorna l'IP nel contesto.</li>
 * </ol>
 *  @author Luca Lupi
 */
public class Jump implements Instruction {

    @Override
    public void execute(ExecutionContext ctx) {
        int jump=ctx.fetchNextByte();
        int ip= ctx.getIp();
        ctx.setIp((ip+1)+jump);
    }
}
