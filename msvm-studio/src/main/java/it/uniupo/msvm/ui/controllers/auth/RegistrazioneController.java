package it.uniupo.msvm.ui.controllers.auth;

import it.uniupo.msvm.App;
import it.uniupo.msvm.common.dto.FullProfileDTO;
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

public class RegistrazioneController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCognome;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfermaPass;
    @FXML private Label lblErroreReg;

    @FXML
    void handleRegistrazione(ActionEvent event) {
        if (lblErroreReg != null) lblErroreReg.setVisible(false);

        String nome = txtNome.getText();
        String cognome = txtCognome.getText();
        String email = txtEmail.getText();
        String pass = txtPassword.getText();
        String confPass = txtConfermaPass.getText();

        // 1. Validazioni Locali
        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            mostraErrore("Tutti i campi sono obbligatori!");
            return;
        }
        if (!pass.equals(confPass)) {
            mostraErrore("Le password non coincidono!");
            return;
        }
        if (pass.length() < 8) {
            mostraErrore("Password troppo corta (min 8 caratteri).");
            return;
        }

        try {
            if (App.server == null) {
                mostraErrore("Impossibile contattare il server.");
                return;
            }

            // 2. Registrazione Account (Crea User)
            // Nota: Usiamo email anche come username per semplicità
            UserDTO newUser = App.server.register(email, email, pass);

            // 3. Aggiornamento Profilo (Salva Nome e Cognome)
            FullProfileDTO profile = new FullProfileDTO();
            profile.setUserId(newUser.getId());
            profile.setFirstName(nome);
            profile.setLastName(cognome);
            profile.setEmail(email);
            profile.setBio("Nuovo utente MSVM");
            profile.setPhoneNumber("");

            boolean updateOk = App.server.updateProfile(profile);

            if (updateOk) {
                System.out.println("[Client] Registrazione completata: " + email);
                // 4. Torna al Login per permettere l'accesso
                tornaALogin(event);
            } else {
                mostraErrore("Account creato, ma errore nel salvataggio profilo.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Mostra un messaggio user-friendly se possibile (es. "Email già in uso")
            mostraErrore("Errore: " + e.getMessage());
        }
    }

    @FXML
    void tornaALogin(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // Assicurati che il percorso del FXML sia corretto
        FXMLLoader loader = new FXMLLoader(App.class.getResource("ui/auth/login.fxml"));
        Scene scene = new Scene(loader.load(), 600, 500);

        stage.setTitle("MSVM Studio - Login");
        stage.setScene(scene);
    }

    private void mostraErrore(String msg) {
        if (lblErroreReg != null) {
            lblErroreReg.setText(msg);
            lblErroreReg.setVisible(true);
        }
    }
}