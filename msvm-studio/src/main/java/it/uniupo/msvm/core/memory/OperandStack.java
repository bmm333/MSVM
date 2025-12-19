package it.uniupo.msvm.core.memory;

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
}