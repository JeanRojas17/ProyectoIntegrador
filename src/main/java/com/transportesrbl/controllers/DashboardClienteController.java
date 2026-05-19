package com.transportesrbl.controllers;

import com.transportesrbl.dao.ClienteDAO;
import com.transportesrbl.models.SesionUsuario;
import com.transportesrbl.models.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DashboardClienteController {

    @FXML private Label lblContacto;
    @FXML private Label lblEmpresa;
    @FXML private Label lblPaquetesCamino;
    @FXML private Label lblEntregadosExito;
    @FXML private Label lblVolumenTotal;
    @FXML private Label lblFecha;
    @FXML private VBox containerPaquetes;
    @FXML private VBox containerNovedades;
    @FXML private StackPane contentArea;
    @FXML private Button btnPedidos, btnResumen, btnNovedades;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private com.transportesrbl.models.Cliente clienteActivo;
    private Node viewPedidos; // Guardar la vista inicial

    @FXML
    public void initialize() {
        viewPedidos = contentArea.getChildren().get(0); // El VBox inicial
        if (lblFecha != null) {
            java.time.LocalDate now = java.time.LocalDate.now();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy", new java.util.Locale("es", "ES"));
            lblFecha.setText(now.format(formatter));
        }
        Usuario user = SesionUsuario.getInstancia().getUsuarioActivo();
        if (user != null) {
            clienteActivo = clienteDAO.getClienteByUsuarioId(user.getId());
            
            if (clienteActivo != null) {
                lblContacto.setText(clienteActivo.getContacto());
                lblEmpresa.setText(clienteActivo.getNombreEmpresa().toUpperCase());
                cargarDatos();
            } else {
                lblContacto.setText(user.getNombre());
                lblEmpresa.setText("SIN EMPRESA VINCULADA");
            }
        }
    }

    @FXML
    private void mostrarSeccionPedidos(ActionEvent event) {
        contentArea.getChildren().setAll(viewPedidos);
        actualizarEstiloBotones(btnPedidos);
        cargarDatos();
    }

    @FXML
    private void mostrarSeccionResumen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/transportesrbl/views/fxml/resumen_cliente.fxml"));
            Node view = loader.load();
            contentArea.getChildren().setAll(view);
            actualizarEstiloBotones(btnResumen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void mostrarSeccionNovedades(ActionEvent event) {
        // Por ahora se mantiene la vista de pedidos ya que las novedades están en el panel derecho
        // Pero podríamos cargar una vista dedicada si se requiere
        mostrarSeccionPedidos(event);
        actualizarEstiloBotones(btnNovedades);
    }

    private void actualizarEstiloBotones(Button activo) {
        btnPedidos.getStyleClass().removeAll("menu-button-active");
        btnResumen.getStyleClass().removeAll("menu-button-active");
        btnNovedades.getStyleClass().removeAll("menu-button-active");
        
        btnPedidos.getStyleClass().add("menu-button");
        btnResumen.getStyleClass().add("menu-button");
        btnNovedades.getStyleClass().add("menu-button");
        
        activo.getStyleClass().remove("menu-button");
        activo.getStyleClass().add("menu-button-active");
    }

    private void cargarDatos() {
        if (clienteActivo == null) return;
        int id = clienteActivo.getIdCliente();

        // Métricas
        Map<String, Object> metricas = clienteDAO.getMetricas(id);
        lblPaquetesCamino.setText(String.valueOf(metricas.getOrDefault("en_camino", 0)));
        lblEntregadosExito.setText(String.valueOf(metricas.getOrDefault("entregados", 0)));
        lblVolumenTotal.setText(String.format("%.2f", (Double) metricas.getOrDefault("volumen_total", 0.0)));

        // Pedidos
        containerPaquetes.getChildren().clear();
        List<Map<String, Object>> paquetes = clienteDAO.getPaquetesActivos(id);
        for (Map<String, Object> pkg : paquetes) {
            containerPaquetes.getChildren().add(crearTarjetaPaquete(pkg));
        }

        // Novedades
        containerNovedades.getChildren().clear();
        List<String> novedades = clienteDAO.getNovedades(id);
        if (novedades.isEmpty()) {
            Label empty = new Label("No hay novedades registradas.");
            empty.setStyle("-fx-text-fill: #adb5bd; -fx-font-size: 12px;");
            containerNovedades.getChildren().add(empty);
        } else {
            for (String nov : novedades) {
                containerNovedades.getChildren().add(crearItemNovedad(nov));
            }
        }
    }

    private Node crearTarjetaPaquete(Map<String, Object> pkg) {
        VBox card = new VBox(15);
        card.getStyleClass().add("pkg-card");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label id = new Label("GUÍA: " + pkg.get("nro_paquete"));
        id.getStyleClass().add("pkg-id");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        String estado = (String) pkg.get("estado");
        Label status = new Label(estado.toUpperCase());
        status.getStyleClass().add("status-badge");
        
        switch (estado.toLowerCase()) {
            case "pendiente": status.getStyleClass().add("status-pendiente"); break;
            case "en reparto":
            case "siguiente": status.getStyleClass().add("status-reparto"); break;
            case "entregado": status.getStyleClass().add("status-entregado"); break;
            case "cancelado": status.getStyleClass().add("status-cancelado"); break;
        }
        
        header.getChildren().addAll(id, spacer, status);

        // Body
        VBox body = new VBox(5);
        Label desc = new Label((String) pkg.get("descripcion"));
        desc.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #2c3e50;");
        
        HBox details = new HBox(20);
        details.getChildren().add(crearDetalle("Volumen", pkg.get("volumen") + " m³"));
        // Peso no está en SQL, mostramos N/A o valor fijo si es necesario
        details.getChildren().add(crearDetalle("Peso", "N/A")); 
        
        Label dir = new Label("Direccion: " + pkg.get("direccion"));
        dir.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 12px;");
        
        body.getChildren().addAll(desc, details, dir);

        card.getChildren().addAll(header, body);
        return card;
    }

    private Node crearDetalle(String label, String val) {
        VBox vb = new VBox(2);
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: #adb5bd; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label v = new Label(val);
        v.setStyle("-fx-text-fill: #495057; -fx-font-size: 13px; -fx-font-weight: bold;");
        vb.getChildren().addAll(l, v);
        return vb;
    }

    private Node crearItemNovedad(String text) {
        VBox item = new VBox();
        item.getStyleClass().add("notif-item");
        Label lbl = new Label(text);
        lbl.getStyleClass().add("notif-text");
        lbl.setWrapText(true);
        item.getChildren().add(lbl);
        return item;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            SesionUsuario.getInstancia().setUsuarioActivo(null);
            Parent root = FXMLLoader.load(getClass().getResource("/com/transportesrbl/views/fxml/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Transportes RBL - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
