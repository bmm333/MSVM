package it.uniupo.msvm.server.persistence.dao;

import it.uniupo.msvm.common.model.UserProfile;
import it.uniupo.msvm.server.persistence.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) per l'entità UserProfile.
 * <p>
 * Questa classe gestisce le operazioni di lettura e scrittura sulla tabella 'user_profiles'.
 * La tabella è in relazione 1-a-1 con la tabella 'users'.
 * </p>
 */
public class UserProfileDAO {

    private final DatabaseManager dbManager;

    public UserProfileDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    // ==================================================================================
    //                                  CREATE (Inserimento)
    // ==================================================================================

    /**
     * Crea un nuovo profilo nel database.
     * Solitamente viene chiamato subito dopo la creazione di un nuovo Utente.
     *
     * @param profile L'oggetto UserProfile da salvare.
     * @return {@code true} se l'inserimento ha successo.
     * @throws SQLException Errore database o violazione vincoli (es. profilo già esistente per quell'ID).
     */
    public boolean insertNewProfile(UserProfile profile) throws SQLException {
        String sql = "INSERT INTO user_profiles (user_id, first_name, last_name, bio, phone_number) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, profile.getUserId());
            stmt.setString(2, profile.getFirstName());
            stmt.setString(3, profile.getLastName());
            stmt.setString(4, profile.getBio());
            stmt.setString(5, profile.getPhoneNumber());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            System.err.println("[DAO] Errore inserimento profilo: " + e.getMessage());
            throw e;
        }
    }

    // ==================================================================================
    //                                  READ (Lettura)
    // ==================================================================================

    /**
     * Cerca il profilo associato a un determinato ID utente.
     *
     * @param userId L'ID dell'utente proprietario del profilo.
     * @return L'oggetto {@link UserProfile} se trovato, altrimenti {@code null}.
     * @throws SQLException Errore di connessione o query.
     */
    public UserProfile findByUserId(Long userId) throws SQLException {
        String sql = "SELECT user_id, first_name, last_name, bio, phone_number FROM user_profiles WHERE user_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProfile(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Errore ricerca profilo per ID: " + e.getMessage());
            throw e;
        }
        return null;
    }

    /**
     * Verifica velocemente se esiste un profilo per un dato utente.
     * Utile per decidere se fare una INSERT o una UPDATE.
     *
     * @param userId L'ID dell'utente.
     * @return {@code true} se il profilo esiste.
     */
    public boolean exists(Long userId) throws SQLException {
        String sql = "SELECT 1 FROM user_profiles WHERE user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ==================================================================================
    //                                  UPDATE (Aggiornamento)
    // ==================================================================================

    /**
     * Aggiorna i dati anagrafici di un profilo esistente.
     *
     * @param profile L'oggetto contenente i dati aggiornati. L'ID deve corrispondere a un record esistente.
     * @return {@code true} se l'aggiornamento è andato a buon fine.
     * @throws SQLException Errore SQL.
     */
    public boolean updateProfile(UserProfile profile) throws SQLException {
        String sql = "UPDATE user_profiles SET first_name = ?, last_name = ?, bio = ?, phone_number = ? WHERE user_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profile.getFirstName());
            stmt.setString(2, profile.getLastName());
            stmt.setString(3, profile.getBio());
            stmt.setString(4, profile.getPhoneNumber());
            // La clausola WHERE usa lo userId
            stmt.setLong(5, profile.getUserId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] Errore update profilo: " + e.getMessage());
            throw e;
        }
    }

    // ==================================================================================
    //                                  DELETE (Cancellazione)
    // ==================================================================================

    /**
     * Cancella il profilo di un utente.
     * Nota: Spesso non serve se sul DB è impostato "ON DELETE CASCADE" sulla foreign key,
     * ma è buona norma averlo nel DAO.
     *
     * @param userId L'ID dell'utente il cui profilo va cancellato.
     * @return {@code true} se cancellato con successo.
     */
    public boolean deleteProfile(Long userId) throws SQLException {
        String sql = "DELETE FROM user_profiles WHERE user_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] Errore delete profilo: " + e.getMessage());
            throw e;
        }
    }

    // ==================================================================================
    //                                  UTILITY
    // ==================================================================================

    /**
     * Metodo helper per convertire una riga del ResultSet in un oggetto UserProfile.
     *
     * @param rs Il ResultSet posizionato sulla riga corretta.
     * @return L'oggetto UserProfile popolato.
     * @throws SQLException In caso di errore di lettura colonne.
     */
    private UserProfile mapRowToProfile(ResultSet rs) throws SQLException {
        return new UserProfile(
                rs.getLong("user_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("bio"),
                rs.getString("phone_number")
        );
    }
}