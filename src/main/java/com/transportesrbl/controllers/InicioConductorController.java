package com.transportesrbl.controllers;

import com.transportesrbl.models.SesionUsuario;
import com.transportesrbl.models.Usuario;
import com.transportesrbl.services.ConductorService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.event.ActionEvent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class InicioConductorController {

    @FXML private Label lblSaludo;
    @FXML private Label lblFecha;
    @FXML private Label lblProducto;
    @FXML private Label lblDireccion;
    @FXML private Label lblVolumen;
    @FXML private Label lblPeso;
    @FXML private Label lblCliente;
    @FXML private Label lblEntregasCompletadas;
    @FXML private Label lblTotalEntregas;
    @FXML private ProgressBar progressJornada;
    @FXML private Label lblPendientes;
    @FXML private Label lblPlaca;
    @FXML private Label lblModeloVehiculo;
    @FXML private Label lblEstadoVehiculo;

    private final ConductorService conductorService = new ConductorService();
    private int idEntregaActual = -1;

    @FXML
    public void initialize() {
        Usuario user = SesionUsuario.getInstancia().getUsuarioActivo();
        if (user != null) {
            cargarDatosDashboard(user.getId());
            lblSaludo.setText("BIENVENIDO, " + user.getNombre().split(" ")[0].toUpperCase());
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
            lblFecha.setText(LocalDate.now().format(formatter));
        }
    }

    private void cargarDatosDashboard(int idUsuario) {
        Map<String, Object> datos = conductorService.getDashboardData(idUsuario);

        if (datos.containsKey("producto")) {
            lblProducto.setText(datos.get("producto").toString());
            lblDireccion.setText(datos.get("direccion").toString());
            lblCliente.setText(datos.get("cliente").toString());
            lblVolumen.setText(datos.get("volumen").toString());
            lblPeso.setText("N/A");
            idEntregaActual = (int) datos.get("idEntrega");
        } else {
            lblProducto.setText("SIN ENTREGAS PENDIENTES");
            lblDireccion.setText("-");
            lblCliente.setText("-");
            lblVolumen.setText("-");
            lblPeso.setText("-");
        }

        if (datos.containsKey("vehiculo")) {
            lblModeloVehiculo.setText(datos.get("vehiculo").toString().toUpperCase());
            lblPlaca.setText("VEHICULO ASIGNADO");
        } else {
            lblModeloVehiculo.setText("SIN VEHICULO");
            lblPlaca.setText("-");
        }

        if (datos.containsKey("estadoVehiculo")) {
            String estado = datos.get("estadoVehiculo").toString().toUpperCase();
            lblEstadoVehiculo.setText("ESTADO: " + estado);
            if ("DISPONIBLE".equals(estado) || "OPERATIVO".equals(estado)) {
                lblEstadoVehiculo.getStyleClass().setAll("status-tag-green");
            } else {
                lblEstadoVehiculo.getStyleClass().setAll("status-tag-red");
            }
        }

        if (datos.containsKey("totalEntregas")) {
            int total = (int) datos.get("totalEntregas");
            int completas = (int) datos.get("completadas");
            lblEntregasCompletadas.setText(String.valueOf(completas));
            lblTotalEntregas.setText("/ " + total);
            
            if (total > 0) {
                double progreso = (double) completas / total;
                progressJornada.setProgress(progreso);
                int pendientes = total - completas;
                lblPendientes.setText(pendientes + " entregas pendientes para finalizar");
            } else {
                progressJornada.setProgress(0);
                lblPendientes.setText("No hay entregas asignadas para hoy");
            }
        }
    }

    @FXML
    private void handleFinalizarEntrega(ActionEvent event) {
        if (idEntregaActual != -1) {
            boolean ok = conductorService.finalizarEntrega(idEntregaActual, "Entrega exitosa");
            if (ok) {
                mostrarAlerta("Éxito", "Entrega finalizada correctamente", Alert.AlertType.INFORMATION);
                cargarDatosDashboard(SesionUsuario.getInstancia().getUsuarioActivo().getId());
            } else {
                mostrarAlerta("Error", "No se pudo finalizar la entrega", Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Aviso", "No hay ninguna entrega activa para finalizar", Alert.AlertType.WARNING);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}