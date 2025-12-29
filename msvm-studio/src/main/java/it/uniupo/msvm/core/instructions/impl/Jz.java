package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione JZ (Jump if Zero).
 * <p>
 * Esegue un salto condizionato solo se il valore in cima allo stack è 0.
 * </p>
 * * <strong>Logica:</strong>
 * <ol>
 * <li>POP valore A dallo stack.</li>
 * <li>Se A == 0: Legge l'offset ed esegue il salto (come in JUMP).</li>
 * <li>Se A != 0: Ignora il salto e prosegue (l'offset non viene letto/consumato qui, attenzione all'allineamento IP).</li>
 * </ol>
 *  @author Luca Lupi
 */
public class Jz implements Instruction {

    @Override
    public void execute(ExecutionContext ctx) {
        int a = ctx.pop();
        if(a == 0){
            int jump=ctx.fetchNextByte();
            int ip= ctx.getIp();
            ctx.setIp((ip+1)+jump);
        }
    }
}
