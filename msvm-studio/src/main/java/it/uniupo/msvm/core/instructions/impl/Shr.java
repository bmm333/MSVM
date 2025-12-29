package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione SHR (Arithmetic Shift Right - Spostamento a Destra).
 * <p>
 * Sposta i bit del valore a destra mantenendo il segno (Arithmetic Shift).
 * Equivale matematicamente a dividere per potenze di 2.
 * </p>
 *
 * <strong>Logica dello Stack:</strong>
 * <ol>
 * <li>POP quantità (di quanto spostare)</li>
 * <li>POP valore (il numero da spostare)</li>
 * <li>PUSH (valore >> quantità)</li>
 * </ol>
 *
 * @author Luca Lupi
 */
public class Shr implements Instruction {
    @Override
    public void execute(ExecutionContext ctx) {
        int a=ctx.pop();
        int b=ctx.pop();
        ctx.push(b >> a);
    }
}
