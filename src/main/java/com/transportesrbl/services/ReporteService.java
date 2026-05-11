package com.transportesrbl.services;

import com.transportesrbl.dao.ReporteDAO;
import com.transportesrbl.models.Reporte;
import com.transportesrbl.models.ReporteDetalle;
import com.transportesrbl.models.RutaSeguimiento;

import java.util.Map;
import java.util.stream.Collectors;

public class ReporteService {
    private final ReporteDAO dao = new ReporteDAO();
    private final RutaService rutaService = new RutaService();

    public Reporte obtenerReporteGeneral() {
        Reporte reporte = dao.obtenerDatosReporteGeneral();
        Map<Integer, RutaSeguimiento> rutasPorEntrega = rutaService.listarRutas().stream()
            .collect(Collectors.toMap(RutaSeguimiento::getIdEntrega, ruta -> ruta, (actual, repetida) -> actual));

        double tiempoAcumulado = 0;
        int entregasConTiempo = 0;

        for (ReporteDetalle detalle : reporte.getDetalles()) {
            RutaSeguimiento ruta = rutasPorEntrega.get(detalle.getIdEntrega());
            if (ruta != null) {
                detalle.aplicarSeguimiento(ruta.getEtaMinutos(), ruta.getProgreso(), ruta.isUbicacionReal());
            }
            if (detalle.getTiempo() > 0) {
                tiempoAcumulado += detalle.getTiempo();
                entregasConTiempo++;
            }
        }

        double tiempoPromedioMapa = entregasConTiempo > 0 ? tiempoAcumulado / entregasConTiempo : 0;
        return new Reporte(
            reporte.getTotalEntregas(),
            reporte.getTasaExito(),
            tiempoPromedioMapa,
            reporte.getVolumenTotal(),
            reporte.getEntregasPorSemana(),
            reporte.getDetalles()
        );
    }
}