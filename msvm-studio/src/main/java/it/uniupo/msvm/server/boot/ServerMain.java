package it.uniupo.msvm.server.boot;

import it.uniupo.msvm.server.controller.MsvmRemoteController;
import it.uniupo.msvm.server.persistence.DatabaseManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    private static final int PORT = 1099;

    public static void main(String[] args) {
        try {
            System.out.println(">>> MSVM Server Starting...");
            System.out.println(">> Booting Database...");
            DatabaseManager.getInstance().initialize();
            Registry registry = LocateRegistry.createRegistry(PORT);
            System.out.println(">>> RMI Registry started on port " + PORT);
            MsvmRemoteController service = new MsvmRemoteController();
            registry.rebind("MsvmService", service);
            System.out.println(">>> Service 'MsvmService' is ready and listening.");
        } catch (Exception e) {
            System.err.println("CRITICAL SERVER ERROR:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}