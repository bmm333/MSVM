package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;
/**
 * Implementazione dell'istruzione ADD (Addizione Intera).
 * <p>
 * Questa istruzione preleva (pop) i due valori in cima allo stack,
 * li somma tra loro e inserisce (push) il risultato nello stack.
 * </p>
 * * <strong>Logica dello Stack:</strong>
 * <ol>
 * <li>POP operando B</li>
 * <li>POP operando A</li>
 * <li>PUSH (B + A)</li>
 * </ol>
 *  @author Luca Lupi
 */
public class Add implements Instruction {

    @Override
    public void execute(ExecutionContext ctx) {
        int a= ctx.pop();
        int b= ctx.pop();
        ctx.push(b+a);
    }
}
