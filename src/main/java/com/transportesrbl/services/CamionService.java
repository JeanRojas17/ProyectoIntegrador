package com.transportesrbl.services;

import com.transportesrbl.dao.CamionDAO;
import com.transportesrbl.models.Camion;

import java.util.List;

public class CamionService {
    private final CamionDAO camionDAO = new CamionDAO();

    public List<Camion> obtenerTodos() {
        return camionDAO.listar();
    }

    public boolean registrar(Camion camion) {
        return camionDAO.insertar(camion);
    }

    public boolean modificar(Camion camion) {
        return camionDAO.actualizar(camion);
    }

    public boolean eliminar(int id) {
        return camionDAO.eliminar(id);
    }
}