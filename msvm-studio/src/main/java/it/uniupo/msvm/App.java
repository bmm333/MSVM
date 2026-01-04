package it.uniupo.msvm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;



public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        //Carico il file Home.fxml

       // FXMLLoader home= new FXMLLoader(App.class.getResource("ui/home.fxml"));
        FXMLLoader loader = new FXMLLoader(App.class.getResource("ui/mainwindow.fxml"));
        Scene scene = new Scene(loader.load(),1024,768);

        stage.setTitle("MSVM Studio TEST mode");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String [] args){
        launch();
    }
}
