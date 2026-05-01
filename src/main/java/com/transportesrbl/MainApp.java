package com.transportesrbl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/transportesrbl/views/fxml/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load()); 

        scene.getStylesheets().add(getClass().getResource("/com/transportesrbl/views/css/Style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Transportes RBL - Sistema Logístico");

        primaryStage.setResizable(false); 
        primaryStage.sizeToScene(); 
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}