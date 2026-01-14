package it.uniupo.msvm.server.services;
import it.uniupo.msvm.common.model.Library;
import it.uniupo.msvm.server.persistence.dao.LibraryDAO;
import java.util.List;
import java.util.Optional;

public class LibraryService {
    private final LibraryDAO libraryDAO;
    public LibraryService() {
        this.libraryDAO = new LibraryDAO();
    }

    public String getLibraryContent(String libName) {
        // Rimuoviamo eventuale estensione se l'utente lha messo
        String cleanName = libName.replace(".msvm", "");
        Optional<Library> libOpt = libraryDAO.findByName(cleanName);
        if (libOpt.isEmpty()) {
            throw new RuntimeException("Libreria non trovata nel database: " + cleanName);
        }
        System.out.println("[LibraryService] Serving library: " + cleanName);
        return libOpt.get().getContent();    }

    public List<String> listLibraries() {
        return libraryDAO.findAllNames();
    }
}