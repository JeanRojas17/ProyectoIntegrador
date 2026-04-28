package com.transportesrbl.controllers;

import com.transportesrbl.models.Entrega;
import com.transportesrbl.models.Camion;
import com.transportesrbl.models.MetricasDashboard;
import com.transportesrbl.services.DashboardService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent; // Importante
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardController {

    @FXML private Label lblEntregasActivas, lblEntregasCompletas, lblProductosPendientes, lblCamionesDisponibles;
    
    @FXML private TableView<Entrega> tblEntregasRecientes;
    @FXML private TableColumn<Entrega, String> colProducto, colDireccion, colEstadoEntrega;

    @FXML private TableView<Camion> tblEstadoFlota;
    @FXML private TableColumn<Camion, String> colModelo, colEstadoCamion;
    @FXML private TableColumn<Camion, Double> colCapacidad;

    private DashboardService service = new DashboardService();

    @FXML
    public void initialize() {
        configurarTablas();
        cargarDatos();
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
        lblEntregasActivas.setText(String.valueOf(m.getEntregasActivas()));
        lblEntregasCompletas.setText(String.valueOf(m.getEntregasCompletas()));
        lblProductosPendientes.setText(String.valueOf(m.getProductosPendientes()));
        lblCamionesDisponibles.setText(m.getCamionesDisponibles());

        tblEntregasRecientes.setItems(FXCollections.observableArrayList(service.listarEntregas()));
        tblEstadoFlota.setItems(FXCollections.observableArrayList(service.listarFlota()));
    }

    // --- ESTE ES EL MÉTODO QUE FALTA Y CAUSA EL ERROR ---
    @FXML
    private void handleNuevaAsignacion(ActionEvent event) {
        System.out.println("Click en Nueva Asignación: El controlador ya lo reconoce.");
        // Aquí puedes poner una alerta o abrir la ventana de registro
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Módulo en Desarrollo");
        alert.setHeaderText(null);
        alert.setContentText("Próximamente: Formulario para nueva asignación.");
        alert.showAndWait();
    }
}