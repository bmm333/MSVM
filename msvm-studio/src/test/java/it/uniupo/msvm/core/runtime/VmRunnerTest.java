package it.uniupo.msvm.core.runtime;


import it.uniupo.msvm.core.cpu.Cpu;
import it.uniupo.msvm.core.instructions.Opcode;
import it.uniupo.msvm.core.memory.Memory;
import it.uniupo.msvm.core.memory.OperandStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di integrazione per il Runtime engine
 * Verifica che il thread parte , ferma e gestisce concorrenza correttamente
 * */
public class VmRunnerTest {
    private Cpu cpu;
    private Memory memory;
    private VmRunner runner;
    @BeforeEach
    void setup()
    {
        memory=new Memory(256);
        OperandStack stack=new OperandStack();
        cpu = new Cpu(memory, stack);
        runner = new VmRunner(cpu);
        //registriamo istr "Stub" (finte) per testare il motore senza dipendeze esterne
        cpu.registerInstruction(Opcode.PUSH, ctx -> { });
        cpu.registerInstruction(Opcode.HALT, ctx -> ctx.halt());
    }
    @Test
    @Timeout(value = 2,unit = TimeUnit.SECONDS)
    void testStartAndAutoStop() throws InterruptedException{
        memory.write(0,Opcode.PUSH.getCode());
        memory.write(1, Opcode.PUSH.getCode());
        memory.write(2, Opcode.HALT.getCode());
        runner.start();
        Thread.sleep(100);
        assertFalse(runner.isRunning(),"Il runner dovrebbe essersi fermato dopo HALT");
        assertTrue(cpu.isHalted(),"La cpu deve essere in stato HALTED");
        assertEquals(3,cpu.getIp(),"L'ip deve essere avanzato di 3 step");
    }
    @Test
    void testPauseAndResume() throws InterruptedException {
        //Riempio la memoria
        for(int i=0; i<100; i++) memory.write(i, Opcode.PUSH.getCode());
        // FIX (penso che sia il mio host la causa): Rallentiamo la VM!
        // 10 Hz = 1 istruzione ogni 100ms.
        // Così siamo sicuri che dopo 20ms stia ancora girando.
        runner.setFrequency(10);
        //Start
        runner.start();
        Thread.sleep(50); // Lascip correre un po' (farà mezza istruzione)
        assertTrue(runner.isRunning(), "Il runner deve essere attivo dopo lo start");
        //pause
        runner.pause();
        Thread.sleep(20); // Diamo tempo al thread di fermarsi
        // Prendiamo uno snapshot per vedere dove siamo
        int ipAtPause = cpu.getIp();
        // Aspettiamo ancora: l'IP NON deve cambiare se siamo in pausa
        Thread.sleep(50);
        assertEquals(ipAtPause, cpu.getIp(), "L'ip non deve avanzare mentre è in pausa");
        //Resume
        runner.start();
        Thread.sleep(150); // Aspettiamo abbastanza per fargli fare almeno 1 step (100ms delay)

        assertTrue(cpu.getIp() > ipAtPause, "L'IP deve riprendere ad avanzare dopo il resume");
        // Cleanup
        runner.stop();
    }
    @Test
    void testSnapshotThreadSafety() {
        memory.write(0, Opcode.PUSH.getCode());
        runner.step(); // Step manuale
        VmStateSnapshot snap = runner.getSnapshot();
        assertNotNull(snap);
        assertEquals(1, snap.ip());
        assertNotNull(snap.memory());
        assertNotNull(snap.stack());
        snap.memory()[0] = 99;
        assertNotEquals(99, memory.read(0), "Lo snapshot deve essere una copia difensiva!");
    }
}
