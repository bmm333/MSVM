package  it.uniupo.msvm.core.runtime;

import it.uniupo.msvm.core.cpu.Cpu;
import it.uniupo.msvm.core.exceptions.VmException;
import java.util.ArrayList;
import java.util.List;

public class VmRunner implements Clock,Runnable {
    private final Cpu cpu;
    private Thread workerThread;
    //Flags volatili per garantire la visbilita tra Thread diversi (UI vs Runner)
    private volatile boolean running = false;
    private volatile boolean paused = true;
    private volatile int delayMs = 0;
    //Lista degli osservatori (UI)
    private final List<VmListener> listeners = new ArrayList<>();
    //oggetto lock dedicato per gestire la pausa senza bloccare lintera istanza
    private final Object pauseLock = new Object();

    public VmRunner(Cpu cpu)
    {
        this.cpu = cpu;
    }
    // Metodo helper privato per la ripresa
    private void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll(); // Sveglia il thread che dorme nel wait()
        }
    }
    //gestione listner
    public void addListener(VmListener listener)
    {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }
    public void removeListener(VmListener listener)
    {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
    //Helper per notificare tutti
    private void fireUpdate()
    {
        //prendere lo snapshot in modo  thread safe (usando getSnapshot)
        VmStateSnapshot snap=getSnapshot();
        synchronized (listeners)
        {
            for(VmListener l:listeners)
            {
                l.onVmUpdate(snap);
            }
        }
    }
    private void fireError(String msg)
    {
        synchronized (listeners)
        {
            for(VmListener l:listeners)
            {
                l.onVmError(msg);
            }
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
    public void run () {
        while (running) {
            //1. Gestione Pausa (pattern wait/notify)
            synchronized (pauseLock) {
                while (paused && running) {
                    try {
                        pauseLock.wait(); //rilascia la cpu e va in sleep
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            //2. Check stato CPU
            if (cpu.isHalted()) {
                stop();
                fireUpdate();
                break;
            }
            //3. Esecuzione step atomico
            try {
                //sync su this per vitare che un snapshot venga fatto a meta scrittura
                synchronized (this) {
                    cpu.step();
                }
                fireUpdate();
            } catch (VmException e) {
                System.err.println("CPU Error: " + e.getMessage());
                //in futuro notifichero ui per il crash
                //E arrivato quel giorno :D
                fireError(e.getMessage());
                stop();
                break;
            }
            //4 Throttling(frequenza)
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
        // Step manuale: consentito solo se siamo in pausa
        if (!paused) {
            throw new IllegalStateException("Cannot manual step while running");
        }
        try {
            synchronized (this) {
                if (!cpu.isHalted()) {
                    cpu.step();
                }
            }
            fireUpdate();
        } catch (VmException e) {
            System.err.println("Manual Step Error: " + e.getMessage());
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

    /**
     * Genera un'istantanea thread-safe dello stato attuale.
     */
    public synchronized VmStateSnapshot getSnapshot() {
        return new VmStateSnapshot(
                cpu.getIp(),
                cpu.isHalted(),
                cpu.getStackCopy(),
                cpu.getMemoryCopy(),
                cpu.getLastWrittenAddress()
        );
    }
}

















