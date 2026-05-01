package com.transportesrbl.controllers;

import com.transportesrbl.dao.ProductoDAO;
import com.transportesrbl.models.Producto;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;

public class FormProductoController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtVolumen;
    @FXML private Spinner<Integer> spnCantidad;
    @FXML private ComboBox<String> cbEstado;
    @FXML private ComboBox<String> cbDestino;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private Producto productoExistente = null;

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        spnCantidad.setValueFactory(valueFactory);

        cbEstado.setItems(FXCollections.observableArrayList(
            "Disponible", 
            "En Tránsito", 
            "Agotado"
        ));

        // Lista de destinos coherentes
        cbDestino.setItems(FXCollections.observableArrayList(
            "Cali - Bodega Principal",
            "Bogotá - Zona Franca",
            "Medellín - Distribuidora",
            "Barranquilla - Puerto",
            "Pereira - Centro Logístico"
        ));
    }

    public void setProducto(Producto producto) {
        this.productoExistente = producto;
        if (producto != null) {
            txtNombre.setText(producto.getNombreProducto());
            txtVolumen.setText(String.valueOf(producto.getVolumenUnitario()));
            spnCantidad.getValueFactory().setValue(producto.getCantidad());
            cbEstado.setValue(producto.getEstado());
            cbDestino.setValue(producto.getDestino());
        }
    }

    @FXML
    private void handleGuardar(ActionEvent event) {
        try {
            String nombre = txtNombre.getText();
            String volumenStr = txtVolumen.getText().replace(",", ".");
            
            if (nombre.isEmpty() || volumenStr.isEmpty() || cbEstado.getValue() == null || cbDestino.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, complete todos los campos.");
                alert.showAndWait();
                return;
            }

            double volumenUnitario = Double.parseDouble(volumenStr);
            int cantidad = spnCantidad.getValue();
            String estado = cbEstado.getValue();
            String destino = cbDestino.getValue();
            double volumenTotal = volumenUnitario * cantidad;

            boolean exito;
            if (productoExistente == null) {
                Producto nuevoProducto = new Producto(nombre, volumenUnitario, cantidad, volumenTotal, estado, destino);
                exito = productoDAO.insertar(nuevoProducto);
            } else {
                Producto actualizado = new Producto(productoExistente.getIdProducto(), nombre, volumenUnitario, cantidad, volumenTotal, estado, destino);
                exito = productoDAO.actualizar(actualizado);
            }

            if (exito) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Operación realizada con éxito.");
                alert.showAndWait();
                Stage stage = (Stage) txtNombre.getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo guardar el producto en la base de datos.");
                alert.showAndWait();
            }

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Revise que los campos numéricos sean válidos.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}