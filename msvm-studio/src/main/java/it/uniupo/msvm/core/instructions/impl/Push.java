package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione PUSH (Immediato).
 * <p>
 * Questa istruzione non preleva nulla dallo stack, ma legge il dato
 * direttamente dal flusso del bytecode (l'argomento dell'istruzione)
 * e lo inserisce in cima allo stack.
 * </p>
 *
 * <strong>Funzionamento:</strong>
 * <ol>
 * <li>Legge il byte successivo al Program Counter (<code>fetchNextByte</code>).</li>
 * <li>PUSH di quel valore nello Stack.</li>
 * <li>Il Program Counter avanza automaticamente.</li>
 * </ol>
 *
 * @author Luca Lupi
 */
public class Push implements Instruction {
    @Override
    public void execute(ExecutionContext ctx) {
        int a=ctx.fetchNextByte();
        ctx.push(a);
    }
}
