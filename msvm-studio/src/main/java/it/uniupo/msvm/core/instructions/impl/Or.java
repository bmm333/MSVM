package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;
/**
 * Implementazione dell'istruzione OR (Bitwise OR).
 * <p>
 * Esegue un'operazione logica OR inclusivo bit a bit tra i due valori in cima allo stack.
 * </p>
 * * <strong>Logica dello Stack:</strong>
 * <ol>
 * <li>POP valore B</li>
 * <li>POP valore A</li>
 * <li>PUSH (B | A)</li>
 * </ol>
 *
 * @author Luca Lupi
 */
public class Or implements Instruction {
    @Override
    public void execute(ExecutionContext ctx) {
        int a = ctx.pop();
        int b = ctx.pop();
        ctx.push(b|a);
    }
}
