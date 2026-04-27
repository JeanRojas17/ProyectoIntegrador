package com.transportesrbl.controllers;

import com.transportesrbl.services.DashboardService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {
    private final DashboardService service = new DashboardService();

    @FXML
    private Label lblEntregas;

    @FXML
    public void initialize() {
        // Carga los datos al iniciar la ventana
        int total = service.listarEntregas().size();
        lblEntregas.setText(String.valueOf(total));
    }
}