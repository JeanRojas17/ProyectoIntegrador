package com.transportesrbl.controllers;

import com.transportesrbl.models.Cliente;
import com.transportesrbl.services.ClienteService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class FormClienteController {

    @FXML private Label lblTitulo, lblInfoEdicion;
    @FXML private TextField txtEmpresa, txtContacto, txtUsuario;
    @FXML private PasswordField txtPassword;

    private final ClienteService service = new ClienteService();
    private Cliente cliente;

    public void setCliente(Cliente c) {
        this.cliente = c;
        if (c != null) {
            lblTitulo.setText("MODIFICAR CLIENTE");
            txtEmpresa.setText(c.getNombreEmpresa());
            txtContacto.setText(c.getContacto());
            
            // Deshabilitar campos de cuenta en edición (por simplicidad inicial)
            txtUsuario.setDisable(true);
            txtPassword.setDisable(true);
            lblInfoEdicion.setVisible(true);
            lblInfoEdicion.setManaged(true);
        }
    }

    @FXML
    private void handleGuardar(ActionEvent event) {
        if (!validar()) return;

        if (cliente == null) {
            cliente = new Cliente(0, txtEmpresa.getText(), txtContacto.getText());
        } else {
            cliente.setNombreEmpresa(txtEmpresa.getText());
            cliente.setContacto(txtContacto.getText());
        }

        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        if (service.guardar(cliente, user, pass)) {
            cerrar(event);
        } else {
            mostrarAlerta("Error", "No se pudo guardar el cliente. Verifique los datos o si el usuario ya existe.");
        }
    }

    private boolean validar() {
        if (txtEmpresa.getText().isEmpty() || txtContacto.getText().isEmpty()) {
            mostrarAlerta("Campos Requeridos", "Debe ingresar el nombre de la empresa y el contacto.");
            return false;
        }
        if (cliente == null && (txtUsuario.getText().isEmpty() || txtPassword.getText().isEmpty())) {
            mostrarAlerta("Campos Requeridos", "Para un nuevo cliente debe asignar un usuario y contraseña.");
            return false;
        }
        return true;
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        cerrar(event);
    }

    private void cerrar(ActionEvent event) {
        Stage stage = (Stage) ((Control) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
