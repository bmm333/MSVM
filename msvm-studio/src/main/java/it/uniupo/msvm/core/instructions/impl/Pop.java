package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione POP.
 * <p>
 * Rimuove l'elemento in cima allo stack scartandolo.
 * Utile per pulire lo stack da valori non più necessari.
 * </p>
 * * <strong>Logica dello Stack:</strong>
 * <ol>
 * <li>POP (valore perso)</li>
 * </ol>
 * @author Luca Lupi
 */
public class Pop implements Instruction {

    @Override
    public void execute(ExecutionContext ctx) {
        ctx.pop();
    }
}
