package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

public class Div implements Instruction {

    @Override
    public void execute(ExecutionContext ctx) {
        int a = ctx.pop();
        int b = ctx.pop();
        if(b==0){
            throw new ArithmeticException("Division by zero");
        }
        ctx.push(a/b);
    }
}
