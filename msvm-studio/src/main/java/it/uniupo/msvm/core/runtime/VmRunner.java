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
 * Refactoring 2.0: Improved Concurrency (Private Lock) & Error handling (No leaking exceptions).
 *
 * @author Arben Mema
 */
public class VmRunner implements Clock,Runnable {
    private final VmBackend backend;
    private Thread workerThread;
    //Flags volatili per garantire la visbilita tra Thread diversi (UI vs Runner)
    private volatile boolean running = false;
    private volatile boolean paused = true;
    private volatile int delayMs = 0;
    //Lista degli osservatori (UI)
    private final List<VmListener> listeners = new ArrayList<>();
    //oggetto lock dedicato per gestire la pausa senza bloccare lintera istanza
    //Refactoring : Encapsulated Concurrency
    private final Object pauseLock = new Object();
    private final Object backendLock = new Object();
    private final Object listenerLock = new Object();

    public VmRunner(VmBackend backend)
    {
        this.backend = backend;
    }
    // Metodo helper privato per la ripresa
    private void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll(); // Sveglia il thread che dorme nel wait()
        }
    }
    //gestione listner
    public void addListener(VmListener listener) {
        synchronized (listenerLock) {
            listeners.add(listener);
        }
    }

    public void removeListener(VmListener listener) {
        synchronized (listenerLock) {
            listeners.remove(listener);
        }
    }
    //Helper per notificare tutti
    private void fireUpdate() {
        VmStateSnapshot snap = getSnapshot(); // Thread-safe call

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

    private void fireError(String msg) {
        List<VmListener> listenersCopy;
        synchronized (listenerLock) {
            listenersCopy = new ArrayList<>(listeners);
        }
        for (VmListener l : listenersCopy) {
            l.onVmError(msg);
        }
    }
    @Override
    public void start ()
    {
        if (workerThread == null || !workerThread.isAlive()) {
            //primo avvio: creo il thread
            workerThread = new Thread(this, "VmRunner-Thread");
            running = true;
            paused = false;
            workerThread.start();
        } else {
            //il thread esiste ma e in pausa: lo sveglio
            resume();
        }
    }
    @Override
    public void run() {
        while (running) {
            //Gestione Pausa
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

            //Controllo Halt
            // Accesso safe allo stato della CPU
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
        if (running&&!paused) {
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
    private void notifyHalt() {
        for (VmListener listener : listeners) {
            listener.onVmHalt();
        }
    }
}

















