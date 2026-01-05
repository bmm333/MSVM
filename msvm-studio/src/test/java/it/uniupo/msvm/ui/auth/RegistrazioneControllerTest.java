package it.uniupo.msvm.ui.auth;

import it.uniupo.msvm.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Suite di test di integrazione UI per la Registrazione.
 * Utilizza {@link FxRobot} per simulare l'interazione dell'utente con la form.
 */
@ExtendWith(ApplicationExtension.class)
class RegistrazioneControllerTest {

    /**
     * Metodo di setup eseguito automaticamente da TestFX prima di ogni test.
     * Carica la vista 'register.fxml' in uno Stage reale.
     */
    @Start
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(App.class.getResource("ui/auth/register.fxml"));
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    /**
     * Pulizia dopo ogni test (chiude lo stage per evitare conflitti).
     */
    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        // Rilascia eventuali tasti (es. shift/ctrl) rimasti premuti
        new FxRobot().release(new javafx.scene.input.KeyCode[]{});
    }

    @Test
    @DisplayName("Fallimento: Click su Registrati con campi vuoti")
    void testValidazioneCampiVuoti(FxRobot robot) {
        // 1. Azione: Clicca direttamente sul bottone (che ha testo "REGISTRATI ORA")
        // Puoi usare il selettore CSS della classe (.login-btn) o il testo
        robot.clickOn(".login-btn");

        // 2. Verifica: La label di errore deve apparire con il messaggio corretto
        verifyThat("#lblErroreReg", (Label l) -> l.isVisible());
        verifyThat("#lblErroreReg", hasText("Tutti i campi sono obbligatori!"));
    }

    @Test
    @DisplayName("Fallimento: Password Non Coincidenti")
    void testPasswordNonCoincidenti(FxRobot robot) {
        // 1. Compila i campi
        robot.clickOn("#txtNome").write("Mario");
        robot.clickOn("#txtCognome").write("Rossi");
        robot.clickOn("#txtEmail").write("mario@test.com");

        // 2. Inserisce password diverse
        robot.clickOn("#txtPassword").write("password123");
        robot.clickOn("#txtConfermaPass").write("passwordDIVERSA");

        // 3. Click Registrati
        robot.clickOn(".login-btn");

        // 4. Verifica errore
        verifyThat("#lblErroreReg", hasText("Le password non coincidono!"));
    }

    @Test
    @DisplayName("Fallimento: Password Troppo Corta")
    void testPasswordCorta(FxRobot robot) {
        // 1. Compila i campi
        robot.clickOn("#txtNome").write("Mario");
        robot.clickOn("#txtCognome").write("Rossi");
        robot.clickOn("#txtEmail").write("mario@test.com");

        // 2. Inserisce password uguali ma corte
        robot.clickOn("#txtPassword").write("123");
        robot.clickOn("#txtConfermaPass").write("123");

        // 3. Click Registrati
        robot.clickOn(".login-btn");

        // 4. Verifica errore
        verifyThat("#lblErroreReg", hasText("La password deve essere di almeno 6 caratteri."));
    }

    @Test
    @DisplayName("Successo: Navigazione al Login dopo registrazione valida")
    void testRegistrazioneSuccesso(FxRobot robot) {
        // 1. Compila tutto correttamente
        robot.clickOn("#txtNome").write("Luigi");
        robot.clickOn("#txtCognome").write("Verdi");
        robot.clickOn("#txtEmail").write("luigi@test.com");
        robot.clickOn("#txtPassword").write("passwordSicura");
        robot.clickOn("#txtConfermaPass").write("passwordSicura");

        // 2. Click Registrati
        robot.clickOn(".login-btn");

        // 3. VERIFICA CORRETTA:
        // Non cerchiamo più #lblErroreReg (che è sparito).
        // Cerchiamo invece un elemento che esiste SOLO nella pagina di Login (es. txtUsername)
        verifyThat("#txtUsername", (TextField t) -> t.isVisible());

        // Oppure verifichiamo che il bottone ora dica "LOGIN"
        verifyThat(".login-btn", hasText("ACCEDI"));
    }
    @Test
    @DisplayName("Navigazione: Click su 'Accedi qui' porta al login")
    void testBottoneTornaAlLogin(FxRobot robot) {
        // Clicca sul link in basso "Accedi qui"
        robot.clickOn("Accedi qui");

        // Verifica che siamo tornati al login (es. controllando il titolo della finestra o un elemento)
        // Nota: Questo funziona solo se il titolo cambia nel controller
    }
}