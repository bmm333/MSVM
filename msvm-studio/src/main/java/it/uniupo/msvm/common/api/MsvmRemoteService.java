package it.uniupo.msvm.common.api;

import it.uniupo.msvm.common.dto.UserDTO;
import it.uniupo.msvm.common.model.User;

import java.rmi.RemoteException;
import java.util.List;
import java.rmi.Remote;
//attendo il merge di @author Lucaluppi 
//import it.uniupo.msvm.common.dto.UserDTO;


/**
 * Interfaccia remota che espone i servizi del backend MSVM 
 * */

public interface MsvmRemoteService extends Remote{
    //auth 
    //forse dobbiamo definire sia email che username? per un ux migliore magari possiamo gestirlo in un altro punto e qua normalizziamo
    UserDTO login(String email, String password) throws RemoteException;
    UserDTO register(String email, String username, String password) throws RemoteException;
    //Library
    /**
     * scarica solo il codice sorgente
     * */
    String fetchLibrary(String library) throws RemoteException;
    //Get all 
    List<String> getAvailableLibraries() throws RemoteException;
}
