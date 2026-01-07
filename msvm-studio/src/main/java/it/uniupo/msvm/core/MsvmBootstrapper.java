package it.uniupo.msvm.core;

import it.uniupo.msvm.core.assembler.Assembler;
import it.uniupo.msvm.core.cpu.Cpu;
import it.uniupo.msvm.core.instructions.Opcode;
import it.uniupo.msvm.core.memory.Memory;
import it.uniupo.msvm.core.memory.OperandStack;
import it.uniupo.msvm.core.runtime.VmRunner;
import it.uniupo.msvm.core.instructions.Opcode;
import it.uniupo.msvm.core.instructions.impl.*;
/**
 * Bootstrapper (Factory)
 * <p>
 *     Responsabile dell'inizializazione e cablaggio (wiring) delle dipendenze.
 *     In un framework come Spring questo sarebbe il @Configuration.
 * </p>
 * */
public class MsvmBootstrapper {
    //Config di default
    private static final int RAM_SIZE = 4096;
    private static final int STACK_SIZE = 512;
    private static final int DEFAULT_CLOCK_HZ = 10;

    /**
     * Crea un'istanza di produzione pronta al uso
     * */
    public static MsvmService buildProductionInstance(){
        //Hardware layers
        Memory memory = new Memory(RAM_SIZE);
        OperandStack stack = new OperandStack(STACK_SIZE);
        Cpu cpu = new Cpu(memory, stack);
        //microcode(instruction set registration)
        registerInstructionSet(cpu);
        //runtime
        VmRunner runner=new VmRunner(cpu); //polimorfismo vmrunner accetta vmbackend
        runner.setFrequency(DEFAULT_CLOCK_HZ);
        Assembler assembler=new Assembler();
        //service injection
        return new MsvmService(memory, cpu, runner, assembler);

    }
    private static void registerInstructionSet(Cpu cpu){
        //stack ops
        cpu.registerInstruction(Opcode.PUSH, new Push());
        cpu.registerInstruction(Opcode.POP, new Pop());
        //arithmetic ops
        cpu.registerInstruction(Opcode.ADD, new Add());
        cpu.registerInstruction(Opcode.SUB, new Sub());
        cpu.registerInstruction(Opcode.MUL, new Mul());
        cpu.registerInstruction(Opcode.DIV,new Div());
        //Logical
        cpu.registerInstruction(Opcode.AND, new And());
        cpu.registerInstruction(Opcode.OR, new Or());
        cpu.registerInstruction(Opcode.NOT, new Not());
        cpu.registerInstruction(Opcode.SHL, new Shl());
        cpu.registerInstruction(Opcode.SHR, new Shr());
        // Flow Control
        cpu.registerInstruction(Opcode.JUMP, new Jump());
        cpu.registerInstruction(Opcode.JZ, new Jz());
        cpu.registerInstruction(Opcode.IFGT, new Ifgt());
        cpu.registerInstruction(Opcode.IFLT, new Iflt());
        // System
        cpu.registerInstruction(Opcode.HALT, new Halt());
    }

}
