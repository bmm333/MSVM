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
    /** Stack interno utilizzato per memorizzare i dati. */
    private final Stack<Integer> internalStack = new Stack<>();
    /** Dimensione massima dello stack. */
    private final int maxSize;

    /**
     * Crea uno stack con una dimensione massima prefissata.
     *
     * @param maxSize il numero massimo di interi che lo stack può contenere.
     */
    public OperandStack(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Inserisce un valore in cima allo stack.
     *
     * @param value il valore intero da inserire.
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
     *
     * @return il valore estratto.
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
     *
     * @return il valore in cima.
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
     *
     * @return la dimensione dello stack.
     */
    public int size() {
        return internalStack.size();
    }

    /**
     * Restituisce una copia dei dati dello stack per debug o UI.
     * È importante che sia una copia per garantire la thread-safety.
     *
     * @return una lista contenente una copia degli elementi dello stack.
     */
    public List<Integer> getStackDump() {
        return new ArrayList<>(internalStack);
    }

    /**
     * Restituisce una copia istantanea degli elementi per lo Snapshot.
     * <p>
     * Nota: Restituire `internalStack` direttamente sarebbe pericoloso perché la UI
     * potrebbe leggerlo mentre la CPU scrive. Viene creata una copia indipendente.
     * </p>
     *
     * @return una lista contenente gli elementi (dal fondo alla cima).
     */
    public List<Integer> getElements() {
        return new ArrayList<>(internalStack);
    }

    /**
     * Pulisce lo stack rimuovendo tutti gli elementi.
     */
    public void clear() {
        internalStack.clear();
    }
}