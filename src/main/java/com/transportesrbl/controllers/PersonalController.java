package com.transportesrbl.controllers;

import com.transportesrbl.models.Personal;
import com.transportesrbl.services.PersonalService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.util.List;
import java.util.stream.Collectors;

public class PersonalController {

    @FXML private Label lblTotalPersonal, lblTotalConductores, lblTotalAuxiliares, lblActivos;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEstado;
    @FXML private ComboBox<String> cbTipo;
    @FXML private FlowPane flowPersonal;

    private final PersonalService service = new PersonalService();
    private List<Personal> listaCompleta;

    @FXML
    public void initialize() {
        configurarFiltros();
        cargarDatos();
        
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> filtrar());
        cbEstado.setOnAction(e -> filtrar());
        cbTipo.setOnAction(e -> filtrar());
    }

    private void configurarFiltros() {
        cbEstado.setItems(FXCollections.observableArrayList("TODOS", "ACTIVO", "INACTIVO"));
        cbEstado.setValue("TODOS");
        
        cbTipo.setItems(FXCollections.observableArrayList("TODOS", "CONDUCTOR", "AUXILIAR"));
        cbTipo.setValue("TODOS");
    }

    private void cargarDatos() {
        listaCompleta = service.obtenerTodo();
        actualizarMetricas(listaCompleta);
        filtrar();
    }

    private void actualizarMetricas(List<Personal> lista) {
        lblTotalPersonal.setText(String.valueOf(lista.size()));
        long conductores = lista.stream().filter(p -> "CONDUCTOR".equals(p.getTipo())).count();
        long auxiliares = lista.stream().filter(p -> "AUXILIAR".equals(p.getTipo())).count();
        long activos = lista.stream().filter(p -> "ACTIVO".equalsIgnoreCase(p.getEstado())).count();
        
        lblTotalConductores.setText(String.valueOf(conductores));
        lblTotalAuxiliares.setText(String.valueOf(auxiliares));
        lblActivos.setText(String.valueOf(activos));
    }

    private void filtrar() {
        String busqueda = txtBuscar.getText().toLowerCase().trim();
        String estado = cbEstado.getValue();
        String tipo = cbTipo.getValue();

        List<Personal> filtrados = listaCompleta.stream().filter(p -> {
            boolean coincideBusqueda = busqueda.isEmpty() || 
                                     p.getNombre().toLowerCase().contains(busqueda) ||
                                     p.getIdentificacion().toLowerCase().contains(busqueda);
            
            boolean coincideEstado = "TODOS".equals(estado) || estado.equalsIgnoreCase(p.getEstado());
            boolean coincideTipo = "TODOS".equals(tipo) || tipo.equalsIgnoreCase(p.getTipo());
            
            return coincideBusqueda && coincideEstado && coincideTipo;
        }).collect(Collectors.toList());

        mostrarTarjetas(filtrados);
    }

    private void mostrarTarjetas(List<Personal> lista) {
        flowPersonal.getChildren().clear();
        for (Personal p : lista) {
            flowPersonal.getChildren().add(crearTarjeta(p));
        }
    }

    private VBox crearTarjeta(Personal p) {
        VBox card = new VBox();
        card.getStyleClass().add("card-item");

        // Header
        VBox header = new VBox();
        header.getStyleClass().add("card-header");
        Label lblNombre = new Label(p.getNombre().toUpperCase());
        lblNombre.getStyleClass().add("card-title");
        Label lblTipo = new Label(p.getTipo() + (p.getLicencia().isEmpty() ? "" : " - " + p.getLicencia()));
        lblTipo.getStyleClass().add("card-subtitle");
        header.getChildren().addAll(lblNombre, lblTipo);

        // Body
        VBox body = new VBox();
        body.getStyleClass().add("card-body");
        
        body.getChildren().addAll(
            crearFilaInfo("ESTADO:", p.getEstado()),
            crearFilaInfo("TELÉFONO:", p.getTelefono().isEmpty() ? "N/A" : p.getTelefono()),
            crearFilaInfo("CORREO:", p.getCorreo().isEmpty() ? "N/A" : p.getCorreo())
        );

        if ("AUXILIAR".equals(p.getTipo()) && !p.getEspecialidad().isEmpty()) {
            body.getChildren().add(crearFilaInfo("ESPECIALIDAD:", p.getEspecialidad()));
        }

        // Actions
        HBox actions = new HBox();
        actions.getStyleClass().add("card-actions");
        Button btnEdit = new Button("EDITAR");
        btnEdit.getStyleClass().add("btn-edit-card");
        btnEdit.setOnAction(e -> handleEditar(p));
        
        Button btnDelete = new Button("ELIMINAR");
        btnDelete.getStyleClass().add("btn-delete-card");
        btnDelete.setOnAction(e -> handleEliminar(p));
        
        actions.getChildren().addAll(btnEdit, btnDelete);
        body.getChildren().add(actions);

        card.getChildren().addAll(header, body);
        return card;
    }

    private HBox crearFilaInfo(String label, String value) {
        HBox row = new HBox(5);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("info-label");
        Label val = new Label(value);
        val.getStyleClass().add("info-value");
        row.getChildren().addAll(lbl, val);
        return row;
    }

    @FXML
    private void handleNuevo(ActionEvent event) {
        abrirFormulario(null, "Nuevo Personal");
    }

    private void handleEditar(Personal p) {
        abrirFormulario(p, "Modificar Personal");
    }

    private void abrirFormulario(Personal personal, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/transportesrbl/views/fxml/form_personal.fxml"));
            Parent root = loader.load();
            
            FormPersonalController controller = loader.getController();
            controller.setPersonal(personal);

            Stage stage = new Stage();
            stage.setTitle("Transportes RBL - " + titulo);
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait(); 
            
            cargarDatos();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("No se pudo cargar el formulario de personal.");
            alert.showAndWait();
        }
    }

    private void handleEliminar(Personal p) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Personal");
        alert.setHeaderText("¿Está seguro de eliminar a " + p.getNombre() + "?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (service.eliminar(p)) {
                    cargarDatos();
                }
            }
        });
    }

    @FXML
    private void handleLimpiar(ActionEvent event) {
        txtBuscar.clear();
        cbEstado.setValue("TODOS");
        cbTipo.setValue("TODOS");
        filtrar();
    }
}
