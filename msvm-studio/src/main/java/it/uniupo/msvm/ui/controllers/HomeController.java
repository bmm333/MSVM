package it.uniupo.msvm.ui.controllers;

import javafx.fxml.FXML;

public class HomeController {

    @FXML
    public void inizialize(){
        System.out.println("Homepage Initialization");
    }

    @FXML
    public void handleCreaFile(){
        System.out.println("Click su Crea File");
        // TODO: Aprire la vista Editor vuota
    }

    @FXML
    public void handleApriFile() {
        System.out.println("Click su Apri File");
        // TODO: Aprire il FileChooser di JavaFX
    }

    @FXML
    public void handleLogin() {
        System.out.println("Click su Profilo Utente!");
        // TODO: Fare il Login/register della applicazione
    }
}
