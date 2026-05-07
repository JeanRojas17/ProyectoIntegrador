package com.transportesrbl.controllers;

import com.transportesrbl.models.Reporte;
import com.transportesrbl.models.ReporteDetalle;
import com.transportesrbl.services.ReporteService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.Map;

public class ReportesController {

    @FXML private BarChart<String, Number> barChartEntregas;
    @FXML private TableView<ReporteDetalle> tblDetallesReporte;
    @FXML private TableColumn<ReporteDetalle, LocalDateTime> colFecha;
    @FXML private TableColumn<ReporteDetalle, String> colCamion, colRuta, colProducto, colEstado;
    @FXML private TableColumn<ReporteDetalle, Double> colVolumen, colTiempo;
    @FXML private Label lblTotalEntregas, lblTasaExito, lblTiempoPromedio, lblVolumenTotal;

    private final ReporteService service = new ReporteService();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCamion.setCellValueFactory(new PropertyValueFactory<>("camion"));
        colRuta.setCellValueFactory(new PropertyValueFactory<>("ruta"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("producto"));
        colVolumen.setCellValueFactory(new PropertyValueFactory<>("volumen"));
        colTiempo.setCellValueFactory(new PropertyValueFactory<>("tiempo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void cargarDatos() {
        Reporte reporte = service.obtenerReporteGeneral();
        
        lblTotalEntregas.setText(String.valueOf(reporte.getTotalEntregas()));
        lblTasaExito.setText(String.format("%.1f%%", reporte.getTasaExito()));
        lblTiempoPromedio.setText(String.format("%.1f hrs", reporte.getTiempoPromedio()));
        lblVolumenTotal.setText(String.format("%.1f m³", reporte.getVolumenTotal()));

        barChartEntregas.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Entregas por Semana");

        Map<String, Integer> datosGrafico = reporte.getEntregasPorSemana();
        for (Map.Entry<String, Integer> entry : datosGrafico.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChartEntregas.getData().add(series);
        tblDetallesReporte.setItems(FXCollections.observableArrayList(reporte.getDetalles()));
    }

    @FXML
    private void handleExportarExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("Reporte_Entregas_" + System.currentTimeMillis() + ".xlsx");
        
        File file = fileChooser.showSaveDialog(tblDetallesReporte.getScene().getWindow());
        
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Entregas");
                Row headerRow = sheet.createRow(0);
                String[] columns = {"Fecha", "Camion", "Ruta", "Producto", "Volumen", "Tiempo", "Estado"};
                for (int i = 0; i < columns.length; i++) {
                    headerRow.createCell(i).setCellValue(columns[i]);
                }

                int rowNum = 1;
                for (ReporteDetalle d : tblDetallesReporte.getItems()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(d.getFecha().toString());
                    row.createCell(1).setCellValue(d.getCamion());
                    row.createCell(2).setCellValue(d.getRuta());
                    row.createCell(3).setCellValue(d.getProducto());
                    row.createCell(4).setCellValue(d.getVolumen());
                    row.createCell(5).setCellValue(d.getTiempo());
                    row.createCell(6).setCellValue(d.getEstado());
                }

                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                mostrarAlerta("Éxito", "Excel generado correctamente.");
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo generar el Excel.");
            }
        }
    }

    @FXML
    private void handleExportarPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("Reporte_Entregas_" + System.currentTimeMillis() + ".pdf");
        
        File file = fileChooser.showSaveDialog(tblDetallesReporte.getScene().getWindow());
        
        if (file != null) {
            try {
                PdfWriter writer = new PdfWriter(file);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                document.add(new Paragraph("REPORTE DE ENTREGAS - TRANSPORTES RBL").setBold().setFontSize(18));
                document.add(new Paragraph("Resumen General:"));
                document.add(new Paragraph("Total Entregas: " + lblTotalEntregas.getText()));
                document.add(new Paragraph("Tasa de Éxito: " + lblTasaExito.getText()));
                document.add(new Paragraph("\nDetalle de Operaciones:"));

                Table table = new Table(UnitValue.createPercentArray(new float[]{20, 15, 15, 20, 10, 10, 10}));
                table.setWidth(UnitValue.createPercentValue(100));
                
                table.addHeaderCell("Fecha");
                table.addHeaderCell("Camion");
                table.addHeaderCell("Ruta");
                table.addHeaderCell("Producto");
                table.addHeaderCell("Vol.");
                table.addHeaderCell("Tiempo");
                table.addHeaderCell("Estado");

                for (ReporteDetalle d : tblDetallesReporte.getItems()) {
                    table.addCell(d.getFecha().toString());
                    table.addCell(d.getCamion());
                    table.addCell(d.getRuta());
                    table.addCell(d.getProducto());
                    table.addCell(String.valueOf(d.getVolumen()));
                    table.addCell(String.format("%.1f", d.getTiempo()));
                    table.addCell(d.getEstado());
                }

                document.add(table);
                document.close();
                mostrarAlerta("Éxito", "PDF generado correctamente.");
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo generar el PDF.");
            }
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    @FXML
    private void handleBusqueda() {
        cargarDatos();
    }
}
