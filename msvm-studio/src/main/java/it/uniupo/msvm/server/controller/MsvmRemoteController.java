package it.uniupo.msvm.server.controller;

import it.uniupo.msvm.common.api.MsvmRemoteService;
import it.uniupo.msvm.common.dto.UserDTO;
import it.uniupo.msvm.server.services.AuthService;
import it.uniupo.msvm.server.services.LibraryService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class MsvmRemoteController extends UnicastRemoteObject implements MsvmRemoteService {
    
    // Dependency Injection (Manuale per ora)
    private final AuthService authService;
    private final LibraryService libService;

    public MsvmRemoteController() throws RemoteException {
        super();
        // In un mondo ideale useremmo Guice/Spring ma va bene cosi composition root.
        this.authService = new AuthService();
        this.libService = new LibraryService();
    }

    @Override
    public UserDTO login(String email, String pass) throws RemoteException {
        try {
            System.out.println("[RMI] Login request for: " + email);
            return authService.authenticate(email, pass);
        } catch (Exception e) {
            // Loggare l'errore vero sul server, ma mandare un messaggio 
            System.err.println("Login error: " + e.getMessage());
            throw new RemoteException("Login failed: " + e.getMessage());
        }
    }

    @Override
    public UserDTO register(String username, String email, String pass) throws RemoteException {
        return authService.registerUser(username, email, pass);
    }

    @Override
    public String fetchLibrary(String name) throws RemoteException {
        return libService.getLibraryContent(name);
    }
    
    @Override
    public List<String> getAvailableLibraries() throws RemoteException {
        return libService.listLibraries();
    }
}
