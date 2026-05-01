package com.transportesrbl.controllers;

import com.transportesrbl.models.Camion;
import com.transportesrbl.services.CamionService;
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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CamionesController {

    @FXML private TableView<Camion> tblCamiones;
    @FXML private TableColumn<Camion, Integer> colId;
    @FXML private TableColumn<Camion, String> colModelo;
    @FXML private TableColumn<Camion, Double> colCapacidad;
    @FXML private TableColumn<Camion, String> colEstado;

    @FXML private Label lblTotalCamiones;
    @FXML private Label lblDisponibles;
    @FXML private Label lblMantenimiento;
    @FXML private Label lblCapacidadTotal;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEstado;

    private final CamionService service = new CamionService();
    private ObservableList<Camion> listaCamiones;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();
        configurarFiltros();
        actualizarIndicadores();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void cargarDatos() {
        listaCamiones = FXCollections.observableArrayList(service.obtenerTodos());
        tblCamiones.setItems(listaCamiones);
    }

    private void configurarFiltros() {
        if (cbEstado != null) {
            cbEstado.setItems(FXCollections.observableArrayList("Seleccionar", "Disponible", "En ruta", "Mantenimiento"));
            cbEstado.setValue("Seleccionar");
        }
    }

    private void actualizarIndicadores() {
        if (listaCamiones == null) return;

        long total = listaCamiones.size();
        long disponibles = listaCamiones.stream().filter(c -> c.getEstado().equalsIgnoreCase("Disponible")).count();
        long mantenimiento = listaCamiones.stream().filter(c -> c.getEstado().equalsIgnoreCase("Mantenimiento")).count();
        double capacidadTotal = listaCamiones.stream().mapToDouble(Camion::getCapacidad).sum();

        if (lblTotalCamiones != null) lblTotalCamiones.setText(String.valueOf(total));
        if (lblDisponibles != null) lblDisponibles.setText(String.valueOf(disponibles));
        if (lblMantenimiento != null) lblMantenimiento.setText(String.valueOf(mantenimiento));
        if (lblCapacidadTotal != null) lblCapacidadTotal.setText(String.format("%.2f m³", capacidadTotal));
    }

    @FXML
    private void handleNuevo(ActionEvent event) {
        abrirFormulario(null, "Nuevo Camión");
    }

    @FXML
    private void handleModificar(ActionEvent event) {
        Camion seleccionado = tblCamiones.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Seleccione un camión de la tabla.", Alert.AlertType.WARNING);
            return;
        }
        abrirFormulario(seleccionado, "Modificar Camión");
    }

    @FXML
    private void handleEliminar(ActionEvent event) {
        Camion seleccionado = tblCamiones.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Seleccione un camión de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar Camión");
        confirm.setHeaderText("¿Está seguro de eliminar este camión?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (service.eliminar(seleccionado.getId())) {
                cargarDatos();
                actualizarIndicadores();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el camión.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleLimpiar(ActionEvent event) {
        if (txtBuscar != null) txtBuscar.clear();
        if (cbEstado != null) cbEstado.setValue("Seleccionar");
        cargarDatos();
    }

    private void abrirFormulario(Camion camion, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/transportesrbl/views/fxml/form_camion.fxml"));
            Parent root = loader.load();
            
            FormCamionController controller = loader.getController();
            controller.setCamion(camion);

            Stage stage = new Stage();
            stage.setTitle("Transportes RBL - " + titulo);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            cargarDatos();
            actualizarIndicadores();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar el formulario.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
