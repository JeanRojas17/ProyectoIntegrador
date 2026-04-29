package com.transportesrbl.services;


import com.transportesrbl.dao.AsignacionDAO;
import com.transportesrbl.models.Asignacion;

public class AsignacionService {
    private AsignacionDAO dao = new AsignacionDAO();

    public boolean crearNuevaAsignacion(Asignacion a) {
        // Aquí podrías poner reglas de negocio (ej. no asignar si el camión está en mantenimiento)
        if (a.getProducto() == null || a.getProducto().isEmpty()) return false;
        return dao.insertar(a);
    }
}