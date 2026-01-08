package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione JUMP (Salto Incondizionato).
 * <p>
 * Modifica il flusso di esecuzione impostando l'Instruction Pointer (IP)
 * a un nuovo indirizzo specifico.
 * </p>
 * <strong>Funzionamento:</strong>
 * <ol>
 *     <li>Legge il byte successivo come indirizzo di destinazione (Target Address).</li>
 *     <li>Sovrascrive l'IP corrente con questo indirizzo.</li>
 * </ol>
 *  @author Luca Lupi (DATED VERSION)
 *  Modified by @author Arben Mema <strong>Nota:</strong> Utilizza indirizzamento ASSOLUTO. L'Assembler risolve le label
 *  @version 1.1
 */
public class Jump implements Instruction {

    @Override
    public void execute(ExecutionContext ctx) {
        // L'assembler fornisce l'indirizzo ASSOLUTO della label, non un offset.
        int targetAddress = ctx.fetchNextByte();
        ctx.setIp(targetAddress);
    }
}