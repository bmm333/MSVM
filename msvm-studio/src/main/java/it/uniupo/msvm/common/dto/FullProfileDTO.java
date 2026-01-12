package it.uniupo.msvm.common.dto;

import java.io.Serializable;

/**
 * DTO aggregato che rappresenta il profilo completo di un utente.
 * <p>
 * Questa classe unisce le informazioni provenienti da due entità distinte:
 * <ul>
 * <li><b>User:</b> (userId, username, email) - Dati di account.</li>
 * <li><b>UserProfile:</b> (firstName, lastName, bio, phoneNumber) - Dati anagrafici.</li>
 * </ul>
 * È estremamente utile per le interfacce grafiche (UI) che devono mostrare o modificare
 * tutte le informazioni dell'utente in un'unica schermata, riducendo il numero di chiamate RMI necessarie.
 * </p>
 */
public class FullProfileDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // --- Dati Base (da User) ---

    /** ID dell'utente (Account). */
    private Long userId;

    /** Username per l'accesso. */
    private String username;

    /** Email di contatto. */
    private String email;

    // --- Dati Dettaglio (da UserProfile) ---

    /** Nome di battesimo. */
    private String firstName;

    /** Cognome. */
    private String lastName;

    /** Breve biografia o descrizione. */
    private String bio;

    /** Numero di telefono. */
    private String phoneNumber;

    /**
     * Costruttore vuoto per serializzazione.
     */
    public FullProfileDTO() {
    }

    /**
     * Costruttore completo per inizializzare tutti i campi del profilo.
     *
     * @param userId      ID Utente.
     * @param username    Nome utente.
     * @param email       Email.
     * @param firstName   Nome.
     * @param lastName    Cognome.
     * @param bio         Biografia.
     * @param phoneNumber Numero di telefono.
     */
    public FullProfileDTO(Long userId, String username, String email, String firstName, String lastName, String bio, String phoneNumber) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.phoneNumber = phoneNumber;
    }

    // --- Getters e Setters ---

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Restituisce una stringa descrittiva del profilo completo.
     */
    @Override
    public String toString() {
        return "FullProfileDTO [userId=" + userId + ", username=" + username + ", fullName=" + firstName + " " + lastName + "]";
    }
}