package com.transportesrbl.controllers;

import com.transportesrbl.models.*;
import com.transportesrbl.services.DashboardService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane; // Asegúrate de que tu contenedor sea un BorderPane
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class DashboardController {

    @FXML private Label lblEntregasActivas, lblEntregasCompletas, lblProductosPendientes, lblCamionesDisponibles;
    @FXML private TableView<Entrega> tblEntregasRecientes;
    @FXML private TableColumn<Entrega, String> colProducto, colDireccion, colEstadoEntrega;
    @FXML private TableView<Camion> tblEstadoFlota;
    @FXML private TableColumn<Camion, String> colModelo, colEstadoCamion;
    @FXML private TableColumn<Camion, Double> colCapacidad;

    // --- ESTA ES LA LÍNEA QUE TE FALTABA ---
    // Asegúrate de que en tu dashboard.fxml, el contenedor central tenga fx:id="contentArea"
    @FXML 
private StackPane contentArea;

    private DashboardService service = new DashboardService();

    @FXML
    public void initialize() {
        if (tblEntregasRecientes != null) {
            configurarTablas();
            cargarDatos();
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
        tblEntregasRecientes.setItems(FXCollections.observableArrayList(service.listarEntregas()));
        tblEstadoFlota.setItems(FXCollections.observableArrayList(service.listarFlota()));
    }

    @FXML
    private void mostrarSeccionDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/asignaciones.fxml"));
        Parent root = loader.load();
        
        // Limpiamos el contenido actual y ponemos el nuevo
        contentArea.getChildren().setAll(root); 
        
        System.out.println("Cargando sección de Asignaciones...");
    } catch (IOException e) {
        System.err.println("Error: No se pudo cargar asignaciones.fxml");
        e.printStackTrace();
    }
}

    @FXML
    private void handleNuevaAsignacion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/form_asignacion.fxml"));
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