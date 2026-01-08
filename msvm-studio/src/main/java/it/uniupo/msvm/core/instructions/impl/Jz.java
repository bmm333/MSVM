package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione JZ (Jump if Zero).
 * <p>
 * Esegue un salto condizionato all'indirizzo di memoria specificato se il valore
 * estratto dallo stack è uguale a 0.
 * </p>
 * <strong>Funzionamento:</strong>
 * <ol>
 *     <li>Preleva un valore dallo stack (POP).</li>
 *     <li>Legge sempre il byte successivo (l'indirizzo target).</li>
 *     <li>Se il valore prelevato è 0, imposta l'Instruction Pointer (IP) al target.</li>
 * </ol>
 *  @author Luca Lupi (DATED VERSION)
 *  Modified by @author Arben Mema <strong>Nota:</strong> Utilizza indirizzamento ASSOLUTO (non offset relativo).
 * @version 1.1
 */
public class Jz implements Instruction {

    @Override
    public void execute(ExecutionContext ctx) {
        int a = ctx.pop();
        int targetAddress = ctx.fetchNextByte(); // Legge indirizzo assoluto

        if (a == 0) {
            ctx.setIp(targetAddress);
        }
    }
}
