package com.transportesrbl.services;

import com.transportesrbl.dao.ReporteDAO;
import com.transportesrbl.models.Reporte;

public class ReporteService {
    private final ReporteDAO dao = new ReporteDAO();

    public Reporte obtenerReporteGeneral() {
        return dao.obtenerDatosReporteGeneral();
    }
}
