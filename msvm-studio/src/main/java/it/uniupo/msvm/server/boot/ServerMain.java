package it.uniupo.msvm.server.boot;

import it.uniupo.msvm.server.controller.MsvmRemoteController;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    private static final int PORT = 1099; // Porta Standard RMI

    public static void main(String[] args) {
        try {
            System.out.println(">>> MSVM Server Starting...");

            //Primo passo in assolutto e quello di Inizializzare Database 
            //(Crea tabelle se non esistono) che non ce ancora ticket MSVM-21/22
            // DatabaseManager.initialize(); 

            //Secondo step :Avvia RMI Registry
            //--createRegistry avvia il registro in-process. 
            //Best pratcice sarebbe un processo separato, ma per dev va benissimo così.
            Registry registry = LocateRegistry.createRegistry(PORT);
            System.out.println(">>> RMI Registry started on port " + PORT);

            //Crea e Binda il servizio
            MsvmRemoteController service = new MsvmRemoteController();
            registry.rebind("MsvmService", service);

            System.out.println(">>> Service 'MsvmService' is ready and listening.");
            // per rmi non serve il keep alive. 
        } catch (Exception e) {
            System.err.println("CRITICAL SERVER ERROR:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
