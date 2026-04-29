package com.transportesrbl.controllers;

import com.transportesrbl.dao.AsignacionDAO;
import com.transportesrbl.models.Asignacion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class FormAsignacionController {

    @FXML private TextField txtProducto, cmbRuta; 
    @FXML private ComboBox<String> cmbCamion, cmbConductor;

   // @FXML private ComboBox<String> cmbCamion, cmbConductor, cmbRuta;

    private AsignacionDAO dao = new AsignacionDAO();

    @FXML
    public void initialize() {
        // Llenado de ComboBoxes
        cmbCamion.getItems().setAll("Chevrolet NPR", "NPR Turbo", "Foton");
        cmbConductor.getItems().setAll("Stiven Ramirez", "Edward Gomez", "Juan Perez");
    }

    @FXML
private void guardarAsignacion(ActionEvent event) {
    System.out.println(">>> Intentando guardar registro...");

    // 1. Validar campos (Ya lo tienes bien con .isEmpty() para la ruta)
    if (txtProducto.getText().isEmpty() || cmbCamion.getValue() == null || 
        cmbConductor.getValue() == null || cmbRuta.getText().isEmpty()) {
        
        mostrarAlerta("Campos Incompletos", "Por favor, completa todos los datos.");
        return;
    }

    try {
        // 2. Crear el objeto con .getText() en la posición de la ruta
        Asignacion nueva = new Asignacion(
            cmbCamion.getValue(),
            cmbConductor.getValue(),
            cmbRuta.getText(), // <--- Verifica que aquí diga .getText()
            txtProducto.getText(),
            "Pendiente"
        );

        if (dao.insertar(nueva)) {
            cerrarVentana(event);
        } else {
            mostrarAlerta("Error", "No se pudo guardar en la base de datos.");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    @FXML
    private void cerrarVentana(ActionEvent event) {
        // Obtenemos el Stage desde el evento del botón
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}