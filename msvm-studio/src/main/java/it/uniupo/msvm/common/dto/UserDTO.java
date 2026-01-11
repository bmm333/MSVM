package it.uniupo.msvm.common.dto;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) sicuro che rappresenta un Utente.
 * <p>
 * Questa classe viene utilizzata per inviare i dati dell'utente dal Server al Client
 * (ad esempio dopo un login avvenuto con successo).
 * <br>
 * A differenza della classe Entity <code>User</code>, questo DTO <b>non contiene la password</b>,
 * garantendo che dati sensibili non viaggino inutilmente verso il client o l'interfaccia grafica.
 * </p>
 */
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Identificativo univoco dell'utente. */
    private Long id;

    /** Nome utente pubblico. */
    private String username;

    /** Email di contatto. */
    private String email;

    /**
     * Costruttore vuoto necessario per la serializzazione.
     */
    public UserDTO() {
    }

    /**
     * Costruttore completo.
     *
     * @param id       L'ID univoco.
     * @param username Il nome utente.
     * @param email    L'indirizzo email.
     */
    public UserDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserDTO [id=" + id + ", username=" + username + ", email=" + email + "]";
    }
}