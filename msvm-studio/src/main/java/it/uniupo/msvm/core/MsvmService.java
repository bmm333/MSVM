package it.uniupo.msvm.core;

import it.uniupo.msvm.core.assembler.Assembler;
import it.uniupo.msvm.core.cpu.Cpu;
import it.uniupo.msvm.core.memory.Memory;
import it.uniupo.msvm.core.runtime.VmListener;
import it.uniupo.msvm.core.runtime.VmRunner;
import it.uniupo.msvm.core.runtime.VmStateSnapshot;

/**
 * MsvmService - Facade Principale
 * <p>
 *     Questa classe rappresenta l'API pubblica del Core MSVM.
 *     Isola il Controller (UI) dai dettagli di implementazione (Runner, CPU, Assembly).
 *     Implementa le regole di business per il caricamento e l'esecuzione.
 * </p>
 */
public class MsvmService {
    /** Memoria della macchina virtuale. */
    private final Memory memory;
    /** CPU della macchina virtuale. */
    private final Cpu cpu;
    /** Esecutore della macchina virtuale. */
    private final VmRunner runner;
    /** Assemblatore per convertire il codice sorgente in bytecode. */
    private final Assembler assembler;

    /**
     * Costruttore con Dependency Injection.
     *
     * @param memory    la memoria da utilizzare.
     * @param cpu       la CPU da utilizzare.
     * @param runner    l'esecutore da utilizzare.
     * @param assembler l'assemblatore da utilizzare.
     */
    public MsvmService(Memory memory, Cpu cpu, VmRunner runner, Assembler assembler) {
        this.memory = memory;
        this.cpu = cpu;
        this.runner = runner;
        this.assembler = assembler;
    }

    /**
     * Compila il sorgente, resetta la VM e carica il bytecode.
     *
     * @param sourceCode il codice sorgente assembly.
     * @throws IllegalStateException se la VM è in esecuzione.
     * @throws RuntimeException      se l'assemblaggio fallisce.
     */
    public void loadCode(String sourceCode) {
        if (runner.isRunning()) {
            throw new IllegalStateException("La VM è in esecuzione. Fermarla prima di caricare il codice.");
        }
        // reset hardware
        cpu.reset();
        memory.clear();
        // Assemblaggio
        int[] bytecode = assembler.assemble(sourceCode);
        // caricamento
        memory.loadProgram(bytecode);
    }

    /**
     * Avvia l'esecuzione della VM se non è già in esecuzione.
     */
    public void run() {
        if (!runner.isRunning()) {
            runner.start();
        }
    }

    /**
     * Mette in pausa l'esecuzione della VM.
     */
    public void pause() {
        runner.pause();
    }

    /**
     * Esegue una singola istruzione (step).
     */
    public void step() {
        runner.step();
    }

    /**
     * Ferma l'esecuzione della VM.
     */
    public void stop() {
        runner.stop();
    }

    /**
     * Ferma la VM e resetta la CPU.
     */
    public void reset() {
        runner.stop();
        cpu.reset();
        // Nota: non puliamo la memoria al reset, solo CPU e Stack (comportamento standard debugger)
    }

    /**
     * Imposta la frequenza di clock della VM.
     *
     * @param hz la frequenza in Hertz.
     */
    public void setFrequency(int hz) {
        runner.setFrequency(hz);
    }

    /**
     * Aggiunge un listener per monitorare lo stato della VM.
     *
     * @param listener il listener da aggiungere.
     */
    public void addListener(VmListener listener) {
        runner.addListener(listener);
    }

    /**
     * Rimuove un listener precedentemente aggiunto.
     *
     * @param listener il listener da rimuovere.
     */
    public void removeListener(VmListener listener) {
        runner.removeListener(listener);
    }

    /**
     * Restituisce uno snapshot dello stato attuale della VM.
     *
     * @return lo snapshot dello stato.
     */
    public VmStateSnapshot getSnapshot() {
        return runner.getSnapshot();
    }
}
