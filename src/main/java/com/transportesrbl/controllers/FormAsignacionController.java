package com.transportesrbl.controllers;

import com.transportesrbl.dao.AsignacionDAO;
import com.transportesrbl.models.Asignacion;
import com.transportesrbl.config.DatabaseConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FormAsignacionController {

    // Cambiamos el TextField de texto por un ComboBox para seleccionar de la BD
    @FXML private ComboBox<String> txtProducto; 
    @FXML private TextField cmbRuta; 
    @FXML private ComboBox<String> cmbCamion, cmbConductor;
    @FXML private ComboBox<String> cmbEstado;

    private AsignacionDAO dao = new AsignacionDAO();
    private Asignacion asignacionExistente = null; 

    @FXML
    public void initialize() {
        cmbEstado.getItems().setAll("Pendiente", "En reparto", "Entregado", "No entregado");
        
        cargarCamionesDisponibles();
        cargarConductoresDisponibles();
        cargarProductosDisponibles();
    }

    private void cargarCamionesDisponibles() {
        ObservableList<String> camiones = FXCollections.observableArrayList();
        String sql = "SELECT modelo_camion FROM CAMIONES";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) camiones.add(rs.getString("modelo_camion"));
            cmbCamion.setItems(camiones);
        } catch (SQLException e) {
            System.err.println("Error al cargar camiones: " + e.getMessage());
        }
    }

    private void cargarConductoresDisponibles() {
        ObservableList<String> conductores = FXCollections.observableArrayList();
        String sql = "SELECT nombre_completo FROM CONDUCTORES";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) conductores.add(rs.getString("nombre_completo"));
            cmbConductor.setItems(conductores);
        } catch (SQLException e) {
            System.err.println("Error al cargar conductores: " + e.getMessage());
        }
    }

    private void cargarProductosDisponibles() {
        ObservableList<String> productos = FXCollections.observableArrayList();
        String sql = "SELECT Descripcion FROM PAQUETE"; // Puedes cambiar a Productos si usas esa tabla
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                productos.add(rs.getString("Descripcion"));
            }
            
            txtProducto.setItems(productos);
            
        } catch (SQLException e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
        }
    }

    public void setAsignacion(Asignacion asig) {
        this.asignacionExistente = asig;
        if (asig != null) {
            txtProducto.setValue(asig.getProducto());
            cmbRuta.setText(asig.getRuta());
            cmbCamion.setValue(asig.getCamion());
            cmbConductor.setValue(asig.getConductor());
            cmbEstado.setValue(asig.getEstado());
        }
    }

    @FXML 
    private void guardar(ActionEvent event) {
        try {
            if (txtProducto.getValue() == null || cmbCamion.getValue() == null || cmbConductor.getValue() == null || cmbRuta.getText().isEmpty()) {
                mostrarAlerta("Campos Incompletos", "Por favor, complete todos los campos de la asignación.");
                return;
            }

            if (asignacionExistente == null) {
                Asignacion nueva = new Asignacion(
                    0,
                    cmbCamion.getValue(),
                    cmbConductor.getValue(),
                    cmbRuta.getText(),
                    txtProducto.getValue(), // Usa el valor seleccionado de la base de datos
                    cmbEstado.getValue()
                );
                if (dao.insertar(nueva)) {
                    cerrarVentana(event);
                }
            } else {
                asignacionExistente.setCamion(cmbCamion.getValue());
                asignacionExistente.setConductor(cmbConductor.getValue());
                asignacionExistente.setRuta(cmbRuta.getText());
                asignacionExistente.setEstado(cmbEstado.getValue());

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

    @FXML 
    private void cerrarVentana(ActionEvent event) {
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