package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione SUB (Sottrazione Intera).
 * <p>
 * Esegue la sottrazione tra i due valori in cima allo stack.
 * Rispetta l'ordine logico: <code>(secondo_pop - primo_pop)</code>.
 * </p>
 *
 * <strong>Logica dello Stack:</strong>
 * <ol>
 * <li>POP operando A (Il Sottraendo - quello che sottrae)</li>
 * <li>POP operando B (Il Minuendo - quello da cui si sottrae)</li>
 * <li>PUSH (B - A)</li>
 * </ol>
 *
 * @author Luca Lupi
 */
public class Sub implements Instruction {


    @Override
    public void execute(ExecutionContext ctx) {
        int a = ctx.pop();
        int b = ctx.pop();
        ctx.push(b-a);
    }
}
