package it.uniupo.msvm.ui.controllers;

import it.uniupo.msvm.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller responsabile della gestione dell'interfaccia di registrazione utente.
 * <p>
 * Questa classe gestisce l'interazione con la form di registrazione ({@code register.fxml}),
 * validando i dati inseriti dall'utente (nome, cognome, email, password) e gestendo
 * la navigazione di ritorno verso la schermata di login.
 * </p>
 */
public class RegistrazioneController {

    /** Campo di testo per l'inserimento del nome. */
    @FXML private TextField txtNome;

    /** Campo di testo per l'inserimento del cognome. */
    @FXML private TextField txtCognome;

    /** Campo di testo per l'inserimento dell'email istituzionale. */
    @FXML private TextField txtEmail;

    /** Campo per l'inserimento della password (testo oscurato). */
    @FXML private PasswordField txtPassword;

    /** Campo per la conferma della password per evitare errori di digitazione. */
    @FXML private PasswordField txtConfermaPass;

    /** Etichetta utilizzata per mostrare messaggi di errore di validazione all'utente. */
    @FXML private Label lblErroreReg;

    /**
     * Gestisce l'evento di click sul pulsante "REGISTRATI ORA".
     * <p>
     * Esegue le seguenti operazioni:
     * <ol>
     * <li>Resetta eventuali messaggi di errore precedenti.</li>
     * <li>Valida che tutti i campi siano compilati.</li>
     * <li>Verifica che le due password inserite coincidano.</li>
     * <li>Verifica la lunghezza minima della password.</li>
     * <li>Simula la registrazione (o invoca il servizio di backend).</li>
     * <li>In caso di successo, reindirizza automaticamente alla pagina di Login.</li>
     * </ol>
     * </p>
     *
     * @param event L'evento generato dal click sul bottone, usato per gestire il flusso UI.
     */
    @FXML
    void handleRegistrazione(ActionEvent event) {
        // 1. Reset errori
        lblErroreReg.setVisible(false);

        String email = txtEmail.getText();
        String pass = txtPassword.getText();
        String confPass = txtConfermaPass.getText();

        // 2. Validazioni base
        if (txtNome.getText().isEmpty() || txtCognome.getText().isEmpty() || email.isEmpty() || pass.isEmpty()) {
            mostraErrore("Tutti i campi sono obbligatori!");
            return;
        }

        if (!pass.equals(confPass)) {
            mostraErrore("Le password non coincidono!");
            return;
        }

        if (pass.length() < 6) {
            mostraErrore("La password deve essere di almeno 6 caratteri.");
            return;
        }

        // 3. Successo (Qui chiameresti il Database)
        System.out.println("Nuovo utente registrato: " + email);

        // Opzionale: Torna automaticamente al login dopo il successo
        try {
            tornaALogin(event);
        } catch (IOException e) {
            e.printStackTrace();
            mostraErrore("Errore durante il caricamento della pagina di Login.");
        }
    }

    /**
     * Gestisce la navigazione per tornare alla schermata di Login.
     * <p>
     * Carica il file {@code login.fxml} e sostituisce la scena corrente nello Stage attuale,
     * permettendo all'utente di accedere con le credenziali appena create o esistenti.
     * </p>
     *
     * @param event L'evento di azione (click) utilizzato per recuperare lo Stage (finestra) corrente.
     * @throws IOException Se il file FXML 'ui/auth/login.fxml' non viene trovato o non può essere caricato.
     */
    @FXML
    void tornaALogin(ActionEvent event) throws IOException {
        System.out.println("Premuto Login - Torno indietro");

        // 1. Recuperiamo la finestra (Stage) attuale dal bottone che è stato cliccato
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // 2. Carichiamo il file FXML del LOGIN
        FXMLLoader loader = new FXMLLoader(App.class.getResource("ui/auth/login.fxml"));

        // Carica il contenuto
        Parent root = loader.load();

        // 3. Creiamo la nuova scena
        Scene scene = new Scene(root, 600, 500);

        // 4. Impostiamo la nuova scena sulla STESSA finestra
        // Nota: Qui potresti voler cambiare il titolo in "MSVM Studio - Login"
        stage.setTitle("MSVM Studio - Login");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Metodo di utilità per visualizzare un messaggio di errore nell'interfaccia.
     * Rende visibile la label di errore e imposta il testo specificato.
     *
     * @param msg Il messaggio di errore da mostrare all'utente.
     */
    private void mostraErrore(String msg) {
        lblErroreReg.setText(msg);
        lblErroreReg.setVisible(true);
    }
}