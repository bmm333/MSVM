package it.uniupo.msvm.core.memory;

import it.uniupo.msvm.core.exceptions.StackException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
/**
 * Rappresenta lo stack degli operandi della VM.
 * <p>
 * Gestisce le operazioni LIFO (Last-In, First-Out) con controlli rigorosi
 * sui limiti di capacità (Overflow) e presenza dati (Underflow).
 * </p>
 */
public class OperandStack {
    private final Stack<Integer> internalStack = new Stack<>();
    private final int maxSize;

    /**
     * Crea uno stack con una dimensione massima prefissata.
     * @param maxSize Il numero massimo di interi che lo stack può contenere.
     */
    public OperandStack(int maxSize) {
        this.maxSize = maxSize;
    }
    /**
     * Inserisce un valore in cima allo stack.
     * @param value Il valore intero da inserire.
     * @throws StackException se lo stack ha raggiunto la capacità massima.
     */
    public void push(int value) {
        if (internalStack.size() >= maxSize) {
            throw StackException.overflow(maxSize);
        }
        internalStack.push(value);
    }

    /**
     * Rimuove e restituisce il valore in cima allo stack.
     * @return Il valore estratto.
     * @throws StackException se lo stack è vuoto.
     */
    public int pop() {
        if (internalStack.isEmpty()) {
            throw StackException.underflow();
        }
        return internalStack.pop();
    }

    /**
     * Restituisce il valore in cima allo stack senza rimuoverlo.
     * @return Il valore in cima.
     * @throws StackException se lo stack è vuoto.
     */
    public int peek() {
        if (internalStack.isEmpty()) {
            throw StackException.underflow();
        }
        return internalStack.peek();
    }
    /**
     * Restituisce il numero di elementi attuali nello stack.
     */
    public int size() {
        return internalStack.size();
    }
    /**
     * Ritorna una copia della lista per debug o UI.
     * È importante che sia una copia per la thread-safety dello Snapshot.
     */
    public List<Integer> getStackDump() {
        return new ArrayList<>(internalStack);
    }

    /**
     * Restituisce una copia istantanea degli elementi per lo Snapshot (Thread-Safe logic).
     * <p>
     * Nota: Restituire `internalStack` direttamente sarebbe pericoloso perché la UI
     * potrebbe leggerlo mentre la CPU scrive. `new ArrayList<>(...)` crea una copia indipendente.
     * </p>
     * @return Una lista contenente gli elementi (dal fondo alla cima).
     */
    public List<Integer> getElements() {
        return new ArrayList<>(internalStack);
    }

    public void clear() {
        internalStack.clear();
    }
}