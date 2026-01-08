package it.uniupo.msvm.ui.controllers.editor;

import it.uniupo.msvm.core.MsvmBootstrapper;
import it.uniupo.msvm.core.MsvmService;
import it.uniupo.msvm.core.runtime.VmListener;
import it.uniupo.msvm.core.runtime.VmStateSnapshot;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;


/**
 * Controller principale per la finestra di editing e simulazione
 * <p>
 *     Questa classe gestisce l'interazione tra l'utente, leditor di codice Assembly e la visualizzazine
 *     in tempo reale dello stato della macchina virtuale. Implementa il pattern MVC per aggiornare
 *     dinamicamente le viste di:
 *     <ul>
 *         <li><b>Stack degli Operandi:</b> Visualizzazione a blochi stile "Tower" con highlight del TOS.</li>
 *         <li><b>Memoria RAM (Tabellare):</b> Vista dettagliata indirizzo-valore.</li>
 *         <li><b>Memoria RAM (Visiva):</b> Mappa ad alta densità (Heatmap) per visualizzare accessi e allocazioni.</li>
 *     </ul>
 * </p>
 * @author Arben Mema
 * @version 1.2(high density UI update)
 * */
public class EditorController  implements VmListener {
  //Componenti FXML Editor e console
    /**Area di testo per la scrittura del codice sorgente Assembly*/
    @FXML private TextArea codeEditor;
    /**Area di output per i messagi di sistema e log della VM*/
    @FXML private TextArea consoleArea;
    /**Label per visualizzare il valore corrente dell'Instruction Pointer(IP)*/
    @FXML private Label lblIp;
    /**Label per visualizzare la dim attuale dello Stack*/
    @FXML private Label lblStackSize;
    /**Contenitore verticale (VBOX) per la rappresentazione grafica dello stack
     * Gli elementi sono aggiunti dinamicamente come {@link HBox} stilizzati */
    @FXML private VBox visualStackContainer;
    /** Tabella per la visualizzazione dettagliata dei valori in memoria. */
    @FXML private TableView<MemoryRow> memoryTable;
    @FXML private TableColumn<MemoryRow, String> colAddress;
    @FXML private TableColumn<MemoryRow, String> colValue;
    /** * Pannello a griglia (TilePane) per la visualizzazione "High Density" della memoria.
     * Contiene array di {@link Rectangle} che cambiano colore in base allo stato della cella.
     */
    @FXML private TilePane memoryGrid;

    //Core
    private MsvmService service;

    /** Array di supporto per l'accesso rapido O(1) alle celle grafiche della griglia. */
    private Rectangle[] visualCells;
    /** Dimensione totale della memoria simulata (celle). */
    private final int MEM_SIZE = 4096;
    /** Modello dati osservabile per la Tabella della memoria. */
    private ObservableList<MemoryRow> memoryData = FXCollections.observableArrayList();

    /**
     * Inizializza il controller dopo che l'elemento radice è stato elaborato completamente.
     * <p>
     * Configura le factory delle celle per la tabella, inizializza la griglia grafica
     * e carica dati fittizi per il testing dell'interfaccia.
     * </p>
     */
    @FXML
    public void initialize() {
        // Configurazione delle colonne
        colAddress.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        colValue.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        memoryTable.setItems(memoryData);

        if(memoryGrid!=null){
            initVisualGrid();
        }
        try{
            this.service= MsvmBootstrapper.buildProductionInstance();
            this.service.addListener(this);
            logToConsole("System: Core Connected succesfully");
            logToConsole("System: Ready");
        }catch (Exception e)
        {
            logToConsole("Critical ERROR: Could not Start MSVM Core"+ e.getMessage());
        }
    }

    /**
     * Inizializza la Griglia Visiva della memoria (Memory Map).
     * <p>
     * Crea {@value @MEM_SIZE} celle grafiche (Rettangoli) ottimizzate per un layout ad alta densità.
     * Ogni cella è dotata di Tooltip per l'ispezione dell'indirizzo.
     * </p>
     */
    private void initVisualGrid() {
        visualCells = new Rectangle[MEM_SIZE];
        memoryGrid.getChildren().clear();
        memoryGrid.setHgap(2);
        memoryGrid.setVgap(2);

        for (int i = 0; i < MEM_SIZE; i++) {
            Rectangle cell = new Rectangle(10, 10);
            cell.setFill(Color.web("#333333"));
            Tooltip.install(cell, new Tooltip("Addr: " + i));
            visualCells[i] = cell;
            memoryGrid.getChildren().add(cell);
        }
    }

    private void resetGridColor() {
        if(visualCells == null) return;
        for(Rectangle r : visualCells) r.setFill(Color.web("#333333"));
    }

    private void logToConsole(String msg) {
        consoleArea.appendText("\n> " + msg);
        consoleArea.setScrollTop(Double.MAX_VALUE); // Auto-scroll
    }

