package com.transportesrbl.controllers;

import com.transportesrbl.models.Producto;
import com.transportesrbl.services.ProductoService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductosController {

    // Componentes de la Tabla
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colProducto, colProveedor, colCliente, colDestino, colEstado;
    @FXML private TableColumn<Producto, Double> colVolumen;

    // Componentes de Filtros e Indicadores
    @FXML private TextField txtBuscar;
    @FXML private TextField txtProveedor;
    @FXML private TextField txtCliente;
    @FXML private ComboBox<String> cbEstado;

    @FXML private Label lblTotalProductos;
    @FXML private Label lblEntregados;
    @FXML private Label lblPendientes;
    @FXML private Label lblVolumenTotal;

    private final ProductoService service = new ProductoService();
    private ObservableList<Producto> listaProductos;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();
        configurarFiltros();
        actualizarIndicadores();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("proveedor"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colVolumen.setCellValueFactory(new PropertyValueFactory<>("volumen"));
        colDestino.setCellValueFactory(new PropertyValueFactory<>("destino"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    public void cargarDatos() {
        listaProductos = FXCollections.observableArrayList(service.obtenerTodosLosProductos());
        tblProductos.setItems(listaProductos);
    }

    private void configurarFiltros() {
        if (cbEstado != null) {
            cbEstado.setItems(FXCollections.observableArrayList("Seleccionar", "Entregado", "Pendiente"));
            cbEstado.setValue("Seleccionar");
        }
    }

    @FXML
    private void handleNuevo(ActionEvent event) {
        // Lógica para abrir modal de crear un nuevo producto
    }

    @FXML
    private void handleModificar(ActionEvent event) {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccionar Producto", "Por favor, selecciona un producto de la tabla para modificar.", Alert.AlertType.WARNING);
            return;
        }
        // Lógica de modificación
    }

    @FXML
    private void handleEliminar(ActionEvent event) {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccionar Producto", "Por favor, selecciona un producto de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        boolean eliminado = service.eliminarProducto(seleccionado.getIdProducto());
        if (eliminado) {
            cargarDatos();
            actualizarIndicadores();
        } else {
            mostrarAlerta("Error", "No se pudo eliminar el producto.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLimpiar(ActionEvent event) {
        if (txtBuscar != null) txtBuscar.clear();
        if (txtProveedor != null) txtProveedor.clear();
        if (txtCliente != null) txtCliente.clear();
        if (cbEstado != null) cbEstado.setValue("Seleccionar");
        
        cargarDatos();
        actualizarIndicadores();
    }

   private void actualizarIndicadores() {
        if (listaProductos == null) {
            return;
        }

        lblTotalProductos.setText(String.valueOf(listaProductos.size()));

        int entregados = 0;
        int pendientes = 0;
        double volumenTotal = 0.0;

        for (Producto p : listaProductos) {
            volumenTotal += p.getVolumen();
            String estado = p.getEstado();

            if (estado != null) {
                if (estado.equalsIgnoreCase("Entregado") || estado.equalsIgnoreCase("Completado")) {
                    entregados++;
                } else {
                    pendientes++;
                }
            } else {
                pendientes++;
            }
        }

        if (lblEntregados != null) {
            lblEntregados.setText(String.valueOf(entregados));
        }
        if (lblPendientes != null) {
            lblPendientes.setText(String.valueOf(pendientes));
        }
        if (lblVolumenTotal != null) {
            lblVolumenTotal.setText(String.format("%.2f", volumenTotal));
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}