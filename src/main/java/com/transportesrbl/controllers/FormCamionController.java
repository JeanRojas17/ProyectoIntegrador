package com.transportesrbl.controllers;

import com.transportesrbl.dao.CamionDAO;
import com.transportesrbl.models.Camion;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormCamionController {

    @FXML private TextField txtModelo;
    @FXML private TextField txtCapacidad;
    @FXML private ComboBox<String> cbEstado;

    private final CamionDAO camionDAO = new CamionDAO();
    private Camion camionExistente = null;

    @FXML
    public void initialize() {
        cbEstado.setItems(FXCollections.observableArrayList(
            "Disponible", 
            "En ruta", 
            "Mantenimiento"
        ));
    }

    public void setCamion(Camion camion) {
        this.camionExistente = camion;
        if (camion != null) {
            txtModelo.setText(camion.getModelo());
            txtCapacidad.setText(String.valueOf(camion.getCapacidad()));
            cbEstado.setValue(camion.getEstado());
        }
    }

    @FXML
    private void handleGuardar(ActionEvent event) {
        try {
            String modelo = txtModelo.getText();
            String capacidadStr = txtCapacidad.getText().replace(",", ".");
            
            if (modelo.isEmpty() || capacidadStr.isEmpty() || cbEstado.getValue() == null) {
                mostrarAlerta("Campos Incompletos", "Por favor, complete todos los campos.");
                return;
            }

            double capacidad = Double.parseDouble(capacidadStr);
            String estado = cbEstado.getValue();

            boolean exito;
            if (camionExistente == null) {
                Camion nuevo = new Camion(0, modelo, capacidad, estado);
                exito = camionDAO.insertar(nuevo);
            } else {
                Camion actualizado = new Camion(camionExistente.getId(), modelo, capacidad, estado);
                exito = camionDAO.actualizar(actualizado);
            }

            if (exito) {
                Stage stage = (Stage) txtModelo.getScene().getWindow();
                stage.close();
            } else {
                mostrarAlerta("Error", "No se pudo guardar el camión.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "La capacidad debe ser un número válido.");
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        Stage stage = (Stage) txtModelo.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}