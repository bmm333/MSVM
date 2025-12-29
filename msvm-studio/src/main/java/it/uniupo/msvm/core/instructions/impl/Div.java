package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione DIV (Divisione Intera).
 * <p>
 * Divide il primo valore estratto dallo stack per il secondo valore estratto.
 * </p>
 * * <strong>Logica dello Stack:</strong>
 * <ol>
 * <li>POP operando A (Dividendo)</li>
 * <li>POP operando B (Divisore)</li>
 * <li>PUSH (A / B)</li>
 * </ol>
 * * @throws ArithmeticException se il divisore (secondo pop) è 0.
 * @author Luca Lupi
 */
public class Div implements Instruction {

    @Override
    public void execute(ExecutionContext ctx) {
        int a = ctx.pop();
        int b = ctx.pop();
        if(b==0){
            throw new ArithmeticException("Division by zero");
        }
        ctx.push(a/b);
    }
}
