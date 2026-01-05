package it.uniupo.msvm.ui.controllers.auth;

import it.uniupo.msvm.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import javafx.scene.control.Label;

/**
 * Controller responsabile della gestione dell'autenticazione utente (Login).
 * <p>
 * Questa classe funge da punto di ingresso per l'applicazione, gestendo l'interazione
 * con la vista di login definita in {@code login.fxml}.
 * </p>
 *
 * <h3>Funzionalità e Metodi Principali:</h3>
 * <ul>
 * <li>{@link #handleLogin()} : Verifica le credenziali inserite e permette l'accesso.</li>
 * <li>{@link #handleRegistrazione(ActionEvent)} : Gestisce la navigazione verso la schermata di registrazione.</li>
 * </ul>
 *
 * @author Luca Lupi
 * @version 1.0
 */
public class LoginController {

    /** Campo di testo per l'inserimento dello username dell'utente. */
    @FXML
    private TextField txtUsername;

    /** Campo per l'inserimento della password (i caratteri vengono oscurati). */
    @FXML
    private PasswordField txtPassword; // PasswordField, non TextField!

    /** Etichetta per visualizzare messaggi di errore (es. "Credenziali errate") all'utente. */
    @FXML
    private Label lblErrore;

    /**
     * Gestisce l'evento di click sul pulsante "Accedi" (Login).
     * <p>
     * Recupera i dati inseriti nei campi username e password.
     * Attualmente implementa una validazione statica (hardcoded) controllando
     * se username e password corrispondono alla stringa "admin".
     * <br>
     * Se l'autenticazione fallisce, rende visibile la label di errore.
     * </p>
     */
    @FXML
    public void handleLogin(){
        System.out.println("Login Premuto");

        // Uso l'operatore ternario per evitare NullPointerException se i campi non sono ancora inizializzati
        String user = (txtUsername != null) ? txtUsername.getText() : "";
        String password = (txtPassword != null) ? txtPassword.getText() : "";

        // Simulazione validazione (DA SOSTITUIRE con verifica su Database in produzione)
        if(user.equals("admin") && password.equals("admin")){
            System.out.println("Login Riuscito!");
            if(lblErrore != null) lblErrore.setVisible(false);

            // TODO: Qui bisognerebbe chiudere la finestra o navigare alla Home
        }
        else{
            System.out.println("Login Fallito");
            if(lblErrore != null){
                lblErrore.setText("Utente o password errati");
                lblErrore.setVisible(true);
            }
        }
    }

    /**
     * Gestisce la navigazione verso la schermata di Registrazione.
     * <p>
     * Viene invocato quando l'utente clicca sul link o bottone per registrarsi.
     * Carica il file FXML {@code ui/auth/register.fxml} e sostituisce la scena corrente
     * all'interno dello stesso Stage (finestra), mantenendo l'esperienza utente fluida.
     * </p>
     *
     * @param event L'evento di azione (click) necessario per recuperare lo Stage corrente.
     * @throws IOException Se il file FXML della registrazione non viene trovato o non può essere caricato.
     */
    @FXML
    public void handleRegistrazione(ActionEvent event) throws IOException {
        System.out.println("Premuto registrazione");

        // 1. Recuperiamo la finestra (Stage) attuale dal bottone che è stato cliccato
        // Questo è il trucco per non aprire una seconda finestra!
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // 2. Carichiamo il file FXML della REGISTRAZIONE
        // Assicurarsi che il percorso sia corretto rispetto alla root delle risorse
        FXMLLoader loader = new FXMLLoader(App.class.getResource("ui/auth/register.fxml"));

        // Carica il contenuto della vista
        Parent root = loader.load();

        // 3. Creiamo la nuova scena
        Scene scene = new Scene(root, 600, 500);

        // 4. Impostiamo la nuova scena sulla STESSA finestra
        stage.setTitle("MSVM Studio - Registrazione");
        stage.setScene(scene);
        stage.show();
    }
}