    /**
     * Popola l'interfaccia con dati fittizi per scopi di debug e design review.
     * <p>
     * Simula lo stato della RAM, dello Stack e colora alcune celle della griglia
     * per verificare il corretto funzionamento del CSS.
     * </p>
     */
    private void initFakeData() {
        // Simuliamo Ram
        for (int i = 0; i < 10; i++) {
            memoryData.add(new MemoryRow(String.format("0x%04X", i), "0000"));
        }
        // Simuliamo stack
        updateVisualStack(java.util.List.of(10, 55, 1024));
        lblIp.setText("0004");
        consoleArea.setText("MSVM Initialized ready to rock.");
        if(visualCells!=null && visualCells.length>0){
            visualCells[0].setFill(Color.CYAN); //simulation
        }
    }
    /** Gestisce il click sul pulsante "Run". Avvia l'esecuzione continua. */
    @FXML public void onRun() {
        try{
            logToConsole("Compiling & Loading...");
            //Carica e compila ( Passa per Preprocessor -> Assembler -> RAM)
            service.loadCode(codeEditor.getText());
            //Esegui
            logToConsole("Running...");
            service.run();

        }catch (Exception e)
        {
            logToConsole("Error: "+ e.getMessage());
            e.printStackTrace();
        }
    }
    /** Gestisce il click sul pulsante "Step". Esegue una singola istruzione. */
    @FXML public void onStep() {
        try{
            //Check se la vm non e carica proviamo a caricare il codice corrente
            if(lblIp.getText().equals("0000")||lblIp.getText().equals("N/A")){
                service.loadCode(codeEditor.getText());
            }
            service.step();
        }catch (Exception e)
        {
            logToConsole("Step Error: "+ e.getMessage());
        }
    }
    /** Gestisce il click sul pulsante "Reset". Ripristina lo stato della VM. */
    @FXML public void onReset() {
        try{
            service.stop(); //ferma
            //pulisce ui
            updateVisualStack(new ArrayList<>());
            memoryData.clear();
            resetGridColor();
            lblIp.setText("0000");
            logToConsole("VM Reset.");
        }catch (Exception e)
        {
            logToConsole("Reset Error: "+ e.getMessage());
        }
    }
    /**
     * Imposta il contenuto dell'editor di codice.
     * <p>
     * Utilizzato dai controller esterni (es. {@code HomeController}) per iniettare
     * il codice sorgente quando si apre un file dal file system.
     * </p>
     *
     * @param content Il codice sorgente Assembly sotto forma di stringa.
     */
    public void setCode(String content) {
        if(content!=null){
            this.codeEditor.setText(content);
        }
    }

    /**
     * Chiamato ogni volta che la VM completa un ciclo (step).
     *
     * @param snapshot La foto dello stato attuale (safe per la UI).
     */
    @Override
    public void onVmUpdate(VmStateSnapshot snapshot) {
        Platform.runLater(()->{
            lblIp.setText(String.format("%04d",snapshot.ip()));
            lblStackSize.setText(String.valueOf(snapshot.stack().size()));
            updateVisualStack(snapshot.stack());
            updateMemoryView(snapshot.memory(),snapshot.ip());
        });
    }

    @Override
    public void onVmHalt() {
        Platform.runLater(() -> {
            logToConsole("=== SYSTEM HALTED ===");
            logToConsole("Program execution finished successfully.");

            lblIp.setText("END");
            lblIp.setStyle("-fx-text-fill: red;");
        });
    }
    /**
     * Chiamato se la VM crasha o incontra un errore critico.
     *
     * @param message
     */
    @Override
    public void onVmError(String message) {
        Platform.runLater(() -> logToConsole("RUNTIME EXCEPTION: " + message));
    }

    private void updateMemoryView(int[] memory, int currentIp) {
        // A. Aggiornamento Tabella (Mostriamo solo celle non zero per performance?)
        // Per ora facciamo un refresh completo delle prime 32 celle + quelle sporche
        // (Ottimizzazione futura: ObservableMap)
        memoryData.clear();
        for (int i = 0; i < memory.length; i++) {
            if (memory[i] != 0 || i < 16) {
                memoryData.add(new MemoryRow(
                        String.format("0x%04X", i),
                        String.valueOf(memory[i]))
                );
            }
        }
        if (visualCells != null) {
            for (int i = 0; i < Math.min(memory.length, visualCells.length); i++) {
                Rectangle cell = visualCells[i];
                if (i == currentIp) {
                    cell.setFill(Color.ORANGE);
                } else if (memory[i] != 0) {
                    cell.setFill(Color.CYAN);
                } else {
                    cell.setFill(Color.web("#333333"));
                }
            }
        }
    }
    private void updateVisualStack(List<Integer> stackValues) {
        visualStackContainer.getChildren().clear();
        if (stackValues == null || stackValues.isEmpty()) {
            Label emptyLabel = new Label("Stack Empty");
            emptyLabel.setStyle("-fx-text-fill: #666; -fx-padding: 10;");
            visualStackContainer.getChildren().add(emptyLabel);
            return;
        }
        for (int i = stackValues.size() - 1; i >= 0; i--) {
            int val = stackValues.get(i);
            boolean isTop = (i == stackValues.size() - 1);

            HBox block = new HBox();
            block.getStyleClass().add("stack-block"); // Assicurati di avere questo nel CSS
            block.setStyle("-fx-background-color: " + (isTop ? "#4CAF50" : "#2E2E2E") + "; " +
                    "-fx-padding: 5; -fx-border-color: #555; -fx-border-width: 0 0 1 0;");

            Label valueLbl = new Label(String.valueOf(val));
            valueLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label addrLbl = new Label(isTop ? "TOP" : "["+i+"]");
            addrLbl.setStyle("-fx-text-fill: #aaa; -fx-font-size: 10px;");
            block.getChildren().addAll(valueLbl, spacer, addrLbl);
            visualStackContainer.getChildren().add(block);
        }
    }
    /**
     * Classe di modello interno per rappresentare una riga nella tabella della memoria.
     * <p>
     * Necessaria per il binding con {@link TableView}.
     * </p>
     */
    public static class MemoryRow {
        private final String address;
        private final String value;
        /**
         * Crea una nuova riga di memoria.
         * @param address Indirizzo formattato (es. "0x000A")
         * @param value Valore formattato (es. "1024")
         */
        public MemoryRow(String address, String value) {
            this.address = address;
            this.value = value;
        }
        public String getAddress() { return address; }
        public String getValue() { return value; }
    }

}
