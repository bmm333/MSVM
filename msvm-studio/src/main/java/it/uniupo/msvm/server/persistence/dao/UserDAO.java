package it.uniupo.msvm.server.persistence.dao;

import it.uniupo.msvm.common.model.User;
import it.uniupo.msvm.server.persistence.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) per l'entità User.
 * <p>
 * Questa classe gestisce tutte le operazioni CRUD (Create, Read, Update, Delete)
 * verso il database per la tabella 'users'.
 */
public class UserDAO {
    /** Gestore del database. */
    private final DatabaseManager dbManager;

    /**
     * Costruttore che inizializza il gestore del database recuperando l'istanza singleton.
     */
    public UserDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Cerca un utente tramite il suo identificativo univoco (ID).
     *
     * @param userId l'ID dell'utente.
     * @return l'utente trovato o null se non esiste.
     * @throws SQLException in caso di errore nel database.
     */
    public User findById(Long userId) throws SQLException {
        String sql = "SELECT id, username, email, password FROM users WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Errore findById: " + e.getMessage());
            throw e;
        }
        return null;
    }

    /**
     * Cerca un utente tramite l'indirizzo Email.
     *
     * @param email l'email da cercare.
     * @return un Optional contenente l'utente se trovato, altrimenti vuoto.
     * @throws SQLException in caso di errore nel database.
     */
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT id, username, email, password FROM users WHERE email = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Errore findByEmail: " + e.getMessage());
            throw e;
        }
        return Optional.empty();
    }

    /**
     * Cerca un utente tramite il nome utente (Username).
     * Fondamentale per la procedura di Login.
     *
     * @param username lo username da cercare.
     * @return l'utente trovato o null se non esiste.
     * @throws SQLException in caso di errore nel database.
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, email, password FROM users WHERE username = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Errore findByUsername: " + e.getMessage());
            throw e;
        }
        return null;
    }

    /**
     * Recupera la lista di TUTTI gli utenti.
     * Utile per pannelli di amministrazione o debug.
     *
     * @return Una lista di utenti (potrebbe essere vuota).
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT id, username, email, password FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("[DAO] Errore findAll: " + e.getMessage());
            throw e;
        }
        return users;
    }

    /**
     * Inserisce un nuovo utente nel database.
     * <p>
     * Se l'ID dell'utente è {@code null}, viene utilizzato l'autoincremento del database.
     * </p>
     *
     * @param user l'utente da salvare.
     * @return {@code true} se l'inserimento ha successo, {@code false} altrimenti.
     * @throws SQLException in caso di errori nel database.
     */
    public boolean insertNewUser(User user) throws SQLException {
        String sql;
        if (user.getId() == null) {
            sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        } else {
            sql = "INSERT INTO users (id, username, password, email) VALUES (?, ?, ?, ?)";
        }

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (user.getId() == null) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPassword());
                stmt.setString(3, user.getEmail());
            } else {
                stmt.setLong(1, user.getId());
                stmt.setString(2, user.getUsername());
                stmt.setString(3, user.getPassword());
                stmt.setString(4, user.getEmail());
            }

            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("[DAO] Errore insertNewUser: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Aggiorna i dati di un utente esistente.
     * <p>
     * Questo metodo sovrascrive username, password ed email per l'utente
     * identificato dal suo ID.
     * </p>
     *
     * @param user L'oggetto User con i dati aggiornati e l'ID corretto.
     * @return {@code true} se l'aggiornamento è riuscito, {@code false} altrimenti.
     * @throws SQLException Errore database.
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, email = ? WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setLong(4, user.getId()); // WHERE id = ?

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] Errore updateUser: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Aggiorna SOLO la password di un utente.
     * Metodo di utilità per il cambio password rapido.
     */
    public boolean updatePassword(Long userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setLong(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    // ==================================================================================
    //                                  DELETE (Cancellazione)
    // ==================================================================================

    /**
     * Elimina un utente.
     * @param userId ID dell'utente.
     * @return true se eliminato.
     */
    public boolean deleteUser(Long userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] Errore deleteUser: " + e.getMessage());
            throw e;
        }
    }

    // ==================================================================================
    //                                  UTILITY (Helper)
    // ==================================================================================

    /**
     * Verifica se esiste già un utente con questo username.
     * Ottimizzato per ritornare solo un booleano (SELECT 1).
     */
    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Verifica se esiste già un utente con questa email.
     */
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Metodo privato di supporto per mappare una riga del ResultSet in un oggetto User.
     * Evita di duplicare questo codice in ogni metodo di lettura.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email")
        );
    }
}