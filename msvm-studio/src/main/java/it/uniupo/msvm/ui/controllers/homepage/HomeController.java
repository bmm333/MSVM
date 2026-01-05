package it.uniupo.msvm.ui.controllers.homepage;

import it.uniupo.msvm.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

import java.io.IOException;

/**
 * Controller principale per la gestione della schermata Home (Dashboard) di MSVM Studio.
 * <p>
 * Questa classe agisce come hub centrale dell'applicazione, permettendo all'utente di:
 * <ul>
 * <li>Creare nuovi progetti/file.</li>
 * <li>Aprire progetti esistenti dal file system locale.</li>
 * <li>Esplorare progetti della community (funzionalità futura).</li>
 * <li>Accedere all'area utente (Login/Registrazione).</li>
 * </ul>
 * </p>
 *
 * <h3>Elenco dei Metodi Principali:</h3>
 * <ul>
 * <li>{@link #handleCreaFile(ActionEvent)} : Avvia la procedura di creazione nuovo file.</li>
 * <li>{@link #handleApriFile(ActionEvent)} : Apre il selettore di file di sistema.</li>
 * <li>{@link #handleEsplora()} : Naviga alla sezione community.</li>
 * <li>{@link #handleLogin()} : Apre la finestra di autenticazione.</li>
 * </ul>
 *
 * @author Luca Lupi
 * @version 1.0
 */
public class HomeController {

    /**
     * Metodo di inizializzazione automatica del controller.
     * <p>
     * Viene invocato automaticamente da JavaFX dopo che il file FXML è stato caricato.
     * Può essere utilizzato per configurare lo stato iniziale della UI (es. caricare la lista dei file recenti).
     * </p>
     */
    @FXML
    public void initialize(){
        System.out.println("Homepage Initialization");
    }

    /**
     * Gestisce l'azione di creazione di un nuovo file.
     * <p>
     * Attualmente stampato un log in console. In futuro, questo metodo si occuperà
     * di cambiare la scena verso l'Editor di codice vuoto.
     * </p>
     *
     * @param event L'evento di click sul pulsante "Crea File".
     */
    @FXML
    public void handleCreaFile(ActionEvent event){
        System.out.println("Click su Crea File");
        //Todo: aspettare l'editor per fare il crea file
    }

    /**
     * Gestisce l'evento di click sul pulsante "Apri File".
     * <p>
     * Apre una finestra di dialogo nativa del sistema operativo ({@link FileChooser})
     * permettendo all'utente di selezionare esclusivamente file con estensione {@code .msvm}.
     * </p>
     * <p>
     * Se un file viene selezionato correttamente, il suo percorso assoluto viene
     * recuperato per le successive operazioni di parsing e caricamento nell'editor.
     * </p>
     *
     * @param event L'evento di azione generato dal click (necessario per recuperare lo {@link Stage} genitore).
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
     * <p>
     * Questa funzionalità è destinata alla visualizzazione di progetti condivisi dalla community.
     * Attualmente funge da placeholder.
     * </p>
     */
    @FXML
    public void handleEsplora(){
        System.out.println("Click su Esplora");
        //TODO: Crea un database per i server per i progetti della community
    }

    /**
     * Gestisce l'apertura della finestra di Login/Autenticazione.
     * <p>
     * Carica la vista definita in {@code ui/auth/login.fxml} e la visualizza in un nuovo Stage
     * (o in quello corrente, a seconda dell'implementazione desiderata).
     * </p>
     *
     * @throws IOException Se il file FXML 'ui/auth/login.fxml' non viene trovato nel classpath o non può essere caricato.
     */
    @FXML
    public void handleLogin() throws IOException {
        Stage stage = new Stage();
        System.out.println("Click su Profilo Utente!");

        // Caricamento del loader per la vista di Login
        // Assicurati che App.java sia nella root o aggiungi "/" prima del path se necessario (es. "/ui/auth/login.fxml")
        FXMLLoader login = new FXMLLoader(App.class.getResource("ui/auth/login.fxml"));

        // Creazione della scena con dimensioni fisse
        Scene scene = new Scene(login.load(), 600, 500);

        stage.setTitle("MSVM Studio");
        stage.setScene(scene);
        stage.show();
    }
}