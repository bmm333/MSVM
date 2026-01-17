package it.uniupo.msvm;

import it.uniupo.msvm.common.api.MsvmRemoteService;
import it.uniupo.msvm.common.dto.UserDTO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Classe principale dell'applicazione Client MSVM.
 * Gestisce l'avvio, la connessione al server RMI e la sessione utente.
 */
public class App extends Application {

    /**
     * Riferimento statico al servizio remoto (Stub).
     * I controller useranno questo oggetto per chiamare il server.
     */
    public static MsvmRemoteService server;

    /**
     * Utente attualmente loggato (Sessione).
     * Se null, l'utente è in modalità ospite/offline.
     */
    public static UserDTO currentUser;

    @Override
    public void start(Stage stage) throws Exception {
        // 1. Tenta la connessione al Server RMI all'avvio
        boolean connesso = connettiAlServer();

        if (!connesso) {
            mostraAvvisoConnessione();
        }

        // 2. Carica la Home Page (finestra principale che sta "sotto")
        // Assicurati che il percorso sia corretto rispetto alla cartella resources
        FXMLLoader loader = new FXMLLoader(App.class.getResource("ui/homepage/home.fxml"));
        Scene scene = new Scene(loader.load(), 1024, 768);

        stage.setTitle("MSVM Studio - Community Edition");
        stage.setScene(scene);
        stage.show();

        // Opzionale: Se vuoi aprire subito il login sopra la home
        if (connesso) {
            apriLoginAutomaticamente(stage);
        }
    }

    /**
     * Stabilisce la connessione con il Server RMI.
     * @return true se la connessione ha successo, false altrimenti.
     */
    private boolean connettiAlServer() {
        try {
            // Cerca il registro RMI sulla macchina locale porta 1099
            // Se il server è su un'altra macchina, cambia "localhost" con l'IP
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Ottiene lo stub del servizio remoto
            server = (MsvmRemoteService) registry.lookup("MsvmService");

            System.out.println("[Client] ✅ Connesso al server RMI.");
            return true;
        } catch (Exception e) {
            System.err.println("[Client] ❌ Errore connessione RMI: " + e.getMessage());
            return false;
        }
    }

    /**
     * Apre la finestra di login come modale sopra la Home.
     */
    private void apriLoginAutomaticamente(Stage ownerStage) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("ui/auth/login.fxml"));
            Stage loginStage = new Stage();
            loginStage.setTitle("Login - MSVM");
            loginStage.setScene(new Scene(loader.load()));

            // Imposta la finestra come modale (blocca la home finché non chiudi il login)
            // loginStage.initOwner(ownerStage);
            // loginStage.initModality(javafx.stage.Modality.WINDOW_MODAL);

            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostraAvvisoConnessione() {
        Alert alert = new Alert(Alert.AlertType.WARNING,
                "Impossibile connettersi al server MSVM.\nAlcune funzionalità (Login, Community) saranno disabilitate.",
                ButtonType.OK);
        alert.setTitle("Modalità Offline");
        alert.setHeaderText("Server non raggiungibile");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}