package it.uniupo.msvm.server.persistence;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestore centralizzato del Database SQLite per il Server MSVM.
 * <p>
 *     Implementa il pattern Singleton.
 *     Abilita la modalità WAL (Write-Ahead Logging) e indici per prestazioni elevate,
 *     evitando colli di bottiglia tra operazioni di lettura e scrittura.
 * </p>
 * @author Arben Mema
 */
public class DatabaseManager {

    /** URL di connessione al database SQLite. */
    private static final String DB_URL = "jdbc:sqlite:msvm_server.db";
    /** Unica istanza della classe (Singleton). */
    private static DatabaseManager instance;
    /** Connessione attiva al database. */
    private Connection connection;

    /**
     * Costruttore privato. Carica il driver JDBC per SQLite.
     */
    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Critico: Driver JDBC SQLite non trovato.");
        }
    }

    /**
     * Restituisce l'unica istanza di DatabaseManager (thread-safe).
     *
     * @return l'istanza di DatabaseManager.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Ottiene la connessione al database.
     * Se la connessione è chiusa o nulla, ne apre una nuova.
     *
     * @return la connessione al database.
     * @throws SQLException in caso di errori di connessione.
     */
    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            configSettings(connection);
            System.out.println("Connessione al database stabilita.");
        }
        return connection;
    }

    /**
     * Applica i PRAGMA di SQLite per migliorare le performance e la concorrenza.
     *
     * @param connection la connessione da configurare.
     * @throws SQLException in caso di errori durante l'esecuzione dei comandi SQL.
     */
    private void configSettings(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Abilitiamo WAL per permettere lettura e scrittura concorrenti
            stmt.execute("PRAGMA journal_mode = WAL;");
            // Sincronizzazione bilanciata
            stmt.execute("PRAGMA synchronous = NORMAL;");
            // Abilita i vincoli di integrità referenziale (Foreign Keys)
            stmt.execute("PRAGMA foreign_keys = ON;");
            // Aumento della dimensione della cache per la velocità
            stmt.execute("PRAGMA cache_size = -2000;");
        }
    }

    /**
     * Inizializza lo schema del database (tabelle e indici).
     * Deve essere chiamato all'avvio del server.
     */
    public void initialize() {
        System.out.println("Inizializzazione dello schema del database...");
        // Query per la tabella degli utenti
        String sqlCreateUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                email TEXT NOT NULL,
                password TEXT NOT NULL,  -- Hash della password
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """;

        String sqlCreateLibs = """
            CREATE TABLE IF NOT EXISTS libraries (
                name TEXT PRIMARY KEY,
                content TEXT NOT NULL,
                description TEXT,
                version INTEGER DEFAULT 1
            );
        """;

        // Seeding iniziale
        String sqlSeedLib = """
            INSERT OR IGNORE INTO libraries (name, content, description) 
            VALUES ('std', '; Standard Library MSVM\n\n:MATH_PI\n PUSH 3\n RET', 'Libreria Standard');
        """;

        // Query per gli indici
        String sqlIndexEmail = "CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users(email);";
        String sqlIndexUser = "CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username ON users(username);";
        String sqlIndexId = "CREATE UNIQUE INDEX IF NOT EXISTS idx_users_id ON users(id);";

        // Tabella per l'audit dei download
        String sqlCreateAudit = """
            CREATE TABLE IF NOT EXISTS download_audit (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_email TEXT,
                library_name TEXT,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """;

        String sqlCreateProfiles = """
            CREATE TABLE IF NOT EXISTS user_profiles (
                user_id INTEGER PRIMARY KEY,
                first_name TEXT,
                last_name TEXT,
                bio TEXT,
                phone_number TEXT,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            );
        """;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCreateUsers);
            stmt.execute(sqlCreateAudit);
            stmt.execute(sqlIndexEmail);
            stmt.execute(sqlIndexUser);
            stmt.execute(sqlIndexId);
            stmt.execute(sqlCreateLibs);
            stmt.execute(sqlSeedLib);
            stmt.execute(sqlCreateProfiles);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'inizializzazione dello schema del database: " + e.getMessage());
        }
    }
    /**
     * Chiude la connessione al database in modo sicuro.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connessione chiusa correttamente.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }
}
