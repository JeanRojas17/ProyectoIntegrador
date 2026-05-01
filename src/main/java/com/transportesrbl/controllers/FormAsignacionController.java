package com.transportesrbl.controllers;

import com.transportesrbl.dao.AsignacionDAO;
import com.transportesrbl.models.Asignacion;
import com.transportesrbl.models.ComboItem;
import com.transportesrbl.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormAsignacionController {

    @FXML private ComboBox<ComboItem> txtProducto;
    @FXML private TextField cmbRuta;
    @FXML private ComboBox<ComboItem> cmbCamion;
    @FXML private ComboBox<ComboItem> cmbConductor;
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
        ObservableList<ComboItem> camiones = FXCollections.observableArrayList();
        String sql = "SELECT id_camion, modelo_camion FROM CAMIONES ORDER BY modelo_camion";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                camiones.add(new ComboItem(rs.getInt("id_camion"), rs.getString("modelo_camion")));
            }
            cmbCamion.setItems(camiones);
        } catch (SQLException e) {
            System.err.println("Error al cargar camiones: " + e.getMessage());
        }
    }

    private void cargarConductoresDisponibles() {
        ObservableList<ComboItem> conductores = FXCollections.observableArrayList();
        String sql = "SELECT id_conductor, nombre_completo FROM CONDUCTORES ORDER BY nombre_completo";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                conductores.add(new ComboItem(rs.getInt("id_conductor"), rs.getString("nombre_completo")));
            }
            cmbConductor.setItems(conductores);
        } catch (SQLException e) {
            System.err.println("Error al cargar conductores: " + e.getMessage());
        }
    }

    private void cargarProductosDisponibles() {
        ObservableList<ComboItem> productos = FXCollections.observableArrayList();
        String sql = "SELECT Id_Paquete, Descripcion FROM PAQUETE ORDER BY Descripcion";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(new ComboItem(rs.getInt("Id_Paquete"), rs.getString("Descripcion")));
            }
            txtProducto.setItems(productos);
        } catch (SQLException e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
        }
    }

    public void setAsignacion(Asignacion asig) {
        this.asignacionExistente = asig;
        if (asig != null) {
            txtProducto.setValue(findItemByIdOrLabel(txtProducto.getItems(), asig.getProductoId(), asig.getProducto()));
            cmbRuta.setText(asig.getRuta());
            cmbCamion.setValue(findItemByIdOrLabel(cmbCamion.getItems(), asig.getCamionId(), asig.getCamion()));
            cmbConductor.setValue(findItemByIdOrLabel(cmbConductor.getItems(), asig.getConductorId(), asig.getConductor()));
            cmbEstado.setValue(asig.getEstado());
        }
    }

    @FXML 
    private void guardar(ActionEvent event) {
        try {
            ComboItem productoSeleccionado = txtProducto.getValue();
            ComboItem camionSeleccionado = cmbCamion.getValue();
            ComboItem conductorSeleccionado = cmbConductor.getValue();

            if (productoSeleccionado == null || camionSeleccionado == null || conductorSeleccionado == null || cmbRuta.getText().isEmpty() || cmbEstado.getValue() == null) {
                mostrarAlerta("Campos Incompletos", "Por favor, complete todos los campos de la asignación.");
                return;
            }

            if (asignacionExistente == null) {
                Asignacion nueva = new Asignacion(
                    camionSeleccionado.getId(),
                    conductorSeleccionado.getId(),
                    productoSeleccionado.getId(),
                    camionSeleccionado.getLabel(),
                    conductorSeleccionado.getLabel(),
                    cmbRuta.getText(),
                    productoSeleccionado.getLabel(),
                    cmbEstado.getValue()
                );
                if (dao.insertar(nueva)) {
                    cerrarVentana(event);
                } else {
                    mostrarAlerta("Error", "No se pudo guardar la asignación.");
                }
            } else {
                asignacionExistente.setCamionId(camionSeleccionado.getId());
                asignacionExistente.setConductorId(conductorSeleccionado.getId());
                asignacionExistente.setProductoId(productoSeleccionado.getId());
                asignacionExistente.setCamion(camionSeleccionado.getLabel());
                asignacionExistente.setConductor(conductorSeleccionado.getLabel());
                asignacionExistente.setProducto(productoSeleccionado.getLabel());
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
            mostrarAlerta("Error", "Ocurrió un error inesperado al guardar la asignación.");
        }
    }

    private ComboItem findItemByIdOrLabel(ObservableList<ComboItem> items, Integer id, String label) {
        if (id != null) {
            for (ComboItem item : items) {
                if (id.equals(item.getId())) {
                    return item;
                }
            }
        }
        if (label != null) {
            for (ComboItem item : items) {
                if (label.equals(item.getLabel())) {
                    return item;
                }
            }
        }
        return null;
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