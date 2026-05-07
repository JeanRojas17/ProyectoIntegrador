package com.transportesrbl.controllers;

import com.transportesrbl.models.Personal;
import com.transportesrbl.services.PersonalService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class FormPersonalController {

    @FXML private Label lblTitulo, lblDinamico;
    @FXML private TextField txtNombre, txtIdentificacion, txtTelefono, txtCorreo, txtDinamico;
    @FXML private ComboBox<String> cbTipo, cbEstado;

    private final PersonalService service = new PersonalService();
    private Personal personal;
    private boolean esNuevo = true;

    @FXML
    public void initialize() {
        cbTipo.setItems(FXCollections.observableArrayList("CONDUCTOR", "AUXILIAR"));
        cbEstado.setItems(FXCollections.observableArrayList("ACTIVO", "INACTIVO"));
        
        cbTipo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("CONDUCTOR".equals(newVal)) {
                lblDinamico.setText("Licencia:");
                txtDinamico.setPromptText("Ej: B1, C2");
            } else {
                lblDinamico.setText("Especialidad:");
                txtDinamico.setPromptText("Ej: Carga pesada, Mecánica");
            }
        });
        
        cbTipo.setValue("CONDUCTOR");
        cbEstado.setValue("ACTIVO");
    }

    public void setPersonal(Personal p) {
        if (p != null) {
            this.personal = p;
            this.esNuevo = false;
            lblTitulo.setText("MODIFICAR PERSONAL");
            
            txtNombre.setText(p.getNombre());
            cbTipo.setValue(p.getTipo());
            txtIdentificacion.setText(p.getIdentificacion());
            txtTelefono.setText(p.getTelefono());
            txtCorreo.setText(p.getCorreo());
            cbEstado.setValue(p.getEstado());
            
            if ("CONDUCTOR".equals(p.getTipo())) {
                txtDinamico.setText(p.getLicencia());
            } else {
                txtDinamico.setText(p.getEspecialidad());
            }
            cbTipo.setDisable(true); // No permitir cambiar el tipo al editar
        }
    }

    @FXML
    private void handleGuardar(ActionEvent event) {
        if (validarCampos()) {
            if (personal == null) personal = new Personal();
            
            personal.setNombre(txtNombre.getText());
            personal.setTipo(cbTipo.getValue());
            personal.setIdentificacion(txtIdentificacion.getText());
            personal.setTelefono(txtTelefono.getText());
            personal.setCorreo(txtCorreo.getText());
            personal.setEstado(cbEstado.getValue());
            
            if ("CONDUCTOR".equals(cbTipo.getValue())) {
                personal.setLicencia(txtDinamico.getText());
                personal.setEspecialidad("");
            } else {
                personal.setEspecialidad(txtDinamico.getText());
                personal.setLicencia("");
            }

            if (service.guardar(personal)) {
                cerrarVentana(event);
            } else {
                mostrarAlerta("Error", "No se pudo guardar la información del personal.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    private boolean validarCampos() {
        if (txtNombre.getText().isEmpty() || txtIdentificacion.getText().isEmpty()) {
            mostrarAlerta("Campos Requeridos", "Por favor complete el nombre y la identificación.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
