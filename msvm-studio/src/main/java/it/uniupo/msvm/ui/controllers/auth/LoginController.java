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

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword; // PasswordField, non TextField!

    @FXML
    private Label lblErrore;

    @FXML
    public void handleLogin(){
        System.out.println("Login Premuto");

        String user=(txtUsername != null) ? txtUsername.getText():"";
        String password=(txtPassword != null) ?txtPassword.getText():"";

        if(user.equals("admin") && password.equals("admin")){
            System.out.println("Login Riuscito!");
            if(lblErrore != null) lblErrore.setVisible(false);
        }
        else{
            System.out.println("Login Fallito");
            if(lblErrore != null){
                lblErrore.setText("Utente o password errati");
                lblErrore.setVisible(true);
            }

        }

    }

    @FXML
    public void handleRegistrazione(ActionEvent event) throws IOException {
        System.out.println("Premuto registrazione");

        // 1. Recuperiamo la finestra (Stage) attuale dal bottone che è stato cliccato
        // Questo è il trucco per non aprire una seconda finestra!
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // 2. Carichiamo il file FXML della REGISTRAZIONE
        // (Nel tuo codice avevi rimesso 'login.fxml', occhio!)
        FXMLLoader loader = new FXMLLoader(App.class.getResource("ui/auth/register.fxml"));

        // Carica il contenuto
        Parent root = loader.load();

        // 3. Creiamo la nuova scena
        Scene scene = new Scene(root, 600, 500);

        // 4. Impostiamo la nuova scena sulla STESSA finestra
        stage.setTitle("MSVM Studio - Registrazione");
        stage.setScene(scene);
        stage.show();
    }
}
