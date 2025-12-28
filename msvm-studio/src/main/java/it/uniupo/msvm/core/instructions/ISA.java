package it.uniupo.msvm.core.instructions;

public class ISA implements Instruction {
    private final Opcode opcode;

    public ISA() {
        this.opcode = null;
    }

    @Override
    public void execute(ExecutionContext ctx) {

    }
}
