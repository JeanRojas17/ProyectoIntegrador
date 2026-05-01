package com.transportesrbl.controllers;

import java.io.IOException;

import com.transportesrbl.models.Producto;
import com.transportesrbl.services.ProductoService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        configurarFiltros();
        configurarListeners();
        cargarDatos();
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

    private void configurarFiltros() {
        if (cbEstado != null) {
            cbEstado.setItems(FXCollections.observableArrayList("Seleccionar", "Pendiente", "En reparto", "Entregado", "No entregado"));
            cbEstado.setValue("Seleccionar");
        }
    }

    private void configurarListeners() {
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarProductos());
        }
        if (txtProveedor != null) {
            txtProveedor.textProperty().addListener((obs, oldValue, newValue) -> filtrarProductos());
        }
        if (txtCliente != null) {
            txtCliente.textProperty().addListener((obs, oldValue, newValue) -> filtrarProductos());
        }
        if (cbEstado != null) {
            cbEstado.setOnAction(e -> filtrarProductos());
        }
    }

    public void cargarDatos() {
        listaProductos = FXCollections.observableArrayList(service.obtenerTodosLosProductos());
        filtrarProductos();
    }

    private void filtrarProductos() {
        if (listaProductos == null) {
            return;
        }

        String busqueda = txtBuscar != null && txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String proveedor = txtProveedor != null && txtProveedor.getText() != null ? txtProveedor.getText().trim().toLowerCase() : "";
        String cliente = txtCliente != null && txtCliente.getText() != null ? txtCliente.getText().trim().toLowerCase() : "";
        String estado = cbEstado != null && cbEstado.getValue() != null ? cbEstado.getValue() : "Seleccionar";
        boolean filtrarEstado = !"Seleccionar".equalsIgnoreCase(estado);

        ObservableList<Producto> filtrados = FXCollections.observableArrayList();
        for (Producto p : listaProductos) {
            String nombre = p.getNombreProducto() != null ? p.getNombreProducto().toLowerCase() : "";
            String prov = p.getProveedor() != null ? p.getProveedor().toLowerCase() : "";
            String cli = p.getCliente() != null ? p.getCliente().toLowerCase() : "";
            String est = p.getEstado() != null ? p.getEstado().toLowerCase() : "";

            boolean coincideBusqueda = busqueda.isEmpty() || nombre.contains(busqueda);
            boolean coincideProveedor = proveedor.isEmpty() || prov.contains(proveedor);
            boolean coincideCliente = cliente.isEmpty() || cli.contains(cliente);
            boolean coincideEstado = !filtrarEstado || est.equalsIgnoreCase(estado);

            if (coincideBusqueda && coincideProveedor && coincideCliente && coincideEstado) {
                filtrados.add(p);
            }
        }

        tblProductos.setItems(filtrados);
        actualizarIndicadores();
    }

    @FXML
    private void handleNuevo(ActionEvent event) {
        abrirFormulario(null, "Nuevo Producto");
    }


    @FXML
    private void handleModificar(ActionEvent event) {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccionar Producto", "Por favor, selecciona un producto de la tabla para modificar.", Alert.AlertType.WARNING);
            return;
        }
        abrirFormulario(seleccionado, "Modificar Producto");
    }

    private void abrirFormulario(Producto producto, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/transportesrbl/views/fxml/form_producto.fxml"));
            Parent root = loader.load();
            
            FormProductoController controller = loader.getController();
            controller.setProducto(producto);

            Stage stage = new Stage();
            stage.setTitle("Transportes RBL - " + titulo);
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait(); 
            
            cargarDatos();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar el formulario.", Alert.AlertType.ERROR);
        }
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
        filtrarProductos();
    }

   private void actualizarIndicadores() {
        ObservableList<Producto> elementos = tblProductos != null ? tblProductos.getItems() : listaProductos;
        if (elementos == null) {
            return;
        }

        lblTotalProductos.setText(String.valueOf(elementos.size()));

        int entregados = 0;
        int pendientes = 0;
        double volumenTotal = 0.0;

        for (Producto p : elementos) {
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