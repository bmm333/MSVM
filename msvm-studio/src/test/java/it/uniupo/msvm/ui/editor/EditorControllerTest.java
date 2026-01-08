package it.uniupo.msvm.ui.editor;

import it.uniupo.msvm.App;
import it.uniupo.msvm.core.runtime.VmStateSnapshot;
import it.uniupo.msvm.ui.controllers.editor.EditorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

/**
 * Suite di test UI e Unitari per {@link EditorController}.
 * <p>
 * Questa classe utilizza <b>TestFX</b> per verificare sia il comportamento dell'interfaccia grafica
 * (simulazione click e digitazione) sia la logica interna del controller (inizializzazione dati,
 * gestione stack e memoria).
 * </p>
 * <p>
 * Obiettivo: Raggiungere il <b>100% di Code Coverage</b> testando anche i metodi pubblici
 * non direttamente scatenati da eventi mouse.
 * </p>
 */
@ExtendWith(ApplicationExtension.class)
@DisplayName("Test Completo Editor Controller (Coverage 100%)")
public class EditorControllerTest {

    /** Riferimento diretto al controller per invocare metodi logici bypassando la GUI. */
    private EditorController controller;

    /**
     * Configurazione iniziale dell'ambiente di test (Fixture Setup).
     * <p>
     * Carica il file FXML {@code EditorWindow.fxml}, recupera l'istanza del controller
     * per i test diretti e mostra lo stage principale.
     * </p>
     *
     * @param stage Lo stage primario fornito da TestFX.
     * @throws IOException Se il file FXML non viene trovato o non può essere caricato.
     */
    @Start
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("ui/editor/EditorWindow.fxml"));
        Parent root = loader.load();

        // Recuperiamo il controller dal loader per poter testare i suoi metodi pubblici
        this.controller = loader.getController();

        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    /**
     * Pulizia dell'ambiente dopo ogni test (Fixture Teardown).
     * <p>
     * Nasconde lo stage e rilascia eventuali tasti o bottoni del mouse rimasti premuti
     * dal Robot, garantendo l'isolamento tra i test.
     * </p>
     *
     * @throws Exception Se si verificano errori durante la chiusura del toolkit JavaFX.
     */
    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        new FxRobot().release(new KeyCode[]{});
    }

    // =================================================================================
    // SEZIONE 1: TEST INTERAZIONE UTENTE (GUI)
    // =================================================================================

    /**
     * Verifica che tutti i componenti grafici principali siano visibili all'avvio.
     * <p>
     * Controlla la presenza dei bottoni della Toolbar (Run, Step, Stop), dell'Editor di codice,
     * della Console di output e della Tabella della memoria.
     * </p>
     *
     * @param robot Il bot TestFX per l'ispezione del grafo della scena.
     */
    @Test
    @DisplayName("Should display all main UI components")
    void shouldDisplayMainComponents(FxRobot robot) {
        verifyThat("#btnRun", isVisible());
        verifyThat("#btnStep", isVisible());
        verifyThat("#btnReset", isVisible());
        verifyThat("#codeEditor", isVisible());
        verifyThat("#consoleArea", isVisible());
        verifyThat("#memoryTable", isVisible());
    }

    /**
     * Verifica la capacità dell'utente di scrivere codice nell'editor.
     * <p>
     * Simula il focus sull'area di testo, la cancellazione del contenuto di default
     * e la digitazione di un'istruzione Assembly. Controlla poi che il testo sia stato recepito.
     * </p>
     *
     * @param robot Il bot TestFX per simulare tastiera e mouse.
     */
    @Test
    @DisplayName("Should allow typing code in the editor")
    void shouldAllowTypingCode(FxRobot robot) {
        robot.clickOn("#codeEditor");
        // Ctrl+A -> Backspace per pulire
        robot.push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE);

        robot.write("PUSH 99");

        TextArea editor = robot.lookup("#codeEditor").queryAs(TextArea.class);
        assertTrue(editor.getText().contains("PUSH 99"), "L'editor dovrebbe contenere il testo digitato dal robot");
    }

    /**
     * Test del pulsante "Run".
     * <p>
     * Simula il click sul bottone e verifica che la console mostri il messaggio di log atteso.
     * </p>
     */
    @Test
    @DisplayName("Run button logs to console")
    void testRunButtonInteraction(FxRobot robot) {
        robot.clickOn("#btnRun");
        TextArea console = robot.lookup("#consoleArea").queryAs(TextArea.class);
        assertTrue(console.getText().contains("Running") || console.getText().contains("Compiling"),
                "Premendo Run, la console deve mostrare il log di avvio (Compiling/Running)");
    }

    /**
     * Test del pulsante "Step".
     * <p>
     * Simula il click sul bottone Step e verifica il feedback in console.
     * </p>
     */
    @Test
    @DisplayName("Step button interaction (Silent success)")
    void testStepButtonInteraction(FxRobot robot) {
        robot.clickOn("#btnStep");
        TextArea console = robot.lookup("#consoleArea").queryAs(TextArea.class);
        assertFalse(console.getText().contains("Step Error"), "Il pulsante Step non deve generare errori bloccanti");
    }

    /**
     * Test del pulsante "Reset" (Stop).
     * <p>
     * Simula il click sul bottone Stop e verifica il feedback in console.
     * </p>
     */
    @Test
    @DisplayName("Reset button logs to console")
    void testResetButtonInteraction(FxRobot robot) {
        robot.clickOn("#btnReset");
        TextArea console = robot.lookup("#consoleArea").queryAs(TextArea.class);
        assertTrue(console.getText().contains("VM Reset"), "Premendo Stop, la console deve mostrare 'VM Reset'");
    }

    // =================================================================================
    // SEZIONE 2: TEST LOGICI E DI DATI (WHITE-BOX TESTING)
    // =================================================================================

    /**
     * Verifica il funzionamento del metodo pubblico {@link EditorController#setCode(String)}.
     * <p>
     * Questo test è fondamentale per coprire lo scenario in cui il codice viene caricato
     * programmaticamente (es. aprendo un file), bypassando l'input manuale.
     * </p>
     *
     * @param robot Il bot TestFX utilizzato per interagire con il thread JavaFX.
     */
    @Test
    @DisplayName("setCode() method should update the Editor TextArea")
    void testSetCodeProgrammatically(FxRobot robot) {
        String sampleCode = "MOV AX, 10\nADD AX, 5";

        robot.interact(() -> {
            controller.setCode(sampleCode);
        });

        TextArea editor = robot.lookup("#codeEditor").queryAs(TextArea.class);
        assertEquals(sampleCode, editor.getText(),
                "Il metodo setCode non ha aggiornato correttamente il contenuto della TextArea");
    }

    /**
     * Verifica l'inizializzazione corretta delle strutture dati visive (Memoria e Stack).
     * <p>
     * Questo test copre:
     * <ul>
     * <li>Il metodo {@code initialize()} del controller.</li>
     * <li>Il metodo privato {@code initFakeData()}.</li>
     * <li>Il metodo privato {@code updateVisualStack()}.</li>
     * <li>La classe interna {@code MemoryRow} e i suoi getter.</li>
     * </ul>
     * Controlla che la Tabella sia popolata e che lo Stack visivo contenga gli elementi iniziali.
     * </p>
     */
    @Test
    @DisplayName("UI Binding: Memory and Stack update on VmSnapshot")
    void testMemoryAndStackBinding(FxRobot robot) {
        List<Integer> stackData = List.of(10, 55, 1024);
        int[] memoryData = new int[4096];
        memoryData[0] = 999;
        VmStateSnapshot snapshot = new VmStateSnapshot(
                4,
                false,
                stackData,
                memoryData,
                0
        );
        robot.interact(() -> controller.onVmUpdate(snapshot));
        TableView<?> table = robot.lookup("#memoryTable").queryTableView();
        assertFalse(table.getItems().isEmpty(), "La tabella della memoria dovrebbe popolarsi dopo un update");
        Object firstItem = table.getItems().get(0);
        assertTrue(firstItem instanceof EditorController.MemoryRow, "L'item nella tabella deve essere istanza di MemoryRow");
        EditorController.MemoryRow row = (EditorController.MemoryRow) firstItem;
        assertEquals("0x0000", row.getAddress());
        assertEquals("999", row.getValue());
        VBox stackContainer = robot.lookup("#visualStackContainer").queryAs(VBox.class);
        assertEquals(3, stackContainer.getChildren().size(), "Lo stack visivo dovrebbe aggiornarsi con 3 elementi");
    }
}