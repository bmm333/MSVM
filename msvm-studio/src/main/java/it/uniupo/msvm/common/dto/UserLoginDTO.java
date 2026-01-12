package it.uniupo.msvm.common.dto;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) utilizzato per l'autenticazione.
 * <p>
 * Questa classe è un contenitore semplice per trasportare le credenziali (username e password)
 * dal Client al Server durante le fasi di Login o Registrazione.
 * Implementa {@link Serializable} per essere trasmesso via rete tramite RMI.
 * </p>
 */
public class UserLoginDTO implements Serializable {

    /**
     * ID univoco per la serializzazione, necessario per la compatibilità RMI.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Lo username inserito dall'utente.
     */
    private String username;

    /**
     * La password inserita dall'utente.
     * <p>
     * <b>Nota:</b> A seconda dell'implementazione del client, questa potrebbe essere
     * inviata in chiaro (se su SSL) o già hashata.
     * </p>
     */
    private String password;

    /**
     * Costruttore vuoto.
     * Necessario per la serializzazione e per la creazione dell'oggetto via reflection.
     */
    public UserLoginDTO(){
    }

    /**
     * Costruttore completo.
     *
     * @param username Il nome utente.
     * @param password La password.
     */
    public UserLoginDTO(String username, String password){
        this.username = username;
        this.password = password;
    }

    /**
     * Restituisce lo username.
     * @return Lo username.
     */
    public String getUsername(){
        return username;
    }

    /**
     * Imposta lo username.
     * @param username Il nuovo username.
     */
    public void setUsername(String username){
        this.username = username;
    }

    /**
     * Restituisce la password.
     * @return La password.
     */
    public String getPassword(){
        return password;
    }

    /**
     * Imposta la password.
     * @param password La nuova password.
     */
    public void setPassword(String password){
        this.password = password;
    }

    /**
     * Rappresentazione in stringa dell'oggetto.
     * <p>
     * <b>Sicurezza:</b> Questo metodo è stato sovrascritto per evitare di stampare
     * la password nei log di sistema.
     * </p>
     */
    @Override
    public String toString() {
        return "UserLoginDTO [username=" + username + "]";
    }
}