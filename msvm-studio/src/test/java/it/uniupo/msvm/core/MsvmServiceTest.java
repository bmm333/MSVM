package it.uniupo.msvm.core;

import it.uniupo.msvm.core.assembler.Assembler;
import it.uniupo.msvm.core.cpu.Cpu;
import it.uniupo.msvm.core.instructions.Opcode;
import it.uniupo.msvm.core.instructions.impl.Halt;
import it.uniupo.msvm.core.instructions.impl.Push;
import it.uniupo.msvm.core.memory.Memory;
import it.uniupo.msvm.core.memory.OperandStack;
import it.uniupo.msvm.core.runtime.VmListener;
import it.uniupo.msvm.core.runtime.VmRunner;
import it.uniupo.msvm.core.runtime.VmStateSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MsvmServiceTest {

    private MsvmService service;
    private Memory memory;
    private Cpu cpu;
    private VmRunner runner;
    private Assembler assembler;

    @BeforeEach
    void setUp() {
        memory = new Memory(256);
        OperandStack stack = new OperandStack(128);
        cpu = new Cpu(memory, stack);
        cpu.registerInstruction(Opcode.PUSH, new Push());
        cpu.registerInstruction(Opcode.HALT, new Halt());

        runner = new VmRunner(cpu);
        assembler = new Assembler();
        service = new MsvmService(memory, cpu, runner, assembler);
    }

    @Test
    void testLoadCodeAndReset() {
        String code = "PUSH 10\nHALT";

        service.loadCode(code);
        assertEquals(Opcode.PUSH.getCode(), memory.read(0));
        assertEquals(10, memory.read(1));
        assertEquals(Opcode.HALT.getCode(), memory.read(2));

        assertEquals(0, cpu.getIp());
        assertFalse(cpu.isHalted());
    }

    @Test
    void testLoadCodeWhileRunningThrowsException() {
        service.loadCode("HALT");
        service.run();

        assertThrows(IllegalStateException.class, () -> service.loadCode("PUSH 20"));

        service.stop();
    }

    @Test
    void testStepExecution() {
        String code = "PUSH 42\nHALT";
        service.loadCode(code);
        service.step();
        VmStateSnapshot snapshot = service.getSnapshot();
        assertEquals(2, snapshot.ip()); // 1 opcode + 1 arg
        assertEquals(1, snapshot.stack().size());
        assertEquals(42, snapshot.stack().get(0));
        assertFalse(snapshot.isHalted());

        service.step();
        snapshot = service.getSnapshot();

        assertTrue(snapshot.isHalted());
    }

    @Test
    void testReset() {
        String code = "PUSH 99\nHALT";
        service.loadCode(code);
        service.step(); // Execute PUSH

        VmStateSnapshot snapshotBefore = service.getSnapshot();
        assertEquals(2, snapshotBefore.ip());
        assertFalse(snapshotBefore.stack().isEmpty());

        service.reset();

        VmStateSnapshot snapshotAfter = service.getSnapshot();
        assertEquals(0, snapshotAfter.ip());
        assertTrue(snapshotAfter.stack().isEmpty());
        assertFalse(snapshotAfter.isHalted());
        assertEquals(Opcode.PUSH.getCode(), memory.read(0));
    }

    @Test
    @Timeout(5)
    void testRunFlow() throws InterruptedException {
        String code = "PUSH 1\nPUSH 2\nHALT";
        service.loadCode(code);
        CountDownLatch latch = new CountDownLatch(1);
        service.addListener(new VmListener() {
            @Override
            public void onVmUpdate(VmStateSnapshot snapshot) {
                if (snapshot.isHalted()) {
                    latch.countDown();
                }
            }
            @Override
            public void onVmError(String message) {
                fail("Should not error: " + message);
            }
        });

        service.run();
        assertTrue(latch.await(2, TimeUnit.SECONDS), "VM should finish execution");
        VmStateSnapshot finalSnap = service.getSnapshot();
        assertEquals(2, finalSnap.stack().size());
        assertTrue(finalSnap.isHalted());
    }
}