package it.uniupo.msvm.core.instructions;


/**
 * Interfaccia che definisice le operazioni sicure che un'istruzione puo richedere alla CPU
 * Implementa il pattern "Ïnterface Segregation" per nascondere i dettagli interni della CPU (come il ciclo run)
 * */
public interface ExecutionContext {
    //Stack Access
    void push(int value);
    int pop();
    int peek();

    //Memory Access
    int readMemory(int address);
    void writeMemory(int address, int value);

    //Flow Control
    int getIp();
    void setIp(int address);
    void halt();

    //Fetching argomenti
    int fetchNextByte();
}
