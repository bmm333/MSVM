package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione HALT.
 * <p>
 * Segnala alla macchina virtuale di arrestare immediatamente l'esecuzione.
 * Invoca il metodo <code>halt()</code> sul contesto di esecuzione.
 * </p>
 *  @author Luca Lupi
 */
public class Halt implements Instruction {
    @Override
    public void execute(ExecutionContext ctx) {
        ctx.halt();
    }
}
