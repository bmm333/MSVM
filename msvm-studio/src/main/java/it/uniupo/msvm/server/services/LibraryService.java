package it.uniupo.msvm.server.services;
import it.uniupo.msvm.common.model.Library;
import it.uniupo.msvm.server.persistence.dao.LibraryDAO;
import java.util.List;
import java.util.Optional;

/**
 * Service Layer per la gestione delle librerie.
 * Si interfaccia con il DAO per fornire la logica di business relativa alle librerie.
 */
public class LibraryService {
    /** DAO per l'accesso ai dati delle librerie. */
    private final LibraryDAO libraryDAO;

    /**
     * Costruttore che inizializza il DAO delle librerie.
     */
    public LibraryService() {
        this.libraryDAO = new LibraryDAO();
    }

    /**
     * Recupera il contenuto di una libreria dato il suo nome.
     *
     * @param libName il nome della libreria.
     * @return il contenuto (codice assembly) della libreria.
     * @throws RuntimeException se la libreria non viene trovata.
     */
    public String getLibraryContent(String libName) {
        // Rimuoviamo eventuale estensione se presente
        String cleanName = libName.replace(".msvm", "");
        Optional<Library> libOpt = libraryDAO.findByName(cleanName);
        if (libOpt.isEmpty()) {
            throw new RuntimeException("Libreria non trovata nel database: " + cleanName);
        }
        System.out.println("[LibraryService] Servizio libreria: " + cleanName);
        return libOpt.get().getContent();
    }

    /**
     * Restituisce la lista dei nomi di tutte le librerie disponibili.
     *
     * @return una lista di stringhe con i nomi delle librerie.
     */
    public List<String> listLibraries() {
        return libraryDAO.findAllNames();
    }
}