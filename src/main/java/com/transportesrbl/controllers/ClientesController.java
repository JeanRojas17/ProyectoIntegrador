package com.transportesrbl.controllers;

import com.transportesrbl.models.Cliente;
import com.transportesrbl.services.ClienteService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;
import java.util.stream.Collectors;

public class ClientesController {

    @FXML private Label lblTotalClientes;
    @FXML private TextField txtBuscar;
    @FXML private FlowPane flowClientes;

    private final ClienteService service = new ClienteService();
    private List<Cliente> listaCompleta;

    @FXML
    public void initialize() {
        cargarDatos();
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> filtrar());
    }

    private void cargarDatos() {
        listaCompleta = service.listar();
        lblTotalClientes.setText(String.valueOf(listaCompleta.size()));
        filtrar();
    }

    private void filtrar() {
        String busqueda = txtBuscar.getText().toLowerCase().trim();

        List<Cliente> filtrados = listaCompleta.stream().filter(c -> {
            return busqueda.isEmpty() || 
                   c.getNombreEmpresa().toLowerCase().contains(busqueda) ||
                   c.getContacto().toLowerCase().contains(busqueda);
        }).collect(Collectors.toList());

        mostrarTarjetas(filtrados);
    }

    private void mostrarTarjetas(List<Cliente> lista) {
        flowClientes.getChildren().clear();
        for (Cliente c : lista) {
            flowClientes.getChildren().add(crearTarjeta(c));
        }
    }

    private VBox crearTarjeta(Cliente c) {
        VBox card = new VBox();
        card.getStyleClass().add("card-item");

        // Header
        VBox header = new VBox();
        header.getStyleClass().add("card-header");
        Label lblEmpresa = new Label(c.getNombreEmpresa().toUpperCase());
        lblEmpresa.getStyleClass().add("card-title");
        Label lblContacto = new Label("Contacto: " + c.getContacto());
        lblContacto.getStyleClass().add("card-subtitle");
        header.getChildren().addAll(lblEmpresa, lblContacto);

        // Body
        VBox body = new VBox();
        body.getStyleClass().add("card-body");
        
        body.getChildren().addAll(
            crearFilaInfo("ID CLIENTE:", String.valueOf(c.getIdCliente())),
            crearFilaInfo("ID USUARIO:", c.getIdUsuario() != null ? String.valueOf(c.getIdUsuario()) : "N/A")
        );

        // Actions
        HBox actions = new HBox();
        actions.getStyleClass().add("card-actions");
        Button btnEdit = new Button("EDITAR");
        btnEdit.getStyleClass().add("btn-edit-card");
        btnEdit.setOnAction(e -> handleEditar(c));
        
        Button btnDelete = new Button("ELIMINAR");
        btnDelete.getStyleClass().add("btn-delete-card");
        btnDelete.setOnAction(e -> handleEliminar(c));
        
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
        abrirFormulario(null, "Nuevo Cliente");
    }

    private void handleEditar(Cliente c) {
        abrirFormulario(c, "Modificar Cliente");
    }

    private void abrirFormulario(Cliente cliente, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/transportesrbl/views/fxml/form_cliente.fxml"));
            Parent root = loader.load();
            
            FormClienteController controller = loader.getController();
            controller.setCliente(cliente);

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
            alert.setContentText("No se pudo cargar el formulario de cliente.");
            alert.showAndWait();
        }
    }

    private void handleEliminar(Cliente c) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Cliente");
        alert.setHeaderText("¿Está seguro de eliminar a " + c.getNombreEmpresa() + "?");
        alert.setContentText("Esto también eliminará su cuenta de usuario vinculada.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (service.eliminar(c.getIdCliente())) {
                    cargarDatos();
                }
            }
        });
    }

    @FXML
    private void handleLimpiar(ActionEvent event) {
        txtBuscar.clear();
        filtrar();
    }
}
