package com.transportesrbl.controllers;

import java.io.IOException;
import java.util.Optional;
import java.time.LocalDate;

import com.transportesrbl.dao.AsignacionDAO;
import com.transportesrbl.models.Asignacion;

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
import javafx.scene.Node;

public class AsignacionesController {

    // Componentes de la Tabla
    @FXML private TableView<Asignacion> tblAsignaciones;
    @FXML private TableColumn<Asignacion, Integer> colId;
    @FXML private TableColumn<Asignacion, String> colCamion, colConductor, colRuta, colProducto, colEstado;
    
    // Componentes de Filtros
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEstado;
    @FXML private DatePicker dpFecha;

    private final AsignacionDAO dao = new AsignacionDAO();

    @FXML
    public void initialize() {
        configurarTabla();
        configurarFiltros();
        
        // Listeners para búsqueda en tiempo real mientras el usuario escribe o selecciona[cite: 7]
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> ejecutarFiltro());
        }
        if (cbEstado != null) {
            cbEstado.setOnAction(e -> ejecutarFiltro());
        }
        if (dpFecha != null) {
            dpFecha.setOnAction(e -> ejecutarFiltro());
        }

        cargarDatos();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCamion.setCellValueFactory(new PropertyValueFactory<>("camion"));
        colConductor.setCellValueFactory(new PropertyValueFactory<>("conductor"));
        colRuta.setCellValueFactory(new PropertyValueFactory<>("ruta"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("producto"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void configurarFiltros() {
        if (cbEstado != null) {
            // "Seleccionar" actúa como el valor nulo para mostrar todo[cite: 7]
            cbEstado.setItems(FXCollections.observableArrayList("Seleccionar", "Pendiente", "En reparto", "Completado"));
            cbEstado.setValue("Seleccionar");
        }
    }

    private void cargarDatos() {
        try {
            // Carga inicial completa usando el método listar original
            ObservableList<Asignacion> lista = FXCollections.observableArrayList(dao.listar());
            tblAsignaciones.setItems(lista);
        } catch (Exception e) {
            mostrarAlerta("Error de Base de Datos", "No se pudieron cargar los datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void ejecutarFiltro() {
        String producto = txtBuscar.getText();
        String estado = cbEstado.getValue();
        LocalDate fecha = dpFecha.getValue();

        // Llama al método de búsqueda dinámica que añadimos al DAO[cite: 7, 8]
        ObservableList<Asignacion> listaFiltrada = FXCollections.observableArrayList(
            dao.buscarConFiltros(producto, estado, fecha)
        );
        tblAsignaciones.setItems(listaFiltrada);
    }

    @FXML
    private void handleNuevo(ActionEvent event) {
        abrirFormulario("/form_asignacion.fxml", "Nueva Asignación");
    }

    @FXML
    private void handleModificar(ActionEvent event) {
        Asignacion seleccionada = tblAsignaciones.getSelectionModel().getSelectedItem();
        
        if (seleccionada == null) {
            mostrarAlerta("Atención", "Selecciona una fila para modificar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/form_asignacion.fxml"));
            Parent root = loader.load();
            
            FormAsignacionController formController = loader.getController();
            formController.setAsignacion(seleccionada); 

            Stage stage = new Stage();
            stage.setTitle("Transportes RBL - Modificar");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait(); 
            
            ejecutarFiltro(); // Refresca manteniendo los filtros activos[cite: 7]
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir el editor.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEliminar(ActionEvent event) {
        Asignacion seleccionada = tblAsignaciones.getSelectionModel().getSelectedItem();
        
        if (seleccionada == null) {
            mostrarAlerta("Atención", "Por favor, selecciona una fila primero.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Estás seguro de eliminar esta asignación?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (dao.eliminar(seleccionada.getId())) {
                ejecutarFiltro(); // Refresca la tabla tras eliminar[cite: 7, 8]
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el registro.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleLimpiar(ActionEvent event) {
        if (txtBuscar != null) txtBuscar.clear();
        if (cbEstado != null) cbEstado.setValue("Seleccionar");
        if (dpFecha != null) dpFecha.setValue(null);
        
        cargarDatos(); // Restablece la tabla a la vista completa[cite: 7]
    }

    // --- MÉTODOS DE APOYO (HELPER METHODS) ---

    private void abrirFormulario(String rutaFxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Transportes RBL - " + titulo);
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait(); 
            
            cargarDatos(); 
        } catch (IOException e) {
            mostrarAlerta("Error de Sistema", "No se pudo abrir el formulario.", Alert.AlertType.ERROR);
            e.printStackTrace();
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