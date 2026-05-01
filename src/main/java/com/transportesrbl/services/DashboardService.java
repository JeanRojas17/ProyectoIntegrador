package com.transportesrbl.services;

import com.transportesrbl.dao.DashboardDAO;
import com.transportesrbl.models.*;

import java.util.List;

public class DashboardService {
    private final DashboardDAO dao = new DashboardDAO();

    public MetricasDashboard obtenerEstadisticas() {
        return dao.obtenerMetricas();
    }

    public List<Entrega> listarEntregas() {
        return dao.obtenerEntregasRecientes();
    }

    public List<Camion> listarFlota() {
        return dao.obtenerEstadoFlota();
    }
}