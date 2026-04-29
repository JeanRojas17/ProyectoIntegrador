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
    
    @FXML private StackPane contentArea; // El contenedor principal del dashboard.fxml

    private DashboardService service = new DashboardService();

    @FXML
    public void initialize() {
        // Solo intentamos cargar datos si los componentes de la tabla existen
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
            // Usamos los getters de tu modelo MetricasDashboard[cite: 4, 5]
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
            // Verificamos que contentArea no sea null antes de usarlo[cite: 4]
            if (contentArea != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/asignaciones.fxml"));
                Parent root = loader.load();
                contentArea.getChildren().setAll(root); 
                System.out.println("Sección de Asignaciones cargada en contentArea.");
            } else {
                // Si es null, cargamos la escena completa para evitar el error[cite: 4]
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/asignaciones.fxml"));
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
            
            // Refrescamos los datos después de cerrar el formulario
            cargarDatos(); 
        } catch (IOException e) {
            System.err.println("Error: No se pudo cargar form_asignacion.fxml");
            e.printStackTrace();
        }
    }
}