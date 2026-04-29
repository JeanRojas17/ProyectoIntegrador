package com.transportesrbl.controllers;

import java.io.IOException;
import java.util.Optional;

import com.transportesrbl.dao.AsignacionDAO;
import com.transportesrbl.models.Asignacion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AsignacionesController {

    @FXML private TableView<Asignacion> tblAsignaciones;
    @FXML private TableColumn<Asignacion, Integer> colId;
    @FXML private TableColumn<Asignacion, String> colCamion, colConductor, colRuta, colProducto, colEstado;
    
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEstado;
    @FXML private DatePicker dpFecha;

    private final AsignacionDAO dao = new AsignacionDAO();

    @FXML
    public void initialize() {
        configurarTabla();
        configurarFiltros();
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
            cbEstado.setItems(FXCollections.observableArrayList("Pendiente", "En Ruta", "Completado"));
        }
    }

    private void cargarDatos() {
        try {
            ObservableList<Asignacion> lista = FXCollections.observableArrayList(dao.listar());
            tblAsignaciones.setItems(lista);
            if (lista.isEmpty()) {
                System.out.println("No se encontraron registros para mostrar.");
            }
        } catch (Exception e) {
            mostrarAlerta("Error de Base de Datos", "No se pudieron cargar los datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
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
        
        // Pasamos el objeto seleccionado al controlador del formulario[cite: 2]
        FormAsignacionController formController = loader.getController();
        formController.setAsignacion(seleccionada); 

        Stage stage = new Stage();
        stage.setTitle("Transportes RBL - Modificar");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait(); 
        
        cargarDatos(); // Refresca la tabla al cerrar la ventana[cite: 2]
    } catch (IOException e) {
        mostrarAlerta("Error", "No se pudo abrir el editor.", Alert.AlertType.ERROR);
        e.printStackTrace();
    }
}


    
    @FXML
    private void handleEliminar(ActionEvent event) {
        Asignacion seleccionada = tblAsignaciones.getSelectionModel().getSelectedItem();
        
        if (seleccionada == null) {
            mostrarAlerta("Atención", "Por favor, selecciona una fila de la tabla primero.", Alert.AlertType.WARNING);
            return;
        }

        // Diálogo de confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Estás seguro de eliminar esta asignación?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (dao.eliminar(seleccionada.getId())) {
                cargarDatos();
                System.out.println("Asignación ID " + seleccionada.getId() + " eliminada.");
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el registro de la base de datos.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleLimpiar(ActionEvent event) {
        if (txtBuscar != null) txtBuscar.clear();
        if (cbEstado != null) cbEstado.getSelectionModel().clearSelection();
        if (dpFecha != null) dpFecha.setValue(null);
        
        cargarDatos();
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
            
            cargarDatos(); // Refrescar al cerrar
        } catch (IOException e) {
            mostrarAlerta("Error de Sistema", "No se pudo abrir el formulario: " + rutaFxml, Alert.AlertType.ERROR);
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