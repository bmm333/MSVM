package it.uniupo.msvm.core.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class OperandStack {
    private final Stack<Integer> internalStack = new Stack<>();

    public void push(int value) {
        internalStack.push(value);
    }

    public int pop() {
        if (internalStack.isEmpty()) throw new RuntimeException("Stack Underflow");
        return internalStack.pop();
    }
    /**
     * Restituisce una copia dello stack come Lista.
     * Utile per la visualizzazione nella UI.
     */
    public List<Integer> getStackDump() {
        // Crea una nuova lista copiando gli elementi attuali dello stack
        return new ArrayList<>(internalStack);
    }
}