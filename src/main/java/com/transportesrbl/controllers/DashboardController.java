package com.transportesrbl.controllers;

import com.transportesrbl.models.Camion;
import com.transportesrbl.models.Entrega;
import com.transportesrbl.models.MetricasDashboard;
import com.transportesrbl.services.DashboardService;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private Label lblEntregasActivas, lblEntregasCompletas, lblProductosPendientes, lblCamionesDisponibles;
    @FXML private TableView<Entrega> tblEntregasRecientes;
    @FXML private TableColumn<Entrega, String> colProducto, colDireccion, colEstadoEntrega;
    @FXML private TableView<Camion> tblEstadoFlota;
    @FXML private TableColumn<Camion, String> colModelo, colEstadoCamion;
    @FXML private TableColumn<Camion, Double> colCapacidad;
    @FXML private Label lblNombreUsuario;
    @FXML private Label lblRolUsuario;
    
    @FXML private StackPane contentArea;

    private DashboardService service = new DashboardService();

    @FXML
    public void initialize() {
        if (tblEntregasRecientes != null) {
            configurarTablas();
            cargarDatos();
        }
        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        com.transportesrbl.models.Usuario usuario = com.transportesrbl.models.SesionUsuario.getInstancia().getUsuarioActivo();
        if (usuario != null) {
            if (lblNombreUsuario != null) {
                lblNombreUsuario.setText(usuario.getNombre());
            }
            if (lblRolUsuario != null) {
                lblRolUsuario.setText(usuario.getRol());
            }
        }
    }

    private void configurarTablas() {
        colProducto.setCellValueFactory(new PropertyValueFactory<>("producto"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colEstadoEntrega.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colEstadoCamion.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void cargarDatos() {
        MetricasDashboard m = service.obtenerEstadisticas();
        if (m != null) {
            lblEntregasActivas.setText(String.valueOf(m.getEntregasActivas()));
            lblEntregasCompletas.setText(String.valueOf(m.getEntregasCompletas()));
            lblProductosPendientes.setText(String.valueOf(m.getProductosPendientes()));
            lblCamionesDisponibles.setText(m.getCamionesDisponibles());
        }
        
        if (tblEntregasRecientes != null) {
            tblEntregasRecientes.setItems(FXCollections.observableArrayList(service.listarEntregas()));
        }
        if (tblEstadoFlota != null) {
            tblEstadoFlota.setItems(FXCollections.observableArrayList(service.listarFlota()));
        }
    }

    @FXML
    private void mostrarSeccionDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/transportesrbl/views/fxml/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void mostrarSeccionAsignaciones(ActionEvent event) {
        try {
            if (contentArea != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/transportesrbl/views/fxml/asignaciones.fxml"));
                Parent root = loader.load();
                contentArea.getChildren().setAll(root); 
                System.out.println("Sección de Asignaciones cargada en contentArea.");
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/transportesrbl/views/fxml/asignaciones.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
            }
        } catch (IOException e) {
            System.err.println("Error: No se pudo cargar asignaciones.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void MostrarSeccionProductos(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/transportesrbl/views/fxml/productos.fxml"));
            ScrollPane productosView = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(productosView);

            System.out.println(">>> Sección de Productos cargada en el contentArea.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la sección de productos: " + e.getMessage());
        }
    }

    @FXML
    private void mostrarSeccionCamiones(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/transportesrbl/views/fxml/camiones.fxml"));
            ScrollPane camionesView = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(camionesView);

            System.out.println(">>> Sección de Camiones cargada en el contentArea.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la sección de camiones: " + e.getMessage());
        }
    }

    @FXML
    private void mostrarSeccionPersonal(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/transportesrbl/views/fxml/personal.fxml"));
            ScrollPane personalView = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(personalView);

            System.out.println(">>> Sección de Personal cargada en el contentArea.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la sección de personal: " + e.getMessage());
        }
    }

    @FXML
    private void mostrarSeccionReportes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/transportesrbl/views/fxml/reportes.fxml"));
            ScrollPane reportesView = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(reportesView);

            System.out.println(">>> Sección de Reportes cargada en el contentArea.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la sección de reportes: " + e.getMessage());
        }
    }

    @FXML
    private void handleNuevaAsignacion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/transportesrbl/views/fxml/form_asignacion.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Transportes RBL - Nueva Asignación");
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait(); 

            cargarDatos(); 
        } catch (IOException e) {
            System.err.println("Error: No se pudo cargar form_asignacion.fxml");
            e.printStackTrace();
        }
    }
}