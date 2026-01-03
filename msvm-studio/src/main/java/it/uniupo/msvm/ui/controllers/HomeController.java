package it.uniupo.msvm.ui.controllers;

import it.uniupo.msvm.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

import java.awt.*;
import java.io.IOException;

public class HomeController {


    @FXML
    public void initialize(){
        System.out.println("Homepage Initialization");
    }

    /**
     * Apre l'editor della nostra applicazione.
     * Attualmente funge da placeholder per funzionalità future.
     */
    @FXML
    public void handleCreaFile(){
        System.out.println("Click su Crea File");
        // TODO: Aprire la vista Editor vuota
    }

    /**
     * Gestisce l'evento di click sul pulsante "Apri File".
     * Apre una finestra di dialogo nativa del sistema operativo (FileChooser)
     * permettendo all'utente di selezionare esclusivamente file con estensione .msvm.
     * <p>
     * Se un file viene selezionato, il suo percorso assoluto viene stampato in console.
     * </p>
     *
     * @param event L'evento di azione generato dal click (usato per recuperare lo Stage corrente).
     */
    @FXML
    public void handleApriFile(ActionEvent event) {
        System.out.println("Click su Apri File");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona file da caricare .msvm");

        // Filtro per accettare solo file specifici MSVM
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("file msvm", "*.msvm")
        );

        // Recupera la finestra (Stage) attuale dal bottone che ha scatenato l'evento
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);

        if(file != null){
            System.out.println("File Scelto: " + file.getAbsoluteFile());
            // TODO: Aggiungere qui la logica di parsing o elaborazione del file
        }
        else{
            System.out.println("Selezione annullata");
        }
    }

    /**
     * Gestisce la navigazione verso la sezione "Esplora".
     * Attualmente funge da placeholder per funzionalità future.
     */
    public void handleEsplora(){
        System.out.println("Click su Esplora");
    }

    /**
     * Gestisce l'apertura della finestra di Login.
     * Carica la vista FXML situata in 'ui/auth/login.fxml' e la mostra in una nuova finestra (Stage).
     *
     * @throws IOException Se il file FXML specificato non viene trovato o non può essere caricato.
     */
    @FXML
    public void handleLogin() throws IOException {
        Stage stage = new Stage();
        System.out.println("Click su Profilo Utente!");

        // Caricamento del loader per la vista di Login
        FXMLLoader login = new FXMLLoader(App.class.getResource("ui/auth/login.fxml"));

        // Creazione della scena con dimensioni fisse
        Scene scene = new Scene(login.load(), 600, 500);

        stage.setTitle("MSVM Studio");
        stage.setScene(scene);
        stage.show();
    }
}
