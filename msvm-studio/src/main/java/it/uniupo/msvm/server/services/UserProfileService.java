package it.uniupo.msvm.server.services;

import it.uniupo.msvm.common.dto.FullProfileDTO;
import it.uniupo.msvm.common.model.User;
import it.uniupo.msvm.common.model.UserProfile;
import it.uniupo.msvm.server.persistence.dao.UserDAO;
import it.uniupo.msvm.server.persistence.dao.UserProfileDAO;

import java.sql.SQLException;

/**
 * Servizio di business logic per la gestione del profilo utente.
 * <p>
 * Questa classe agisce come un <i>Facade</i> verso il livello di persistenza (DAO),
 * aggregando i dati provenienti da due diverse entità:
 * <ul>
 * <li>{@link User}: contenente le credenziali e i dati di accesso (tabella 'users').</li>
 * <li>{@link UserProfile}: contenente i dettagli anagrafici (tabella 'user_profiles').</li>
 * </ul>
 * Il servizio si occupa di assemblare questi dati in un {@link FullProfileDTO} per il client
 * e di smistarli correttamente durante le operazioni di aggiornamento.
 * </p>
 */
public class UserProfileService {

    /** DAO per l'accesso ai dati dell'account utente. */
    private final UserDAO userDAO;

    /** DAO per l'accesso ai dati di dettaglio del profilo. */
    private final UserProfileDAO userProfileDAO;

    /**
     * Costruttore predefinito.
     * Inizializza i DAO necessari per le operazioni sui dati.
     */
    public UserProfileService() {
        this.userDAO = new UserDAO();
        this.userProfileDAO = new UserProfileDAO();
    }

    /**
     * Recupera il profilo completo di un utente.
     * <p>
     * Questo metodo esegue due query distinte: una per recuperare l'utente base (User)
     * e una per i dettagli (UserProfile). Se il profilo di dettaglio non esiste ancora
     * nel database, ne viene creato uno vuoto in memoria per evitare null pointer exception nel client.
     * </p>
     *
     * @param userId L'identificativo univoco dell'utente.
     * @return Un oggetto {@link FullProfileDTO} contenente tutti i dati unificati.
     * @throws RuntimeException Se l'utente con l'ID specificato non esiste o in caso di errore SQL.
     */
    public FullProfileDTO getFullProfile(Long userId) {
        try {
            // 1. Recupera i dati di base (Account)
            User user = userDAO.findById(userId);
            if (user == null) {
                throw new RuntimeException("Utente non trovato con ID: " + userId);
            }

            // 2. Recupera i dettagli del profilo
            UserProfile profile = userProfileDAO.findByUserId(userId);

            // Fallback: se il profilo non esiste, usiamo un oggetto vuoto
            if (profile == null) {
                profile = new UserProfile(userId, "", "", "", "");
            }

            // 3. Unisce tutto nel DTO da inviare al client
            return new FullProfileDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    profile.getFirstName(),
                    profile.getLastName(),
                    profile.getBio(),
                    profile.getPhoneNumber()
            );

        } catch (SQLException e) {
            e.printStackTrace(); // Log lato server
            throw new RuntimeException("Errore DB nel recupero profilo: " + e.getMessage());
        }
    }

    /**
     * Aggiorna i dati del profilo completo.
     * <p>
     * L'operazione è atomica dal punto di vista logico: aggiorna sia i dati nella tabella
     * 'users' (es. email) sia quelli nella tabella 'user_profiles' (es. bio, nome).
     * Gestisce automaticamente la distinzione tra INSERT (se il profilo non esisteva)
     * e UPDATE.
     * </p>
     *
     * @param dto L'oggetto DTO contenente i dati aggiornati provenienti dal client.
     * @return {@code true} se l'operazione è andata a buon fine, {@code false} se l'utente non è stato trovato.
     * @throws RuntimeException In caso di errori di accesso al database (SQLException).
     */
    public boolean updateFullProfile(FullProfileDTO dto) {
        try {
            // 1. Aggiorna l'email nella tabella Users
            User user = userDAO.findById(dto.getUserId());
            if (user != null) {
                user.setEmail(dto.getEmail());
                userDAO.updateUser(user);
            } else {
                return false; // L'utente principale non esiste, impossibile aggiornare
            }

            // 2. Prepara l'oggetto modello per la tabella UserProfiles
            UserProfile profileToUpdate = new UserProfile(
                    dto.getUserId(),
                    dto.getFirstName(),
                    dto.getLastName(),
                    dto.getBio(),
                    dto.getPhoneNumber()
            );

            // 3. Esegue UPDATE o INSERT a seconda se il record esiste già
            if (userProfileDAO.exists(dto.getUserId())) {
                return userProfileDAO.updateProfile(profileToUpdate);
            } else {
                return userProfileDAO.insertNewProfile(profileToUpdate);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log lato server
            throw new RuntimeException("Errore DB nell'aggiornamento profilo: " + e.getMessage());
        }
    }
}