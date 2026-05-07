package com.transportesrbl.services;

import com.transportesrbl.dao.PersonalDAO;
import com.transportesrbl.models.Personal;

import java.util.List;

public class PersonalService {
    private final PersonalDAO dao = new PersonalDAO();

    public List<Personal> obtenerTodo() {
        return dao.listarTodo();
    }

    public boolean guardar(Personal p) {
        if ("CONDUCTOR".equals(p.getTipo())) {
            if (p.getId() > 0) {
                return dao.actualizarConductor(p);
            } else {
                return dao.insertarConductor(p);
            }
        } else if ("AUXILIAR".equals(p.getTipo())) {
            if (p.getId() > 0) {
                return dao.actualizarAuxiliar(p);
            } else {
                return dao.insertarAuxiliar(p);
            }
        }
        return false;
    }

    public boolean eliminar(Personal p) {
        if ("CONDUCTOR".equals(p.getTipo())) {
            return dao.eliminarConductor(p.getId());
        } else if ("AUXILIAR".equals(p.getTipo())) {
            return dao.eliminarAuxiliar(p.getId());
        }
        return false;
    }
}
