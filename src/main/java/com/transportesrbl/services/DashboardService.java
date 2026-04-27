package com.transportesrbl.services;

import com.transportesrbl.dao.DashboardDAO;
import com.transportesrbl.models.Entrega;
import java.util.List;

public class DashboardService {
    private final DashboardDAO dao = new DashboardDAO();

    public List<Entrega> listarEntregas() {
        return dao.obtenerEntregasRecientes();
    }
}