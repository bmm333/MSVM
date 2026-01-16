package  it.uniupo.msvm.core.runtime;

import it.uniupo.msvm.core.exceptions.VmException;
import java.util.ArrayList;
import java.util.List;
/**
 * Motore di esecuzione della VM.
 * <p>
 * Gestisce il ciclo di vita del thread di esecuzione, la sincronizzazione
 * e la notifica degli eventi.
 * </p>
 * Rifacimento 2.0: Concorrenza migliorata (Lock privati) e gestione degli errori.
 *
 * @author Arben Mema
 */
public class VmRunner implements Clock, Runnable {
    /** Backend della VM (CPU/Memoria). */
    private final VmBackend backend;
    /** Thread di lavoro dedicato all'esecuzione. */
    private Thread workerThread;
    /** Flag che indica se il runner è attivo. */
    private volatile boolean running = false;
    /** Flag che indica se l'esecuzione è in pausa. */
    private volatile boolean paused = true;
    /** Ritardo tra un ciclo e l'altro in millisecondi (per simulare la frequenza). */
    private volatile int delayMs = 0;
    /** Lista degli osservatori (UI) per gli aggiornamenti di stato. */
    private final List<VmListener> listeners = new ArrayList<>();
    /** Oggetto lock dedicato per gestire la pausa. */
    private final Object pauseLock = new Object();
    /** Oggetto lock dedicato per l'accesso al backend. */
    private final Object backendLock = new Object();
    /** Oggetto lock dedicato per l'accesso ai listener. */
    private final Object listenerLock = new Object();

    /**
     * Costruttore del runner.
     *
     * @param backend il backend della VM da eseguire.
     */
    public VmRunner(VmBackend backend) {
        this.backend = backend;
    }

    /**
     * Riprende l'esecuzione dopo una pausa.
     */
    private void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll(); // Sveglia il thread che dorme nel wait()
        }
    }

    /**
     * Aggiunge un listener per gli eventi della VM.
     *
     * @param listener il listener da aggiungere.
     */
    public void addListener(VmListener listener) {
        synchronized (listenerLock) {
            listeners.add(listener);
        }
    }

    /**
     * Rimuove un listener precedentemente aggiunto.
     *
     * @param listener il listener da rimuovere.
     */
    public void removeListener(VmListener listener) {
        synchronized (listenerLock) {
            listeners.remove(listener);
        }
    }

    /**
     * Notifica tutti i listener registrati di un aggiornamento dello stato.
     */
    private void fireUpdate() {
        VmStateSnapshot snap = getSnapshot(); // Chiamata Thread-safe

        // Copia difensiva della lista listener per iterare fuori dal lock
        // Evita deadlock se un listener richiama metodi del runner
        List<VmListener> listenersCopy;
        synchronized (listenerLock) {
            listenersCopy = new ArrayList<>(listeners);
        }

        for (VmListener l : listenersCopy) {
            l.onVmUpdate(snap);
        }
    }

    /**
     * Notifica tutti i listener di un errore verificatosi durante l'esecuzione.
     *
     * @param msg messaggio di errore.
     */
    private void fireError(String msg) {
        List<VmListener> listenersCopy;
        synchronized (listenerLock) {
            listenersCopy = new ArrayList<>(listeners);
        }
        for (VmListener l : listenersCopy) {
            l.onVmError(msg);
        }
    }

    /**
     * Avvia il thread di esecuzione o riprende se era in pausa.
     */
    @Override
    public void start() {
        if (workerThread == null || !workerThread.isAlive()) {
            // primo avvio: creo il thread
            workerThread = new Thread(this, "VmRunner-Thread");
            running = true;
            paused = false;
            workerThread.start();
        } else {
            // il thread esiste ma è in pausa: lo sveglio
            resume();
        }
    }

    /**
     * Ciclo principale di esecuzione del thread.
     */
    @Override
    public void run() {
        while (running) {
            // Gestione Pausa
            synchronized (pauseLock) {
                while (paused && running) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            // Controllo Halt
            boolean halted;
            synchronized (backendLock) {
                halted = backend.isHalted();
            }

            if (halted) {
                stop();
                fireUpdate(); // Ultimo update per mostrare lo stato halt
                break;
            }

            //Step Atomico
            try {
                executeAtomicStep();
            } catch (VmException e) {
                // Gestione graceful del crash
                System.err.println("Runtime Crash: " + e.getMessage());
                fireError(e.getMessage());
                stop();
                break;
            }

            //Throttling
            if (delayMs > 0) {
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    @Override
    public void pause ()
    {
        paused = true;
    }
    @Override
    public void stop()
    {
        running = false;
        paused=false;
        //bisognia risvegliare se in pausa , altrimenti non esce mai dal while
        resume();
    }
    @Override
    public void step() {
        // Design Decision: Se la VM sta correndo, ignoriamo lo step manuale.
        // Non lanciamo eccezioni alla UI (Information Hiding).
        if (!paused) {
            fireError("Warning: Cannot manual step while VM is running.");
            return;
        }

        try {
            boolean halted;
            synchronized (backendLock) {
                halted = backend.isHalted();
            }

            if (!halted) {
                executeAtomicStep();
            }
        } catch (VmException e) {
            fireError(e.getMessage());
        }
    }

    @Override
    public boolean isRunning() {
        return running && !paused;
    }

    @Override
    public void setFrequency(int hz) {
        if (hz <= 0) {
            this.delayMs = 0; // Massima velocità
        } else {
            this.delayMs = 1000 / hz; // Es. 10Hz -> 100ms
        }
    }
    private void executeAtomicStep() {
        synchronized (backendLock) {
            backend.step();
        }
        fireUpdate();
    }
    /**
     * Genera un'istantanea thread-safe dello stato attuale.
     */
    public synchronized VmStateSnapshot getSnapshot() {
        synchronized (backendLock) {
            return new VmStateSnapshot(
                    backend.getIp(),
                    backend.isHalted(),
                    backend.getStackSnapshot(),
                    backend.getMemorySnapshot(),
                    backend.getLastWrittenAddress()
            );
        }
    }
}

















