package com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/login.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 400, 400); // Definimos un tamaño inicial

    // ESTA LÍNEA ES LA CLAVE: busca el archivo style.css en src/main/resources
    scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

    primaryStage.setScene(scene);
    primaryStage.setTitle("Transportes RBL - Sistema Logístico");
    primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}