package com.transportesrbl.services;

import java.util.List;

import com.transportesrbl.dao.DashboardDAO;
import com.transportesrbl.models.Camion;
import com.transportesrbl.models.Entrega;
import com.transportesrbl.models.MetricasDashboard;

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