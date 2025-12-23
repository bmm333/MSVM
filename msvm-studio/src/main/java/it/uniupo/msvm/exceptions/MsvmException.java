package it.uniupo.msvm.exceptions;


/**
* Radice della  gerarchia di eccezioni per lintero progetto MSVM
* <p>
*  Questa classe estende {@link RuntimeException} (Unchecked) per permettere la propagazione
* degli errori senza sporcare le firme dei metodi con clausole {@code throws}
* Tutte le eccezioni specifiche (Core, Compiler,UI) devono estendere questa classe
* o una delle sue sottoclassi
* </p>
* @author Arben Mema
* */
public class MsvmException extends RuntimeException{
    /**
     * Costruisce una nuova eccezione con un messaggio di dettaglio.
     *
     * @param message Il messaggio che descrive l'errore.
     */
    public MsvmException(String message) {
        super(message);
    }
    /**
     * Costruisce una nuova eccezione con un messaggio e la causa scatenante (Exception Chaining).
     * Utile quando si wrappa un'eccezione Java nativa (es. IOException).
     *
     * @param message Il messaggio che descrive l'errore.
     * @param cause   L'eccezione originale che ha causato questo errore.
     */
    public MsvmException(String message, Throwable cause) {
        super(message, cause);
    }
}
