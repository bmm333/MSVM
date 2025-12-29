package it.uniupo.msvm.core.instructions.impl;
import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione MUL (Moltiplicazione Intera).
 * <p>
 * Esegue la moltiplicazione dei due valori in cima allo stack.
 * </p>
 * * <strong>Logica dello Stack:</strong>
 * <ol>
 * <li>POP operando B</li>
 * <li>POP operando A</li>
 * <li>PUSH (B * A)</li>
 * </ol>
 * @author Luca Lupi
 */
public class Mul implements Instruction {

    @Override
    public void execute(ExecutionContext ctx) {
        int a = ctx.pop();
        int b = ctx.pop();
        ctx.push(b*a);
    }
}
