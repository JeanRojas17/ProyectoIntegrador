package com.transportesrbl.controllers;

import com.transportesrbl.dao.AsignacionDAO;
import com.transportesrbl.models.Asignacion;
import com.transportesrbl.models.SesionUsuario;
import com.transportesrbl.models.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class MisPaquetesController {

    @FXML private Label lblModeloCamion;
    @FXML private Label lblPlacaCamion;
    @FXML private Label lblPendientes;
    @FXML private Label lblVolumenTotal;
    @FXML private TextField txtBuscar;
    @FXML private TilePane containerPaquetes;

    private AsignacionDAO asignacionDAO = new AsignacionDAO();
    private com.transportesrbl.dao.ReporteDAO reporteDAO = new com.transportesrbl.dao.ReporteDAO();
    private List<Asignacion> todasLasAsignaciones;

    @FXML
    public void initialize() {
        cargarDatos();
        
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarPaquetes(newValue);
        });
    }

    private void cargarDatos() {
        todasLasAsignaciones = asignacionDAO.listar();
        
        actualizarUI(todasLasAsignaciones);
        actualizarResumen(todasLasAsignaciones);
    }

    private void actualizarResumen(List<Asignacion> lista) {
        if (!lista.isEmpty() && lista.get(0).getCamion() != null) {
            lblModeloCamion.setText(lista.get(0).getCamion().toUpperCase());
            lblPlacaCamion.setText("[ ASIGNADO ]");
        }
        
        long pendientes = lista.stream()
                .filter(a -> !"Entregado".equalsIgnoreCase(a.getEstado()) && !"Cargado".equalsIgnoreCase(a.getEstado()))
                .count();
        lblPendientes.setText(String.format("%02d", pendientes));
        
        // Calcular volumen real desde ReporteDAO
        double volumen = reporteDAO.obtenerDatosReporteGeneral().getVolumenTotal();
        lblVolumenTotal.setText(String.format("%.1f m³", volumen));
    }

    private void filtrarPaquetes(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            actualizarUI(todasLasAsignaciones);
        } else {
            List<Asignacion> filtrados = todasLasAsignaciones.stream()
                    .filter(a -> a.getProducto().toLowerCase().contains(filtro.toLowerCase()) || 
                                 a.getRuta().toLowerCase().contains(filtro.toLowerCase()))
                    .collect(Collectors.toList());
            actualizarUI(filtrados);
        }
    }

    private void actualizarUI(List<Asignacion> lista) {
        containerPaquetes.getChildren().clear();
        for (Asignacion asig : lista) {
            containerPaquetes.getChildren().add(crearTarjetaPaquete(asig));
        }
    }

    private VBox crearTarjetaPaquete(Asignacion asig) {
        VBox card = new VBox();
        card.getStyleClass().add("pkg-card");
        card.setPrefWidth(350);
        card.setSpacing(10);

        // Header Guía y Estado
        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label lblGuia = new Label("GUÍA: " + (asig.getId())); // Usando ID como guía temporal
        lblGuia.getStyleClass().add("pkg-id");
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        Label lblEstado = new Label(asig.getEstado().toUpperCase());
        lblEstado.getStyleClass().add(asig.getEstado().equalsIgnoreCase("Pendiente") ? "tag-pending" : "tag-ready");
        header.getChildren().addAll(lblGuia, spacer, lblEstado);

        // Nombre Producto
        Label lblNombre = new Label(asig.getProducto());
        lblNombre.getStyleClass().add("pkg-name");

        // Info Grid
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);

        grid.add(crearInfoBox("DESTINO", asig.getRuta()), 0, 0);
        grid.add(crearInfoBox("CONDUCTOR", asig.getConductor()), 1, 0);

        // Botón Acción
        HBox btnContainer = new HBox();
        btnContainer.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));
        Button btnAccion = new Button();
        btnAccion.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnAccion, javafx.scene.layout.Priority.ALWAYS);

        if ("Pendiente".equalsIgnoreCase(asig.getEstado())) {
            btnAccion.setText("MARCAR COMO CARGADO");
            btnAccion.getStyleClass().add("btn-action-deliver");
            btnAccion.setOnAction(e -> actualizarEstado(asig, "Cargado"));
        } else {
            btnAccion.setText("ANULAR CARGA");
            btnAccion.getStyleClass().add("btn-action-load");
            btnAccion.setOnAction(e -> actualizarEstado(asig, "Pendiente"));
        }

        btnContainer.getChildren().add(btnAccion);
        card.getChildren().addAll(header, lblNombre, grid, btnContainer);

        return card;
    }

    private VBox crearInfoBox(String titulo, String valor) {
        VBox box = new VBox();
        Label lblTit = new Label(titulo);
        lblTit.getStyleClass().add("label-muted-small");
        Label lblVal = new Label(valor);
        lblVal.getStyleClass().add("val-text");
        box.getChildren().addAll(lblTit, lblVal);
        return box;
    }

    private void actualizarEstado(Asignacion asig, String nuevoEstado) {
        asig.setEstado(nuevoEstado);
        if (asignacionDAO.actualizar(asig)) {
            cargarDatos(); // Recargar todo para refrescar la UI
        }
    }
}
