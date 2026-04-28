package com.transportesrbl.services;

import com.transportesrbl.dao.DashboardDAO;
import com.transportesrbl.models.MetricasDashboard;
import com.transportesrbl.models.Camion;
import com.transportesrbl.models.Entrega; // Si aún sale rojo, es porque no hemos creado la clase Entrega
import java.util.List;

public class DashboardService {
    private final DashboardDAO dao = new DashboardDAO();

    // Para los contadores de arriba (8, 24, 42, etc.)
    public MetricasDashboard obtenerEstadisticas() {
        return dao.obtenerMetricas();
    }

    // Para la tabla de "Entregas Recientes"
    public List<Entrega> listarEntregas() {
        return dao.obtenerEntregasRecientes();
    }

    public List<Camion> listarFlota() {
    return dao.obtenerEstadoFlota();
}

}