package com.transportesrbl.models;

import java.util.List;
import java.util.Map;

public class Reporte {
    private int totalEntregas;
    private double tasaExito;
    private double tiempoPromedio;
    private double volumenTotal;
    private Map<String, Integer> entregasPorSemana;
    private List<ReporteDetalle> detalles;

    public Reporte(int totalEntregas, double tasaExito, double tiempoPromedio, double volumenTotal, 
                   Map<String, Integer> entregasPorSemana, List<ReporteDetalle> detalles) {
        this.totalEntregas = totalEntregas;
        this.tasaExito = tasaExito;
        this.tiempoPromedio = tiempoPromedio;
        this.volumenTotal = volumenTotal;
        this.entregasPorSemana = entregasPorSemana;
        this.detalles = detalles;
    }

    public int getTotalEntregas() { return totalEntregas; }
    public double getTasaExito() { return tasaExito; }
    public double getTiempoPromedio() { return tiempoPromedio; }
    public double getVolumenTotal() { return volumenTotal; }
    public Map<String, Integer> getEntregasPorSemana() { return entregasPorSemana; }
    public List<ReporteDetalle> getDetalles() { return detalles; }
}
