package it.uniupo.msvm.server.persistence;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestore centralizzato del Database SQLite per il Server MSVM.
 * <p>
 *     Implementa il pattern Singleton.
 *     Abilitando WAL e inidici per presetazioni elevetae ,e evitare colli di bottiglia tra Rd e Wr
 *     durante la registrazione e login.
 * </p>
 * @author Arben Mema
 * */
public class DatabaseManager {

    private static final String DB_URL="jdbc:sqlite:msvm_server.db";
    //Singleton
    private static  DatabaseManager instance;
    private Connection connection;

    /**
     * Costruttore private. Carica il driver JDBC.
     * */
    private DatabaseManager()
    {
        try{
            //carica il driver
            Class.forName("org.sqlite.JDBC");
        }catch (ClassNotFoundException e){
            throw new RuntimeException("Critical: Sqlite JDBC driver not found.");
        }
    }
    //Singletone access: thread-safe
    public static synchronized DatabaseManager getInstance()
    {
        if(instance==null)
        {
            instance=new DatabaseManager();
        }
        return instance;
    }
    /**
     * Ottinee la connessione al db
     * Se la connesione e chiusa o nulla, ne apre una nuova
     * */
    public synchronized Connection getConnection() throws SQLException{
        if(connection==null||connection.isClosed())
        {
            connection= DriverManager.getConnection(DB_URL);
            configSettings(connection);
            System.out.println("Database connection established.");
        }
        return connection;
    }
    /**
     * Applicando i PRAGMA di SQLite per avere performance migliore e concorrenz (riduciamo p99)
     * */
    private void configSettings(Connection connection) throws SQLException{
        try(Statement stmt= connection.createStatement())
        {
            //Abilitiamo WAL per permettere lettura e scrittura concorrenti,
            //Se wal disabilitato sqlite bloccherebbe tutto il db se qualcuno e in WR
            stmt.execute("PRAGMA journal_mode = WAL;");
            //synch normal , bilanciato
            stmt.execute("PRAGMA synchronous = NORMAL;");
            //fk abilita i vincoli di integrita ref
            stmt.execute("PRAGMA foreign_keys = ON;");
            //aumento della cache size per veolocita
            stmt.execute("PRAGMA cache_size = -2000;");
        }
    }
    /**
     * Inizializza lo schema del database (Tabella e indici(
     * Da chiamare obbligatoriamente all avvio del server
     * */
    public void initialize()
    {
        System.out.println("Initializing database schema...");
        //query per la tabella degli utenti
        String sqlCreateUsers="""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                email TEXT NOT NULL,
                password TEXT NOT NULL,  -- Hash della password (BCrypt/SHA)
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """;
        //Querry per gli indici
        //Rende la ricerca per email/username/id O(logN)
        String sqlIndexEmail = "CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users(email);";
        String sqlIndexUser = "CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username ON users(username);";
        String sqlIndexId = "CREATE UNIQUE INDEX IF NOT EXISTS idx_users_id ON users(id);";

        //tabella per tracciare i download delle librerie forse?
        String sqlCreateAudit = """
            CREATE TABLE IF NOT EXISTS download_audit (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_email TEXT,
                library_name TEXT,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """;
        try(Connection conn=getConnection();
        Statement stmt=conn.createStatement())
        {
            stmt.execute(sqlCreateUsers);
            stmt.execute(sqlCreateAudit);
            stmt.execute(sqlIndexEmail);
            stmt.execute(sqlIndexUser);
            stmt.execute(sqlIndexId);
        }
    }
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed gracefully.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}
