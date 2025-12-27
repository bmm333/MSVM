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
    void testPauseAndResume()throws InterruptedException{
        memory.write(0,Opcode.PUSH.getCode());
        //non avanzera mai oltrea a 0 se non scriviamo codice che incrementa ip
        //ma push in cpu.java reale incrementa ip se legge argomenti
        //qui il lambda dummy non fa nulla, quindi ip avanza solo per l'opcode
        //simuliamo un programma lungo
        for(int i=0;i<100;i++) memory.write(i,Opcode.PUSH.getCode());
        //start
        runner.start();
        Thread.sleep(200);
        assertTrue(runner.isRunning(),"Il runner dovrebbe essersi avviato dopo start()");
        //pause
        runner.pause();
        Thread.sleep(20);//dando tempo al thread di fermarsi sul wait()
        //snapshot per vedere dove siamo
        int ipAtPause=cpu.getIp();
        //attendo ancora, ip non deve cambiare se sono in pause
        Thread.sleep(50);
        assertEquals(ipAtPause,cpu.getIp(),"L'ip non dovrebbe cambiare dopo pause()");
        //Resume
        runner.start();
        Thread.sleep(20);
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
