package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

public class Pop implements Instruction {

    @Override
    public void execute(ExecutionContext ctx) {
        ctx.pop();
    }
}
