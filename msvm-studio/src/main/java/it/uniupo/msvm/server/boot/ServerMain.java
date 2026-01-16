package it.uniupo.msvm.server.boot;

import it.uniupo.msvm.server.controller.MsvmRemoteController;
import it.uniupo.msvm.server.persistence.DatabaseManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Classe principale per l'avvio del server MSVM.
 * Inizializza il database e registra il servizio RMI per i client remoti.
 */
public class ServerMain {
    /** Porta predefinita per il registro RMI. */
    private static final int PORT = 1099;

    /**
     * Punto di ingresso principale del server.
     *
     * @param args argomenti da riga di comando (non utilizzati).
     */
    public static void main(String[] args) {
        try {
            System.out.println(">>> Avvio Server MSVM...");
            System.out.println(">> Inizializzazione Database...");
            DatabaseManager.getInstance().initialize();
            Registry registry = LocateRegistry.createRegistry(PORT);
            System.out.println(">>> Registro RMI avviato sulla porta " + PORT);
            MsvmRemoteController service = new MsvmRemoteController();
            registry.rebind("MsvmService", service);
            System.out.println(">>> Servizio 'MsvmService' pronto e in ascolto.");
        } catch (Exception e) {
            System.err.println("ERRORE CRITICO DEL SERVER:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}