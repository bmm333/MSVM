package it.uniupo.msvm.core.exceptions;

public class StackException extends VmException{
    public StackException(String message) {
        super(message);
    }
    //Factory Methods
    public static StackException underflow(){
        return new StackException("Stack underflow: Tentativo di POP su stack vuoto");
    }
    public static StackException overflow(int maxSize){
        return new StackException("Stack Overflow: Superata dimensione massima (" + maxSize + ")");    }
}
