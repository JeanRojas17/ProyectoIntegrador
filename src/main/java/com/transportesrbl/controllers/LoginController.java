package com.transportesrbl.controllers;

import com.transportesrbl.models.Usuario;
import com.transportesrbl.services.AuthService;
import javafx.event.ActionEvent; // Te faltaba este import
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsuario;
    
    @FXML
    private PasswordField txtContrasena;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = txtUsuario.getText();
        String pass = txtContrasena.getText();

        // Validamos contra la base de datos de Neon
        Usuario usuario = authService.login(user, pass);

        if (usuario != null) {
            System.out.println("Bienvenido: " + usuario.getNombre());
            mostrarDashboard(event);
        } else {
            System.out.println("Error: Usuario o contraseña incorrectos en Neon DB.");
            // Opcional: Mostrar una alerta en pantalla
        }
    }

    private void mostrarDashboard(ActionEvent event) {
        try {
            // Cargamos el FXML del Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/transportesrbl/views/fxml/dashboard.fxml"));
            Parent root = loader.load();

            // Obtenemos la ventana (stage) actual
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Creamos la nueva escena para el Dashboard
            stage.setScene(new Scene(root));
            stage.setTitle("Transportes RBL - Dashboard");
            
            // ---- CONFIGURACIÓN CLAVE PARA PERMITIR REDIMENSIONAR ----
            stage.setResizable(true);
            stage.setMinWidth(1000); // Tamaño mínimo de ancho
            stage.setMinHeight(600);  // Tamaño mínimo de alto
            
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Error al cargar el Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}