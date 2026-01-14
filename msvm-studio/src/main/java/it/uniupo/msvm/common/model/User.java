package it.uniupo.msvm.common.model;




import java.io.Serializable;

/**
 * Modello che rappresenta un Utente del sistema.
 * <p>
 * Questa classe è condivisa tra client e server (modulo <code>common</code>)
 * e viene utilizzata per l'autenticazione e l'identificazione.
 * Implementa {@link Serializable} per poter essere trasferita tramite RMI.
 * </p>
 *
 * @author Luca Lupi
 * @version 1.0
 */
public class User implements Serializable {

    /**
     * ID univoco per la serializzazione.
     * Garantisce la compatibilità tra diverse versioni della classe durante il trasferimento RMI.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identificativo univoco dell'utente (es. Primary Key del database).
     */

    private Long id;

    /**
     * Nome utente univoco utilizzato per il login.
     */
    private String username;

    /**
     * Password dell'utente.
     * <p>
     * <b>Nota di sicurezza:</b> In un ambiente di produzione, questo campo dovrebbe
     * contenere l'hash della password, non la password in chiaro.
     * </p>
     */
    private String password;

    /**
     * Indirizzo email di contatto dell'utente.
     */
    private String email;

    /**
     * Ruolo dell'utente
     */
    private String role;

    /**
     * Costruttore vuoto.
     * <p>
     * Necessario per la serializzazione RMI e per la creazione di istanze
     * da parte di framework di persistenza o librerie di mapping.
     * </p>
     */
    public User() {
    }

    /**
     * Costruttore completo per inizializzare un nuovo utente.
     *
     * @param id       L'identificativo univoco dell'utente.
     * @param username Il nome utente per l'accesso.
     * @param password La password (o il suo hash).
     * @param email    L'indirizzo email dell'utente.
     */
    public User(Long id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    /**
     * Imposta l'identificativo dell'utente.
     *
     * @param id l'identificativo da impostare.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Restituisce l'identificativo dell'utente.
     *
     * @return l'identificativo dell'utente.
     */
    public Long getId() {
        return id;
    }

    /**
     * Imposta il nome utente.
     *
     * @param username il nome utente da impostare.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Restituisce il nome utente.
     *
     * @return il nome utente.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Imposta la password (o il suo hash).
     *
     * @param password la password da impostare.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Restituisce la password (o il suo hash).
     *
     * @return la password dell'utente.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Imposta l'indirizzo email.
     *
     * @param email l'indirizzo email da impostare.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Restituisce l'indirizzo email.
     *
     * @return l'indirizzo email dell'utente.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Imposta il ruolo dell'utente.
     *
     * @param role il ruolo da impostare.
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Restituisce il ruolo dell'utente.
     *
     * @return il ruolo dell'utente.
     */
    public String getRole() {
        return role;
    }
    /**
     * Restituisce una rappresentazione in stringa dell'oggetto User.
     * <p>
     * <b>Nota:</b> Per motivi di sicurezza, la password non viene inclusa
     * nella stringa restituita.
     * </p>
     *
     * @return Una stringa contenente username ed email dell'utente.
     */
    @Override
    public String toString() {
        return "User [username=" + username + ", email=" + email + "]";
    }
}