package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione SHL (Shift Left - Spostamento a Sinistra).
 * <p>
 * Sposta i bit del valore a sinistra di un numero specificato di posizioni.
 * Equivale matematicamente a moltiplicare per potenze di 2.
 * </p>
 *
 * <strong>Logica dello Stack:</strong>
 * <ol>
 * <li>POP quantità (di quanto spostare)</li>
 * <li>POP valore (il numero da spostare)</li>
 * <li>PUSH (valore << quantità)</li>
 * </ol>
 *
 * @author Luca Lupi
 */
public class Shl implements Instruction {

    @Override
    public void execute(ExecutionContext ctx) {
        int a=ctx.pop();
        int b=ctx.pop();
        ctx.push(b<<a);
    }
}
