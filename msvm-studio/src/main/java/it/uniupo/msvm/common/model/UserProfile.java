package it.uniupo.msvm.common.model;

import java.io.Serializable;

/**
 * Modello che rappresenta i dettagli del profilo di un utente.
 * <p>
 * Mentre la classe {@link User} gestisce le credenziali e l'accesso,
 * questa classe contiene le informazioni anagrafiche e descrittive.
 * Implementa {@link Serializable} per il trasferimento via RMI.
 * </p>
 *
 * @author Luca Lupi
 * @version 1.0
 */
public class UserProfile implements Serializable {

    /**
     * ID univoco per la serializzazione RMI.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Riferimento all'ID dell'utente proprietario di questo profilo.
     * <p>
     * Questo campo funge da chiave esterna (Foreign Key) logica per collegare
     * il profilo al corrispondente oggetto {@link User}.
     * </p>
     */

    private Long userId;

    /**
     * Nome di battesimo dell'utente.
     */

    private String firstName;

    /**
     * Cognome dell'utente.
     */

    private String lastName;

    /**
     * Una breve descrizione o biografia dell'utente.
     */

    private String bio;

    /**
     * Numero di telefono di contatto (opzionale).
     */

    private String phoneNumber;

    /**
     * Costruttore vuoto.
     * Necessario per la serializzazione e per i framework.
     */
    public UserProfile() {
    }

    /**
     * Costruttore completo per inizializzare un profilo.
     *
     * @param userId      L'ID dell'utente associato (User.id).
     * @param firstName   Il nome.
     * @param lastName    Il cognome.
     * @param bio         La biografia.
     * @param phoneNumber Il numero di telefono.
     */
    public UserProfile(Long userId, String firstName, String lastName, String bio, String phoneNumber) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Imposta il nome di battesimo dell'utente.
     *
     * @param firstName il nome da impostare.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Restituisce il nome di battesimo dell'utente.
     *
     * @return il nome di battesimo.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Imposta l'ID dell'utente associato.
     *
     * @param userId l'ID utente da impostare.
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Restituisce l'ID dell'utente associato.
     *
     * @return l'ID utente.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Imposta il cognome dell'utente.
     *
     * @param lastName il cognome da impostare.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Restituisce il cognome dell'utente.
     *
     * @return il cognome dell'utente.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Imposta la biografia dell'utente.
     *
     * @param bio la biografia da impostare.
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * Restituisce la biografia dell'utente.
     *
     * @return la biografia.
     */
    public String getBio() {
        return bio;
    }

    /**
     * Imposta il numero di telefono dell'utente.
     *
     * @param phoneNumber il numero di telefono da impostare.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Restituisce il numero di telefono dell'utente.
     *
     * @return il numero di telefono.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Restituisce una rappresentazione testuale del profilo.
     *
     * @return Stringa con i dettagli principali del profilo.
     */
    @Override
    public String toString() {
        return "UserProfile [userId=" + userId + ", name=" + firstName + " " + lastName + "]";
    }
}