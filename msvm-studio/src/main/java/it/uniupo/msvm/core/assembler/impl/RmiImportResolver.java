package it.uniupo.msvm.core.assembler.impl;

import it.uniupo.msvm.core.assembler.ImportResolver;
import it.uniupo.msvm.core.assembler.RemoteLibraryRepository;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;


/**
 * Client RMI che si connette al server per scaricare le librerie
 * */
public class RmiImportResolver implements ImportResolver {
    private final String host;
    private  final int port;
    private final String serviceName;

    public RmiImportResolver(String host,int port,String serviceName) {
        this.host = host;
        this.port = port;
        this.serviceName = serviceName;
    }
    @Override
    public List<String> resolve(String dependencyIndentifier)
    {
        try{
            System.out.println("[RMI] Connecting to " + host + ":" + port + " for " + dependencyIndentifier);
            Registry registry = LocateRegistry.getRegistry(host,port);
            RemoteLibraryRepository repo = (RemoteLibraryRepository) registry.lookup(serviceName);

            return repo.fetchLibrary(dependencyIndentifier);
        }catch (Exception e)
        {
            System.err.println("[RMI] Error fetching library: " + e.getMessage());
            //Ritorniamo null per permettere al HybridResolver di gestire il fallimento
            // anche se in questo caso e l'ultima spiaggia quindi falira comunque :D
            return null;
        }
    }
}
