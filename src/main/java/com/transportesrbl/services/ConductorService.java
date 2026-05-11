package com.transportesrbl.services;

import com.transportesrbl.dao.ConductorDAO;
import java.util.Map;

public class ConductorService {

    private final ConductorDAO conductorDAO = new ConductorDAO();

    public Map<String, Object> getDashboardData(int idUsuario) {
        return conductorDAO.obtenerDatosDashboard(idUsuario);
    }

    public int obtenerIdConductor(int idUsuario) {
        return conductorDAO.obtenerIdConductor(idUsuario);
    }

    public java.util.List<com.transportesrbl.models.Asignacion> obtenerRutas(int idConductor) {
        return new com.transportesrbl.dao.AsignacionDAO().listarPorConductor(idConductor);
    }

    public boolean finalizarEntrega(int idEntrega, String observacion) {
        return conductorDAO.finalizarEntrega(idEntrega, observacion);
    }
}