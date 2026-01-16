package it.uniupo.msvm.server.services;

import it.uniupo.msvm.common.dto.UserDTO;
import it.uniupo.msvm.common.model.User;
import it.uniupo.msvm.server.persistence.dao.UserDAO;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Service Layer per l'autenticazione con BCrypt per garantire la sicurezza dei dati.
 */
public class AuthService {
    /** DAO per l'accesso ai dati degli utenti. */
    private final UserDAO userDAO;

    /** Fattore di lavoro per BCrypt (numero di round di hashing). */
    private static final int WORK_FACTOR = 12;

    /**
     * Costruttore che inizializza il DAO degli utenti.
     */
    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Gestisce l'autenticazione di un utente.
     *
     * @param email       l'email dell'utente che tenta l'accesso.
     * @param rawPassword la password in chiaro fornita dall'utente.
     * @return un oggetto {@link UserDTO} con i dati dell'utente se l'autenticazione ha successo.
     * @throws SQLException             in caso di errore nel database.
     * @throws IllegalArgumentException se i dati forniti non sono validi.
     * @throws SecurityException        se le credenziali sono errate.
     */
    public UserDTO authenticate(String email, String rawPassword) throws SQLException {
        // Validazione dati
        if (email == null || rawPassword == null || email.isBlank()) {
            throw new IllegalArgumentException("Email o password non valide");
        }
        // Recupero dell'utente (cerchiamo l'hash nel DB)
        Optional<User> userOpt = userDAO.findByEmail(email);
        // Se l'utente non esiste, lanciamo comunque credenziali non valide
        // per evitare di rivelare se l'email è presente nel sistema (Enumerazione Utenti)
        if (userOpt.isEmpty()) {
            throw new SecurityException("Credenziali non valide");
        }
        User user = userOpt.get();
        String storedHash = user.getPassword();
        // Verifica password
        boolean passwordMatch = BCrypt.checkpw(rawPassword, storedHash);
        if (!passwordMatch) {
            System.out.println("[AuthService] Login fallito per: " + email + " (Password errata)");
            throw new SecurityException("Credenziali non valide.");
        }
        // DTO senza password
        System.out.println("[AuthService] Login successo: " + email);
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }

    /**
     * Gestisce la registrazione di un nuovo utente.
     * Hasha la password con un nuovo Salt prima di salvarla nel database.
     *
     * @param username    il nome utente scelto.
     * @param email       l'email dell'utente.
     * @param rawPassword la password in chiaro scelta.
     * @return un oggetto {@link UserDTO} con i dati dell'utente registrato.
     * @throws SQLException             in caso di errore nel database.
     * @throws IllegalArgumentException se la password è troppo corta.
     * @throws RuntimeException          se si verifica un errore durante la registrazione.
     */
    public UserDTO registerUser(String username, String email, String rawPassword) throws SQLException {
        // Validazione dati
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException("Password troppo corta (minimo 8 caratteri)");
        }
        // Hashing
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt(WORK_FACTOR));
        User newUser = new User(null, username, hashedPassword, email);
        boolean success = userDAO.insertNewUser(newUser);
        if (!success) {
            throw new RuntimeException("Errore durante la registrazione");
        }
        System.out.println("[AuthService] Registrazione avvenuta con successo per: " + email);
        return new UserDTO(null, username, email);
    }
}
