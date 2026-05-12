package com.transportesrbl.controllers;

import com.transportesrbl.models.SesionUsuario;
import com.transportesrbl.models.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class DashboardAuxiliarController {

    @FXML private Label lblNombreAuxiliar;
    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        Usuario user = SesionUsuario.getInstancia().getUsuarioActivo();
        if (user != null) {
            lblNombreAuxiliar.setText(user.getNombre().toUpperCase());
        }
        cargarVista("/com/transportesrbl/views/fxml/mis_paquetes.fxml");
    }

    private void cargarVista(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Node vista = loader.load();
            contentArea.getChildren().setAll(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleInicio(ActionEvent event) {
        cargarVista("/com/transportesrbl/views/fxml/mis_paquetes.fxml");
    }

    @FXML
    private void handleResumen(ActionEvent event) {
        cargarVista("/com/transportesrbl/views/fxml/resumen_auxiliar.fxml");
    }

    @FXML
    private void handleVehiculo(ActionEvent event) {
        cargarVista("/com/transportesrbl/views/fxml/vehiculo_auxiliar.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            SesionUsuario.getInstancia().setUsuarioActivo(null);
            Parent root = FXMLLoader.load(getClass().getResource("/com/transportesrbl/views/fxml/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Transportes RBL - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}