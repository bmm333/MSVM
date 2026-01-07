package it.uniupo.msvm.core;

import it.uniupo.msvm.core.assembler.Assembler;
import it.uniupo.msvm.core.cpu.Cpu;
import it.uniupo.msvm.core.memory.Memory;
import it.uniupo.msvm.core.runtime.VmListener;
import it.uniupo.msvm.core.runtime.VmRunner;
import it.uniupo.msvm.core.runtime.VmStateSnapshot;

/**
 * MsvmService - Facde Principale
 * <p>
 *     Questa classe rappresenta l'API publica del Core MSVM
 *     Isola il Controller (UI) dai dettagli di implementazione. (Runner, CPU, Assembly)
 *     Implementa le regole di business per il caricamento e l'esecuzione.
 * </p>
 * */
public class MsvmService {
    private final Memory memory;
    private final Cpu cpu;
    private final VmRunner runner;
    private final Assembler assembler;

    //Dependency Injection Constructor
    public MsvmService(Memory memory, Cpu cpu, VmRunner runner, Assembler assembler) {
        this.memory = memory;
        this.cpu = cpu;
        this.runner = runner;
        this.assembler = assembler;
    }
    /**
     * Compila il sorgente, resetta la VM e carica il bytecode
     * @throws RuntimeException se l'assemlaggio fallisce o la VM e in exe
     */
    public void loadCode(String sourceCode){
        if(runner.isRunning())
        {
            //Domain signal: meglio lanciare eccezione controllata o ritornare false
            //ma per ora proteggiamo lo stato.
            throw new IllegalStateException("Vm is running. Stop before loading");
        }
        //reset hardware
        cpu.reset();
        memory.clear();
        //Assemblaggio
        int[] bytecode=assembler.assemble(sourceCode);
        //loading
        memory.loadProgram(bytecode);
    }
    public void run(){
        if(!runner.isRunning())
        {
            runner.start();
        }
    }
    public void pause() {
        runner.pause();
    }

    public void step() {
        runner.step();
    }

    public void stop() {
        runner.stop();
    }

    public void reset() {
        runner.stop();
        cpu.reset();
        // Nota: non puliamo la memoria al reset, solo CPU e Stack (behaviour standard debugger)
    }
    public void setFrequency(int hz) {
        runner.setFrequency(hz);
    }
    //proxy per pattern observer
    public void addListener(VmListener listener) {
        runner.addListener(listener);
    }
    public void removeListener(VmListener listener) {
        runner.removeListener(listener);
    }
    public VmStateSnapshot getSnapshot() {
        return runner.getSnapshot();
    }
}
