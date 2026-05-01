package com.transportesrbl.controllers;

import com.transportesrbl.dao.ProductoDAO;
import com.transportesrbl.models.ComboItem;
import com.transportesrbl.models.Producto;
import com.transportesrbl.config.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FormProductoController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtVolumen;
    @FXML private Spinner<Integer> spnCantidad;
    @FXML private ComboBox<ComboItem> cbCliente;
    @FXML private ComboBox<String> cbEstado;
    @FXML private ComboBox<String> cbDestino;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private Producto productoExistente = null;

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        spnCantidad.setValueFactory(valueFactory);

        cbEstado.setItems(FXCollections.observableArrayList(
            "Pendiente",
            "En reparto",
            "Entregado",
            "No entregado"
        ));

        // Lista de destinos coherentes
        cbDestino.setItems(FXCollections.observableArrayList(
            "Cali - Bodega Principal",
            "Bogotá - Zona Franca",
            "Medellín - Distribuidora",
            "Barranquilla - Puerto",
            "Pereira - Centro Logístico"
        ));

        cargarClientesDisponibles();
    }

    public void setProducto(Producto producto) {
        this.productoExistente = producto;
        if (producto != null) {
            txtNombre.setText(producto.getNombreProducto());
            txtVolumen.setText(String.valueOf(producto.getVolumenUnitario()));
            spnCantidad.getValueFactory().setValue(producto.getCantidad());
            cbEstado.setValue(producto.getEstado());
            cbDestino.setValue(producto.getDestino());
            cbCliente.setValue(findItemByIdOrLabel(cbCliente.getItems(), producto.getClienteId(), producto.getCliente()));
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
            ComboItem clienteSeleccionado = cbCliente.getValue();
            double volumenTotal = volumenUnitario * cantidad;

            if (clienteSeleccionado == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, selecciona un cliente.");
                alert.showAndWait();
                return;
            }

            boolean exito;
            if (productoExistente == null) {
                Producto nuevoProducto = new Producto(
                    -1,
                    clienteSeleccionado.getId(),
                    nombre,
                    "Sin Proveedor",
                    clienteSeleccionado.getLabel(),
                    volumenUnitario,
                    cantidad,
                    volumenTotal,
                    estado,
                    destino
                );
                exito = productoDAO.insertar(nuevoProducto);
            } else {
                Producto actualizado = new Producto(
                    productoExistente.getIdProducto(),
                    clienteSeleccionado.getId(),
                    nombre,
                    "Sin Proveedor",
                    clienteSeleccionado.getLabel(),
                    volumenUnitario,
                    cantidad,
                    volumenTotal,
                    estado,
                    destino
                );
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

    private void cargarClientesDisponibles() {
        ObservableList<ComboItem> clientes = FXCollections.observableArrayList();
        String sql = "SELECT Id_Cliente, Nombre_Empresa FROM CLIENTE ORDER BY Nombre_Empresa";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(new ComboItem(rs.getInt("Id_Cliente"), rs.getString("Nombre_Empresa")));
            }
            cbCliente.setItems(clientes);
        } catch (SQLException e) {
            System.err.println("Error al cargar clientes: " + e.getMessage());
        }
    }

    private ComboItem findItemByIdOrLabel(ObservableList<ComboItem> items, int id, String label) {
        for (ComboItem item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        for (ComboItem item : items) {
            if (label != null && label.equals(item.getLabel())) {
                return item;
            }
        }
        return null;
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}