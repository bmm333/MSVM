package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

/**
 * Implementazione dell'istruzione DUP (Duplicate).
 * <p>
 * Duplica l'elemento attualmente in cima allo stack (Top Of Stack).
 * </p>
 * <strong>Logica dello Stack:</strong>
 * <ul>
 *     <li>Prima: {@code [..., A]}</li>
 *     <li>Dopo:  {@code [..., A, A]}</li>
 * </ul>
 * <p>
 * Questa istruzione è essenziale nei cicli (es. {@code COUNTDOWN}) per permettere
 * a istruzioni come {@code JZ} di consumare un valore per il controllo, lasciandone
 * una copia intatta per l'iterazione successiva o per calcoli futuri.
 * </p>
 * @author Arben Mema
 */
public class Dup implements Instruction {

    @Override
    public void execute(ExecutionContext ctx) {
        // legge il valore in cima allo stack senza rimuoverlo
        int val = ctx.peek();
        // Lo inseriscie di nuovo duplicandolo
        ctx.push(val);
    }
}