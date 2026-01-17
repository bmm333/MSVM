package it.uniupo.msvm.ui.controllers.auth;

import it.uniupo.msvm.App;
import it.uniupo.msvm.common.dto.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblErrore;

    /**
     * Esegue il login chiamando il server RMI.
     * Se le credenziali sono valide, chiude la finestra di login.
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        String email = txtUsername.getText();
        String password = txtPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            mostraErrore("Inserisci email e password.");
            return;
        }

        try {
            // Verifica connessione server
            if (App.server == null) {
                mostraErrore("Errore: Non connesso al server MSVM.");
                return;
            }

            System.out.println("[Client] Tentativo di login: " + email);

            // 1. Chiamata RMI al Server
            UserDTO user = App.server.login(email, password);

            if (user != null) {
                // 2. Login OK: Salva sessione globale
                App.currentUser = user;
                System.out.println("[Client] Login riuscito: " + user.getUsername());

                // 3. Chiude la finestra di login (rivelando la Home sotto)
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            } else {
                mostraErrore("Credenziali non valide.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostraErrore("Errore Login: " + e.getMessage());
        }
    }

    /**
     * Passa alla schermata di registrazione (nella stessa finestra).
     */
    @FXML
    public void handleRegistrazione(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // Assicurati che il percorso del FXML sia corretto
        FXMLLoader loader = new FXMLLoader(App.class.getResource("ui/auth/register.fxml"));
        Scene scene = new Scene(loader.load(), 600, 500);

        stage.setTitle("MSVM Studio - Registrazione");
        stage.setScene(scene);
    }

    private void mostraErrore(String msg) {
        if (lblErrore != null) {
            lblErrore.setText(msg);
            lblErrore.setVisible(true);
        }
    }
}