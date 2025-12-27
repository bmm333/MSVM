package it.uniupo.msvm.core.runtime;

public interface Clock {
    void start();
    void pause();
    void step(); //permette di fare un solo passo alla volta
    void stop();
    boolean isRunning();
    void setFrequency(int hz); // hz=0 -> Max Speed (bounded by machine), hz=1 -> 1 instruction per second
}
