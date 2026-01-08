package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;
/**
 * Implementazione di IFGT (Branch if Greater Than).
 * <p>
 * Salta all'indirizzo target se il secondo valore estratto è maggiore del primo (B > A).
 * Utilizza indirizzamento ASSOLUTO.
 * </p>
 *
 * @author Luca Lupi
 */
public class Ifgt implements Instruction {
    @Override
    public void execute(ExecutionContext ctx) {
        int a = ctx.pop();
        int b = ctx.pop();
        int targetAddress = ctx.fetchNextByte();

        if (b > a) {
            ctx.setIp(targetAddress);
        }
    }
}