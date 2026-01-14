package it.uniupo.msvm.server.persistence.dao;

import it.uniupo.msvm.common.model.Library;
import it.uniupo.msvm.server.persistence.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) per la gestione delle librerie nel database.
 * Fornisce metodi per recuperare informazioni sulle librerie memorizzate.
 */
public class LibraryDAO {
    /** Gestore del database. */
    private final DatabaseManager dbManager;

    /**
     * Costruttore che inizializza il gestore del database.
     */
    public LibraryDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Recupera una libreria per nome.
     *
     * @param name il nome della libreria da cercare.
     * @return un Optional contenente la libreria se trovata, altrimenti vuoto.
     */
    public Optional<Library> findByName(String name) {
        String sql = "SELECT name, content, description, version FROM libraries WHERE name = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Mappiamo la riga SQL nell'oggetto Java
                    Library lib = new Library(
                            rs.getString("name"),
                            rs.getString("content"),
                            rs.getString("description"),
                            rs.getInt("version")
                    );
                    return Optional.of(lib);
                }
            }
        } catch (SQLException e) {
            System.err.println("[LibraryDAO] Errore: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Ritorna la lista dei nomi di tutte le librerie disponibili.
     *
     * @return una lista di stringhe contenente i nomi delle librerie.
     */
    public List<String> findAllNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT name FROM libraries ORDER BY name ASC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("[LibraryDAO] Errore nel recupero delle librerie: " + e.getMessage());
        }
        return names;
    }
}
