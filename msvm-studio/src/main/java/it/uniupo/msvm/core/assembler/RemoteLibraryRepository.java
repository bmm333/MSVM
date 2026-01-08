package it.uniupo.msvm.core.assembler;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface RemoteLibraryRepository extends Remote {
    List<String> fetchLibrary(String libName) throws RemoteException;
}
