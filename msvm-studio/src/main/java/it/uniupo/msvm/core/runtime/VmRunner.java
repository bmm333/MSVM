package it.uniupo.msvm.core.runtime;
import it.uniupo.msvm.core.cpu.*;


public class VmRunner implements Clock,Runnable{
    private final Cpu cpu;
    private Thread workerThread;
    //uttilizzo volatile per garantire che i thread vedono subito i cambiamenti
    private volatile  boolean running=false; //il thread e vivo?
    private volatile boolean pasued=true; //il thread e in pausa?
    private volatile int delayMs=0; //ritardo tra le istruzioni.
    public VmRunner(Cpu cpu)
    {
        this.cpu=cpu;
    }
    @Override 
    public void start()
    {
        //impl tutti i altri metodi ereditati da clock
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }
    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }
    @Override
    public void step() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'step'");
    }
    @Override
    public void stop() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stop'");
    }
    @Override
    public boolean isRunning() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isRunning'");
    }
    @Override
    public void setFrequency(int hz) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setFrequency'");
    }
    public VmStateSnapshot getSnapshot(){
        synchronized(this)
        {
            return new VmStateSnapshot(cpu.getIp(), cpu.isHalted(), cpu.getStackCopy(), cpu.getMemoryCopy(), cpu.getLastWrittenAddress());
        }
    }
}
