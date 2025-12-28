package it.uniupo.msvm.core.instructions.impl;

import it.uniupo.msvm.core.instructions.ExecutionContext;
import it.uniupo.msvm.core.instructions.Instruction;

public class Jump implements Instruction {

    @Override
    public void execute(ExecutionContext ctx) {
        int jump=ctx.fetchNextByte();
        int ip= ctx.getIp();
        ctx.setIp((ip+1)+jump);
    }
}
