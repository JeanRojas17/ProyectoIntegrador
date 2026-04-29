package com.transportesrbl.controllers;

import com.transportesrbl.dao.AsignacionDAO;
import com.transportesrbl.models.Asignacion;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormAsignacionController {

    @FXML private TextField txtProducto, cmbRuta; 
    @FXML private ComboBox<String> cmbCamion, cmbConductor;
    @FXML private ComboBox<String> cmbEstado;

    private AsignacionDAO dao = new AsignacionDAO();
    private Asignacion asignacionExistente = null; // Para saber si editamos[cite: 3]

    @FXML
    public void initialize() {
        cmbCamion.getItems().setAll("Chevrolet NPR", "NPR Turbo", "Foton");
        cmbConductor.getItems().setAll("Stiven Ramirez", "Edward Gomez", "Juan Perez");
        cmbEstado.getItems().setAll("Pendiente", "En reparto", "Completado");
    }

    // Método para recibir datos desde la tabla principal[cite: 3]
    public void setAsignacion(Asignacion asig) {
        this.asignacionExistente = asig;
        txtProducto.setText(asig.getProducto());
        cmbCamion.setValue(asig.getCamion());
        cmbConductor.setValue(asig.getConductor());
        cmbRuta.setText(asig.getRuta());
        cmbEstado.setValue(asig.getEstado());
        txtProducto.setEditable(false); // Opcional: no permitir cambiar el producto al editar
    }

    @FXML
private void guardarAsignacion(ActionEvent event) {
    // 1. Validar que nada esté vacío
    if (txtProducto.getText().isEmpty() || cmbCamion.getValue() == null || 
        cmbConductor.getValue() == null || cmbRuta.getText().isEmpty() || 
        cmbEstado.getValue() == null) {
        
        mostrarAlerta("Campos Incompletos", "Por favor, completa todos los datos para Transportes RBL.");
        return;
    }

    try {
        if (asignacionExistente == null) {
            // --- MODO: NUEVO ---[cite: 3, 4]
            // Nota: En tu SQL esto insertaría en ASIGNACION_PAQUETE
            Asignacion nueva = new Asignacion(
                cmbCamion.getValue(),
                cmbConductor.getValue(),
                cmbRuta.getText(), // Equivale a Dir_Entrega en tu SQL
                txtProducto.getText(),
                cmbEstado.getValue()
            );
            if (dao.insertar(nueva)) {
                cerrarVentana(event);
            }
        } else {
            // --- MODO: EDITAR ---[cite: 3, 4]
            // Sincronizamos el objeto con lo que el usuario seleccionó en la interfaz
            asignacionExistente.setCamion(cmbCamion.getValue());
            asignacionExistente.setConductor(cmbConductor.getValue());
            asignacionExistente.setRuta(cmbRuta.getText()); // Setea la Dir_Entrega
            asignacionExistente.setEstado(cmbEstado.getValue()); // El nuevo estado elegido[cite: 3]

            if (dao.actualizar(asignacionExistente)) {
                cerrarVentana(event);
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el registro en la base de datos.");
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    @FXML private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML private void cancelar(ActionEvent event) { cerrarVentana(event); }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}