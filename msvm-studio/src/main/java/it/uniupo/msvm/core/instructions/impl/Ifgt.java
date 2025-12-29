package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione IFGT (Branch if Greater Than).
 * <p>
 * Esegue un salto condizionato se il secondo valore estratto (b) è MAGGIORE
 * del primo valore estratto (a).
 * </p>
 *
 * <strong>Logica dello Stack:</strong>
 * <ol>
 * <li>POP operando A (Termine di destra del confronto)</li>
 * <li>POP operando B (Termine di sinistra del confronto)</li>
 * <li>Legge l'offset di salto (byte successivo).</li>
 * <li>Se <strong>B > A</strong>: Salta all'indirizzo relativo.</li>
 * </ol>
 *
 * @author Luca Lupi
 */
public class Ifgt implements Instruction {
    @Override
    public void execute(ExecutionContext ctx) {
        int a = ctx.pop();
        int b= ctx.pop();
        int jump=ctx.fetchNextByte();
        if(b>a){

            int ip= ctx.getIp();
            ctx.setIp((ip+1)+jump);
        }
    }
}
