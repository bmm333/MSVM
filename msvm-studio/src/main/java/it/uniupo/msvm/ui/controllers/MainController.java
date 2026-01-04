package it.uniupo.msvm.ui.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainController {
    //Rif FXML
    @FXML private TextArea codeEditor;
    @FXML private TextArea consoleArea;
    @FXML private Label lblIp;
    @FXML private Label lblStackSize;
    //Stack view
    @FXML private ListView<String> stackList;

    //Memory view
    @FXML private TableView<MemoryRow> memoryTable;
    @FXML private TableColumn<MemoryRow, String> colAddress;
    @FXML private TableColumn<MemoryRow, String> colValue;

    private ObservableList<MemoryRow> memoryData = FXCollections.observableArrayList();
    @FXML
    public void initialize() {
        //Setup colonne tabella mem
        colAddress.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        colValue.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        memoryTable.setItems(memoryData);
        //Popoliamo dati finti per vedere il funzionamento
        initFakeData();
    }
    private void initFakeData() {
        // Simuliamo Ram
        for (int i = 0; i < 10; i++) {
            memoryData.add(new MemoryRow(String.format("0x%04X", i), "0000"));
        }
        // Simuliamo stack
        stackList.getItems().addAll("10 (Top)", "55", "1024");

        lblIp.setText("0004");
        consoleArea.setText("MSVM Initialized ready to rock.");
    }
    @FXML public void onRun() { consoleArea.appendText("\n> Run clicked!"); }
    @FXML public void onStep() { consoleArea.appendText("\n> Step clicked!"); }
    @FXML public void onReset() { consoleArea.appendText("\n> Reset clicked!"); }
    public static class MemoryRow {
        private final String address;
        private final String value;

        public MemoryRow(String address, String value) {
            this.address = address;
            this.value = value;
        }

        public String getAddress() { return address; }
        public String getValue() { return value; }
    }
}
