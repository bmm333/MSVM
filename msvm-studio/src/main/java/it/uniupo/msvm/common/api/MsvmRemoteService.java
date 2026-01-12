package it.uniupo.msvm.common.api;

import java.rmi.RemoteException;
import java.util.List;
//attendo il merge di @author Lucaluppi 
//import it.uniupo.msvm.common.dto.UserDTO;


/**
 * Interfaccia remota che espone i servizi del backend MSVM 
 * */

public interface MsvmRemoteService extends Remote{
    //auth 
    //forse dobbiamo definire sia email che username? per un ux migliore magari possiamo gestirlo in un altro punto e qua normalizziamo
    UserDto login(String email,String password) throws RemoteException;
    UserDTO singup(String email,String username,String password) throws RemoteException;
    //Library
    /**
     * scaruca solo il codice sorgente
     * */
    String getLibrary(String library) throws RemoteException;
    //Get all 
    List<String> getAvalibleLibraries() throws RemoteException;
}
