package it.uniupo.msvm.ui.homepage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
@DisplayName("Test UI: Home Controller & Interazioni")
@EnabledOnOs({OS.WINDOWS, OS.MAC, OS.LINUX})
class HomeControllerTest{

    // Questi oggetti servono per "catturare" le scritte fatte con System.out.println
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    /**
     * Prima di ogni test, ridirezioniamo la console per leggere cosa stampa.
     */
    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    /**
     * Alla fine di ogni test, rimettiamo la console normale.
     */
    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    /**
     * Avvia la GUI caricando il file FXML vero.
     */
    @Start
    private void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/uniupo/msvm/ui/homepage/home.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    @Test
    @DisplayName("Verifica Inizializzazione (UI Caricata)")
    void testInitialize(FxRobot robot) {

        assertTrue(robot.lookup("#btnCreaFile").tryQuery().isPresent(),
                "Il bottone Crea File dovrebbe essere presente se l'initialize ha funzionato");

        assertTrue(robot.lookup("#btnCreaFile").queryButton().isVisible(),
                "Il bottone dovrebbe essere visibile a schermo");
    }
    @Test
    @DisplayName("Click su 'Crea File'")
    void testHandleCreaFile(FxRobot robot) {
        robot.clickOn("#btnCreaFile");

        assertTrue(outContent.toString().contains("Click su Crea File"),
                "Doveva stampare 'Click su Crea File'");
    }

    @Test
    @DisplayName("Click su 'Apri File'")
    void testHandleApriFile(FxRobot robot) {
        // Usa l'ID
        robot.clickOn("#btnApriFile");

        assertTrue(outContent.toString().contains("Click su Apri File"),
                "Doveva stampare 'Click su Apri File'");
    }

    @Test
    @DisplayName("Click su Profilo (Login)")
    void testHandleLogin(FxRobot robot) {

        robot.clickOn(".profile-button");

        assertTrue(outContent.toString().contains("Click su Profilo Utente!"),
                "Doveva stampare 'Click su Profilo Utente!'");
    }

    @Test
    @DisplayName("Click sul bottone Cloud")
    void TestHandleEsplora(FxRobot robot){
        robot.clickOn("#btnEsplora");


        assertTrue(outContent.toString().contains("Click su Esplora"),"Doveva stampare il messaggio 'Click su Esplora' ");
    }
}