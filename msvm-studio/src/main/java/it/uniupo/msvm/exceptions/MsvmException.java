package it.uniupo.msvm.exceptions;

/*
* Eccezione base per tutto il progetto MSVM
* Estende RunTimeException (Unchecked) per non sporacre il codice con throws
* */
public class MsvmException extends RuntimeException{
    public MsvmException(String message) {
        super(message);
    }
    public MsvmException(String message, Throwable cause) {
        super(message, cause);
    }
}
