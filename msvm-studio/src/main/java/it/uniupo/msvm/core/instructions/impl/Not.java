package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione NOT (Bitwise Complement).
 * <p>
 * Questa è un'operazione unaria: preleva un solo valore dallo stack
 * e ne inverte tutti i bit (Complemento a uno).
 * </p>
 * * <strong>Logica dello Stack:</strong>
 * <ol>
 * <li>POP valore A</li>
 * <li>PUSH (~A)</li>
 * </ol>
 * @author Luca Lupi
 */
public class Not implements Instruction {
    @Override
    public void execute(ExecutionContext ctx) {
        int a = ctx.pop();
        ctx.push(~a);
    }
}
