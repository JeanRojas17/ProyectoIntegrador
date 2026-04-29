package com.transportesrbl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Cargamos el FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        
        // 2. Quitamos los números 400, 400 para que no fuerce un tamaño pequeño
        Scene scene = new Scene(fxmlLoader.load()); 

        // 3. Vinculamos el CSS (Asegúrate de que el archivo se llame Style.css con 'S' mayúscula si así lo tienes)
        scene.getStylesheets().add(getClass().getResource("/Style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Transportes RBL - Sistema Logístico");

        // 4. ESTA ES LA CLAVE PARA EL AUTOAJUSTE:
        primaryStage.setResizable(false); // Evita que el usuario deforme el diseño
        primaryStage.sizeToScene();      // Ajusta la ventana al tamaño exacto de los nodos del FXML
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}