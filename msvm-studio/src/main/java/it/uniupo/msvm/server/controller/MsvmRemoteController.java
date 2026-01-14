package it.uniupo.msvm.server.controller;

import it.uniupo.msvm.common.api.MsvmRemoteService;
import it.uniupo.msvm.common.dto.UserDTO;
import it.uniupo.msvm.server.services.AuthService;
import it.uniupo.msvm.server.services.LibraryService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class MsvmRemoteController extends UnicastRemoteObject implements MsvmRemoteService {

    private final AuthService authService;
    private final LibraryService libService;

    public MsvmRemoteController() throws RemoteException {
        super();
        this.authService = new AuthService();
        this.libService = new LibraryService();
    }
    @Override
    public UserDTO login(String email, String pass) throws RemoteException {
        try {
            System.out.println("[RMI] Login request for: " + email);
            return authService.authenticate(email, pass);
        } catch (Exception e) {
            System.err.println("[RMI Error] Login failed: " + e.getMessage());
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public UserDTO register(String username, String email, String pass) throws RemoteException {
        try {
            System.out.println("[RMI] Register request for: " + username);
            return authService.registerUser(username, email, pass);
        } catch (Exception e) {
            System.err.println("[RMI Error] Register failed: " + e.getMessage());
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public String fetchLibrary(String name) throws RemoteException {
        try {
            return libService.getLibraryContent(name);
        } catch (Exception e) {
            System.err.println("[RMI Error] Fetch lib failed: " + e.getMessage());
            throw new RemoteException("Library not found or error: " + e.getMessage());
        }
    }

    @Override
    public List<String> getAvailableLibraries() throws RemoteException {
        try {
            return libService.listLibraries();
        } catch (Exception e) {
            throw new RemoteException("Error listing libraries: " + e.getMessage());
        }
    }
}