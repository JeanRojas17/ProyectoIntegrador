package com.controlador;


import com.dao.UsuarioDao;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMensaje;

    private UsuarioDao usuarioDao = new UsuarioDao();

    @FXML
    public void handleLogin() {
        String usuario = txtUsuario.getText();
        String pass = txtPassword.getText();

        if (usuarioDao.validarLogin(usuario, pass)) {
            lblMensaje.setText("Acceso concedido");
            // Aquí llamarías a la carga del Dashboard
        } else {
            lblMensaje.setText("Credenciales incorrectas");
        }
    }
}