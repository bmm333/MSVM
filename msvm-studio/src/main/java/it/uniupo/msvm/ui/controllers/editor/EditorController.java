package it.uniupo.msvm.ui.controllers.editor;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
public class EditorController {
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
    /** Array di supporto per l'accesso rapido O(1) alle celle grafiche della griglia. */
    private Rectangle[] visualCells;
    /** Dimensione totale della memoria simulata (celle). */
    private final int MEM_SIZE = 1024;
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
        // ============================

        if(memoryGrid!=null){
            initVisualGrid();
        }
        // TODO: Rimuovere in produzione, serve solo per visualizzare il layout
        initFakeData();
    }
    /**
     * Aggiorna la rappresentazione visiva dello Stack degli operandi.
     * <p>
     * Pulisce il contenitore grafico e rigenera i blocchi (Card) basandosi sui valori forniti.
     * L'iterazione avviene in ordine inverso per visualizzare la "Cima" (Top) dello stack in alto.
     * </p>
     * @param stackValues Lista dei valori interi attualmente nello stack (dal fondo alla cima).
     */
    private void updateVisualStack(java.util.List<Integer> stackValues) {
        visualStackContainer.getChildren().clear();
        if (stackValues == null || stackValues.isEmpty()) {
            Label emptyLabel = new Label("Stack is Empty");
            emptyLabel.getStyleClass().add("stack-empty-label");
            visualStackContainer.getChildren().add(emptyLabel);
            return;
        }
        for (int i = stackValues.size() - 1; i >= 0; i--) {
            int val = stackValues.get(i);
            boolean isTop = (i == stackValues.size() - 1);
            HBox block = new HBox();
            block.getStyleClass().add("stack-block");
            if (isTop) {
                block.getStyleClass().add("stack-block-top");
            }
            Label valueLbl = new Label(String.valueOf(val));
            valueLbl.getStyleClass().add("stack-value");
            Label addrLbl = new Label("offset [" + i + "]");
            addrLbl.getStyleClass().add("stack-address");
            if (isTop) addrLbl.setText("TOP [" + i + "]");
            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            block.getChildren().addAll(valueLbl, spacer, addrLbl);
            visualStackContainer.getChildren().add(block);
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
        memoryGrid.setHgap(1);
        memoryGrid.setVgap(1);
        memoryGrid.setStyle("-fx-background-color: transparent;");
        for (int i = 0; i < MEM_SIZE; i++) {
            Rectangle cell = new Rectangle(8, 8);
            cell.setFill(Color.web("#333333"));
            cell.setArcWidth(2);
            cell.setArcHeight(2);
            Tooltip.install(cell, new Tooltip("0x" + String.format("%04X", i)));
            visualCells[i] = cell;
            memoryGrid.getChildren().add(cell);
        }
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
    @FXML public void onRun() { consoleArea.appendText("\n> Run clicked!"); }
    /** Gestisce il click sul pulsante "Step". Esegue una singola istruzione. */
    @FXML public void onStep() { consoleArea.appendText("\n> Step clicked!"); }
    /** Gestisce il click sul pulsante "Reset". Ripristina lo stato della VM. */
    @FXML public void onReset() { consoleArea.appendText("\n> Reset clicked!"); }
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
