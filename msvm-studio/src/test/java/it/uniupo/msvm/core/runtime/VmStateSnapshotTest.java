package it.uniupo.msvm.core.runtime;

import it.uniupo.msvm.core.cpu.Cpu;
import it.uniupo.msvm.core.memory.Memory;
import it.uniupo.msvm.core.memory.OperandStack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VmStateSnapshotTest {
    @Test
    void testSnapshotImmutability()
    {
        //setup
        Memory mem=new Memory(256);
        OperandStack stack=new OperandStack(128);
        Cpu cpu=new Cpu(mem,stack);
        VmRunner runner=new VmRunner(cpu);
        //Modifico lo stato Reale iniziale
        //cpu implementa ExecutionContext
        cpu.push(10); //Stack: [10]
        mem.write(0,99); //Mem[0]=99
        //Snapshot
        VmStateSnapshot snapshot=runner.getSnapshot();
        //cpu va avanti e modifica i dati veri
        cpu.pop(); //stack vuoto
        mem.write(0,0); //Mem[0]=0
        //Verifico che snapshot sia quella vecchia
        //se lo snapshot forse un riferimento diretto qui vedremmo 0 e stack vuoto
        //controllo mem nello snapshot
        assertEquals(99,snapshot.memory()[0],"La memoria nello snapshot deve restare 99");
        // Controllo del Stack nello Snapshot
        assertEquals(1, snapshot.stack().size(), "Lo stack nello snapshot deve avere ancora 1 elemento");
        assertEquals(10, snapshot.stack().get(0), "Il valore nello stack snapshot deve essere 10");
    }

}
