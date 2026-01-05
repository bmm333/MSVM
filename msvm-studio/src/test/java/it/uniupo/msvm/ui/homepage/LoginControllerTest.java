package it.uniupo.msvm.ui.homepage;

import it.uniupo.msvm.App; // Assicurati di importare la tua Main App
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test di Integrazione UI per il Login.
 * Usa TestFX (FXRobot) per simulare le azioni dell'utente.
 */
@ExtendWith(ApplicationExtension.class) // <--- FONDAMENTALE: Collega JUnit 5 a JavaFX
class LoginControllerTest {

    /**
     * Questo metodo parte AUTOMATICAMENTE prima di ogni test.
     * Serve a caricare la tua interfaccia grafica (Scene e Stage).
     */
    @Start
    public void start(Stage stage) throws Exception {
        // Carica il file FXML vero, così testi l'app reale!
        FXMLLoader loader = new FXMLLoader(App.class.getResource("ui/auth/login.fxml"));
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront(); // Porta la finestra davanti a tutto
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Chiude tutto dopo il test e pulisce la memoria
        FxToolkit.hideStage();
        // Rilascia i tasti eventualmente rimasti premuti
        // (utile se il test fallisce mentre premeva SHIFT o CTRL)
    }

    @Test
    @DisplayName("Test: L'interfaccia si avvia correttamente")
    void testInterfacciaCaricata(FxRobot robot) {
        // Verifica che ci sia un bottone con il testo "LOGIN" (o l'ID del bottone)
        // Nota: Assicurati che nel CSS/FXML il bottone abbia del testo visibile
        verifyThat(".login-btn", hasText("ACCEDI"));
    }

    @Test
    @DisplayName("Test: Login con credenziali corrette")
    void testLoginSuccesso(FxRobot robot) {
        // 1. Clicca sulla casella username (usa l'ID CSS #txtUsername)
        robot.clickOn("#txtUsername");
        // 2. Scrive "admin"
        robot.write("admin");

        // 3. Clicca sulla password
        robot.clickOn("#txtPassword");
        robot.write("admin");

        // 4. Clicca sul bottone Login (cerca per testo o per ID #btnLogin)
        // Se nel CSS hai messo la classe .login-btn, puoi usare quella
        robot.clickOn(".login-btn");

        // 5. VERIFICA: Cosa deve succedere dopo?
        // Esempio: Se il login va a buon fine, la finestra login dovrebbe chiudersi o cambiare.
        // Qui controlliamo solo che NON ci siano messaggi di errore rossi visibili
        // verifyThat("#lblErrore", (Label l) -> !l.isVisible());
    }

    @Test
    @DisplayName("Test: Login fallito mostra errore")
    void testLoginFallito(FxRobot robot) {
        // Scrive dati sbagliati
        robot.clickOn("#txtUsername").write("utenteSbagliato");
        robot.clickOn("#txtPassword").write("passwordErrata");

        // Clicca Login
        robot.clickOn(".login-btn");

        // Verifica che appaia il messaggio di errore
        // "lblErrore" è l'fx:id della Label rossa nel tuo FXML
        verifyThat("#lblErrore", (javafx.scene.control.Label l) -> l.isVisible());
        verifyThat("#lblErrore", hasText("Utente o password errati")); // O il testo che hai impostato
    }
}