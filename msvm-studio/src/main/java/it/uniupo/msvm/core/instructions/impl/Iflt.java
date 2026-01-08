package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione IFLT (Branch if Less Than).
 * <p>
 * Esegue un salto condizionato se il secondo valore estratto (b) è MINORE
 * del primo valore estratto (a).
 * </p>
 *
 * <strong>Logica dello Stack:</strong>
 * <ol>
 * <li>POP operando A (Termine di destra del confronto)</li>
 * <li>POP operando B (Termine di sinistra del confronto)</li>
 * <li>Legge l'offset di salto (byte successivo).</li>
 * <li>Se <strong>B < A</strong>: Salta all'indirizzo relativo.</li>
 * </ol>
 *
 * @author Luca Lupi
 * @author Arben Mema
 */
public class Iflt implements Instruction {
    @Override
    public void execute(ExecutionContext ctx) {
        int a = ctx.pop();
        int b = ctx.pop();
        int targetAddress = ctx.fetchNextByte();

        if (b < a) {
            ctx.setIp(targetAddress);
        }
    }
}
