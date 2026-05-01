package com.transportesrbl.services;

import com.transportesrbl.dao.AsignacionDAO;
import com.transportesrbl.models.Asignacion;

public class AsignacionService {
    private AsignacionDAO dao = new AsignacionDAO();

    public boolean crearNuevaAsignacion(Asignacion a) {
        if (a.getProducto() == null || a.getProducto().isEmpty()) return false;
        return dao.insertar(a);
    }

    public java.util.List<Asignacion> filtrarAsignaciones(String producto, String estado, java.time.LocalDate fecha) {
        return dao.buscarConFiltros(producto, estado, fecha);
    }
}