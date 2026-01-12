package it.uniupo.msvm.server.persistence.dao;

import it.uniupo.msvm.common.model.User;
import it.uniupo.msvm.server.persistence.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Data Access Object (DAO) per l'entità User.
 * <p>
 * Questa classe gestisce tutte le operazioni CRUD (Create, Read, Update, Delete)
 * verso il database per la tabella 'users'.
 * </p>
 */
public class UserDAO {

    private final DatabaseManager dbManager;

    public UserDAO() {
        this.dbManager = new DatabaseManager();
    }

    /**
     * Cerca un utente nel database tramite il suo indirizzo email.
     * <p>
     * Grazie all'indice 'idx_user_email' (se presente sul DB), questa operazione
     * è ottimizzata con complessità O(logN).
     * </p>
     *
     * @param email L'indirizzo email da cercare.
     * @return Un {@link Optional} contenente l'utente se trovato, altrimenti vuoto.
     * @throws SQLException Se si verifica un errore di accesso al database.
     */
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT id, username, email, password FROM users WHERE email = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Errore ricerca per email: " + e.getMessage());
            throw e;
        } finally {
            dbManager.close();
        }
        return Optional.empty();
    }

    /**
     * Cerca un utente nel database tramite il suo ID univoco (Primary Key).
     *
     * @param userId L'ID dell'utente da cercare.
     * @return L'oggetto {@link User} trovato, oppure {@code null} se non esiste.
     * @throws SQLException Se si verifica un errore di accesso al database.
     */
    public User findById(Long userId) throws SQLException {
        String sql = "SELECT id, username, email, password FROM users WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Errore ricerca per ID: " + e.getMessage());
            throw e;
        } finally {
            dbManager.close();
        }
        return null;
    }

    /**
     * Inserisce un nuovo utente nel database.
     *
     * @param user L'oggetto User contenente i dati da salvare.
     * @return {@code true} se l'inserimento è andato a buon fine, {@code false} altrimenti.
     * @throws SQLException Se si verifica un errore SQL (es. vincolo UNIQUE violato).
     */
    public boolean insertNewUser(User user) throws SQLException {
        // NOTA: Assumiamo che ID non sia AUTO_INCREMENT se lo passi qui.
        // Se è AUTO_INCREMENT, rimuovi 'id' dalla query e dai parametri.
        String sql = "INSERT INTO users (id, username, password, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user.getId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getEmail());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            System.err.println("[DAO] Errore inserimento utente: " + e.getMessage());
            throw e;
        } finally {
            dbManager.close();
        }
    }

    /**
     * Elimina un utente dal database basandosi sul suo ID.
     *
     * @param userId L'ID dell'utente da eliminare.
     * @return {@code true} se l'utente è stato cancellato, {@code false} se l'utente non esisteva.
     * @throws SQLException Se si verifica un errore di accesso al database.
     */
    public boolean deleteUser(Long userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            int rowsAffected = stmt.executeUpdate();
            // Restituisce true se è stata cancellata esattamente una riga
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] Errore eliminazione utente: " + e.getMessage());
            throw e;
        } finally {
            dbManager.close();
        }
    }
}