package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

public class Add implements Instruction {

    @Override
    public void execute(ExecutionContext ctx) {
        int a= ctx.pop();
        int b= ctx.pop();
        ctx.push(a+b);
    }
}
