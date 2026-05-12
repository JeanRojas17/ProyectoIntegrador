package com.transportesrbl.controllers;

import com.transportesrbl.dao.ReporteDAO;
import com.transportesrbl.models.Reporte;
import com.transportesrbl.models.ReporteDetalle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResumenAuxiliarController {

    @FXML private Label lblTotalCargado;
    @FXML private Label lblEficiencia;
    @FXML private Label lblIncidencias;

    @FXML private TableView<ReporteDetalle> tblHistorial;
    @FXML private TableColumn<ReporteDetalle, LocalDateTime> colHora;
    @FXML private TableColumn<ReporteDetalle, String> colActividad;
    @FXML private TableColumn<ReporteDetalle, String> colVehiculo;
    @FXML private TableColumn<ReporteDetalle, String> colEstado;

    private ReporteDAO reporteDAO = new ReporteDAO();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        colHora.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colActividad.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colVehiculo.setCellValueFactory(new PropertyValueFactory<>("camion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Formatear la hora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        colHora.setCellFactory(column -> {
            return new javafx.scene.control.TableCell<ReporteDetalle, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.format(formatter));
                    }
                }
            };
        });
    }

    private void cargarDatos() {
        Reporte reporte = reporteDAO.obtenerDatosReporteGeneral();
        
        // Métricas simuladas basadas en datos reales para el auxiliar
        lblTotalCargado.setText(String.valueOf(reporte.getDetalles().size()));
        lblEficiencia.setText(String.format("%.1f%%", reporte.getTasaExito()));
        
        long incidencias = reporte.getDetalles().stream()
                .filter(d -> "No entregado".equalsIgnoreCase(d.getEstado()) || "Devuelto".equalsIgnoreCase(d.getEstado()))
                .count();
        lblIncidencias.setText(String.format("%02d", incidencias));

        ObservableList<ReporteDetalle> data = FXCollections.observableArrayList(reporte.getDetalles());
        tblHistorial.setItems(data);
    }
}
