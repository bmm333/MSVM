package it.uniupo.msvm.server.controller;

import it.uniupo.msvm.common.api.MsvmRemoteService;
import it.uniupo.msvm.common.dto.UserDTO;
import it.uniupo.msvm.server.services.AuthService;
import it.uniupo.msvm.server.services.LibraryService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Controller RMI per l'accesso remoto ai servizi del server MSVM.
 * Implementa l'interfaccia {@link MsvmRemoteService} per fornire funzionalità di
 * autenticazione e gestione librerie ai client remoti.
 */
public class MsvmRemoteController extends UnicastRemoteObject implements MsvmRemoteService {

    /** Servizio per la gestione dell'autenticazione. */
    private final AuthService authService;
    /** Servizio per la gestione delle librerie. */
    private final LibraryService libService;

    /**
     * Costruttore che inizializza i servizi necessari.
     *
     * @throws RemoteException in caso di errori durante l'esportazione dell'oggetto remoto.
     */
    public MsvmRemoteController() throws RemoteException {
        super();
        this.authService = new AuthService();
        this.libService = new LibraryService();
    }

    /**
     * Gestisce la richiesta di login da parte di un client.
     *
     * @param email l'email dell'utente.
     * @param pass  la password dell'utente.
     * @return un oggetto {@link UserDTO} con i dati dell'utente se l'autenticazione ha successo.
     * @throws RemoteException in caso di errore durante la procedura o credenziali errate.
     */
    @Override
    public UserDTO login(String email, String pass) throws RemoteException {
        try {
            System.out.println("[RMI] Richiesta login per: " + email);
            return authService.authenticate(email, pass);
        } catch (Exception e) {
            System.err.println("[RMI Errore] Login fallito: " + e.getMessage());
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * Gestisce la richiesta di registrazione di un nuovo utente.
     *
     * @param username il nome utente scelto.
     * @param email    l'email dell'utente.
     * @param pass     la password scelta.
     * @return un oggetto {@link UserDTO} con i dati dell'utente registrato.
     * @throws RemoteException in caso di errori durante la registrazione.
     */
    @Override
    public UserDTO register(String username, String email, String pass) throws RemoteException {
        try {
            System.out.println("[RMI] Richiesta registrazione per: " + username);
            return authService.registerUser(username, email, pass);
        } catch (Exception e) {
            System.err.println("[RMI Errore] Registrazione fallita: " + e.getMessage());
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * Recupera il contenuto di una libreria remota.
     *
     * @param name il nome della libreria da recuperare.
     * @return il codice assembly della libreria.
     * @throws RemoteException se la libreria non viene trovata o in caso di errore.
     */
    @Override
    public String fetchLibrary(String name) throws RemoteException {
        try {
            return libService.getLibraryContent(name);
        } catch (Exception e) {
            System.err.println("[RMI Errore] Recupero libreria fallito: " + e.getMessage());
            throw new RemoteException("Libreria non trovata o errore: " + e.getMessage());
        }
    }

    /**
     * Restituisce la lista di tutte le librerie disponibili sul server.
     *
     * @return una lista di stringhe con i nomi delle librerie.
     * @throws RemoteException in caso di errori durante il recupero della lista.
     */
    @Override
    public List<String> getAvailableLibraries() throws RemoteException {
        try {
            return libService.listLibraries();
        } catch (Exception e) {
            throw new RemoteException("Errore nel listare le librerie: " + e.getMessage());
        }
    }
}