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

    public int peek() {
        if (internalStack.isEmpty()) throw new RuntimeException("Stack Empty");
        return internalStack.peek();
    }

    /**
     * Ritorna una copia della lista per debug o UI.
     * È importante che sia una copia per la thread-safety dello Snapshot.
     */
    public List<Integer> getStackDump() {
        return new ArrayList<>(internalStack);
    }

    // Questo metodo serve alla CPU per lo snapshot
    public List<Integer> getElements() {
        return new ArrayList<>(internalStack);
    }

    public void clear() {
        internalStack.clear();
    }
}