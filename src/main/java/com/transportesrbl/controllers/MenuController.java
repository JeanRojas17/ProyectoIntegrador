package com.transportesrbl.controllers;

import com.transportesrbl.models.SesionUsuario;
import com.transportesrbl.models.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MenuController {

    @FXML private Label lblNombreUsuario;
    @FXML private Label lblRolUsuario;

    @FXML
    public void initialize() {
        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        Usuario usuario = SesionUsuario.getInstancia().getUsuarioActivo();
        if (usuario != null) {
            lblNombreUsuario.setText(usuario.getNombre());
            
            // Usamos getIdRol() y lo convertimos a String para mostrarlo en la etiqueta
            lblRolUsuario.setText(String.valueOf(usuario.getRol())); 
        } else {
            lblNombreUsuario.setText("Sin sesión");
            lblRolUsuario.setText("Invitado");
        }
    }
}