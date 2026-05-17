package com.transportesrbl.controllers;

import com.transportesrbl.dao.ClienteDAO;
import com.transportesrbl.models.SesionUsuario;
import com.transportesrbl.models.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import java.util.List;
import java.util.Map;

public class ResumenClienteController {

    @FXML private Label lblTotalPedidos;
    @FXML private Label lblTasaExito;
    @FXML private Label lblNovedades;

    @FXML private TableView<Map<String, Object>> tblHistorial;
    @FXML private TableColumn<Map<String, Object>, String> colGuia;
    @FXML private TableColumn<Map<String, Object>, String> colDescripcion;
    @FXML private TableColumn<Map<String, Object>, Double> colVolumen;
    @FXML private TableColumn<Map<String, Object>, String> colDestino;
    @FXML private TableColumn<Map<String, Object>, String> colEstado;

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        colGuia.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String) data.getValue().get("nro_paquete")));
        colDescripcion.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String) data.getValue().get("descripcion")));
        colVolumen.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>((Double) data.getValue().get("volumen")));
        colDestino.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String) data.getValue().get("direccion")));
        colEstado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String) data.getValue().get("estado")));
    }

    private void cargarDatos() {
        Usuario user = SesionUsuario.getInstancia().getUsuarioActivo();
        if (user == null) return;

        com.transportesrbl.models.Cliente cliente = clienteDAO.getClienteByUsuarioId(user.getId());
        if (cliente == null) return;

        List<Map<String, Object>> paquetes = clienteDAO.getPaquetesActivos(cliente.getIdCliente());
        tblHistorial.setItems(FXCollections.observableArrayList(paquetes));

        // Cálculos para el resumen
        int total = paquetes.size();
        long entregados = paquetes.stream()
                .filter(p -> "Entregado".equalsIgnoreCase((String) p.get("estado")))
                .count();
        long novedades = clienteDAO.getNovedades(cliente.getIdCliente()).size();

        lblTotalPedidos.setText(String.valueOf(total));
        lblNovedades.setText(String.valueOf(novedades));
        
        double tasa = total > 0 ? (double) entregados / total * 100 : 0;
        lblTasaExito.setText(String.format("%.1f%%", tasa));
    }
}
