package it.uniupo.msvm.common.model;

import lombok.Getter;
import lombok.Setter;
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
    @Getter
    @Setter
    private Long userId;

    /**
     * Nome di battesimo dell'utente.
     */
    @Getter
    @Setter
    private String firstName;

    /**
     * Cognome dell'utente.
     */
    @Getter
    @Setter
    private String lastName;

    /**
     * Una breve descrizione o biografia dell'utente.
     */
    @Getter
    @Setter
    private String bio;

    /**
     * Numero di telefono di contatto (opzionale).
     */
    @Getter
    @Setter
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
     * Restituisce una rappresentazione testuale del profilo.
     *
     * @return Stringa con i dettagli principali del profilo.
     */
    @Override
    public String toString() {
        return "UserProfile [userId=" + userId + ", name=" + firstName + " " + lastName + "]";
    }
}s