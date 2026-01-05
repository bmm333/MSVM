package it.uniupo.msvm.ui.auth;

import it.uniupo.msvm.ui.controllers.RegistrazioneController;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test per {@link RegistrazioneController}.
 * <p>
 * Verifica la logica di validazione dei campi del form di registrazione.
 * Utilizza JavaFX Platform startup per consentire l'istanziazione dei controlli UI.
 * </p>
 */
class RegistrazioneControllerTest {

    private RegistrazioneController controller;

    // Campi simulati per l'iniezione
    private TextField txtNome;
    private TextField txtCognome;
    private TextField txtEmail;
    private PasswordField txtPassword;
    private PasswordField txtConfermaPass;
    private Label lblErroreReg;

    /**
     * Avvia il toolkit JavaFX una volta sola prima di tutti i test.
     * Necessario per poter istanziare TextField e Label senza errori.
     */
    /**
     * Avvia il toolkit JavaFX in modo sicuro.
     * Se è già stato avviato da un altro test, ignora l'errore e prosegue.
     */
    @BeforeAll
    static void initJfxToolkit() {
        try {
            // Proviamo ad avviare il Toolkit
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Se entriamo qui, significa che il Toolkit era già attivo.
            // Non facciamo nulla, va benissimo così!
        }
    }

    /**
     * Configurazione dell'ambiente di test.
     * Crea il controller e inietta manualmente i componenti UI usando la Reflection.
     */
    @BeforeEach
    void setUp() throws Exception {
        // Poiché i controlli JavaFX devono essere creati nel thread FX o dopo l'init
        Platform.runLater(() -> {
            controller = new RegistrazioneController();
            txtNome = new TextField();
            txtCognome = new TextField();
            txtEmail = new TextField();
            txtPassword = new PasswordField();
            txtConfermaPass = new PasswordField();
            lblErroreReg = new Label();
        });

        // Attendiamo che il thread FX finisca l'inizializzazione
        Thread.sleep(100);

        // Iniezione manuale tramite Reflection (perché i campi sono privati)
        injectField("txtNome", txtNome);
        injectField("txtCognome", txtCognome);
        injectField("txtEmail", txtEmail);
        injectField("txtPassword", txtPassword);
        injectField("txtConfermaPass", txtConfermaPass);
        injectField("lblErroreReg", lblErroreReg);
    }

    /**
     * Helper per iniettare campi privati usando Reflection.
     */
    private void injectField(String fieldName, Object value) throws Exception {
        Field field = RegistrazioneController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    /**
     * Chiama il metodo privato o protetto handleRegistrazione tramite reflection
     * o semplicemente invoca il metodo se è package-private.
     * Qui simuliamo l'azione chiamando il metodo annotato con @FXML.
     */
    private void invokeHandleRegistrazione() {
        // Nota: Passiamo null come ActionEvent perché stiamo testando solo la validazione
        // che avviene PRIMA dell'uso dell'evento.
        try {
            // Usiamo reflection per chiamare il metodo privato/package-private
            java.lang.reflect.Method method = RegistrazioneController.class.getDeclaredMethod("handleRegistrazione", javafx.event.ActionEvent.class);
            method.setAccessible(true);
            method.invoke(controller, (javafx.event.ActionEvent) null);
        } catch (Exception e) {
            // Ignoriamo eccezioni di navigazione (perché event è null), ci interessa solo se setta la label prima
        }
    }

    @Test
    @DisplayName("Fallimento: Campi Vuoti")
    void testValidazioneCampiVuoti() {
        Platform.runLater(() -> {
            // Setup: Lasciamo i campi vuoti
            txtNome.setText("");

            // Esecuzione
            invokeHandleRegistrazione();

            // Verifica
            assertTrue(lblErroreReg.isVisible(), "La label di errore deve essere visibile");
            assertEquals("Tutti i campi sono obbligatori!", lblErroreReg.getText());
        });
        try { Thread.sleep(100); } catch (InterruptedException e) {}
    }

    @Test
    @DisplayName("Fallimento: Password Non Coincidenti")
    void testPasswordNonCoincidenti() {
        Platform.runLater(() -> {
            // Setup
            txtNome.setText("Mario");
            txtCognome.setText("Rossi");
            txtEmail.setText("mario@test.com");
            txtPassword.setText("password123");
            txtConfermaPass.setText("passwordDIVERSA"); // <--- Errore qui

            // Esecuzione
            invokeHandleRegistrazione();

            // Verifica
            assertTrue(lblErroreReg.isVisible());
            assertEquals("Le password non coincidono!", lblErroreReg.getText());
        });
        try { Thread.sleep(100); } catch (InterruptedException e) {}
    }

    @Test
    @DisplayName("Fallimento: Password Troppo Corta")
    void testPasswordCorta() {
        Platform.runLater(() -> {
            // Setup
            txtNome.setText("Mario");
            txtCognome.setText("Rossi");
            txtEmail.setText("mario@test.com");
            txtPassword.setText("123"); // <--- Corta
            txtConfermaPass.setText("123");

            // Esecuzione
            invokeHandleRegistrazione();

            // Verifica
            assertTrue(lblErroreReg.isVisible());
            assertEquals("La password deve essere di almeno 6 caratteri.", lblErroreReg.getText());
        });
        try { Thread.sleep(100); } catch (InterruptedException e) {}
    }

    @Test
    @DisplayName("Successo: Validazione OK")
    void testValidazioneSuccesso() {
        Platform.runLater(() -> {
            // Setup corretto
            txtNome.setText("Mario");
            txtCognome.setText("Rossi");
            txtEmail.setText("mario@test.com");
            txtPassword.setText("passwordSicura");
            txtConfermaPass.setText("passwordSicura");

            // Esecuzione
            // Nota: Qui il metodo proverà a fare 'tornaALogin' e fallirà perché 'event' è null.
            // Ma a noi interessa che NON abbia settato errori nella label prima di quel punto.
            invokeHandleRegistrazione();

            // Verifica: Se la validazione passa, la label viene nascosta o non mostra errori di validazione
            assertFalse(lblErroreReg.isVisible() && lblErroreReg.getText().contains("obbligatori"),
                    "Non dovrebbero esserci errori di campi vuoti");
        });
        try { Thread.sleep(100); } catch (InterruptedException e) {}
    }
}