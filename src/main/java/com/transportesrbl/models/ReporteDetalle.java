package com.transportesrbl.models;

import java.time.LocalDateTime;

public class ReporteDetalle {
    private LocalDateTime fecha;
    private String camion;
    private String ruta;
    private String producto;
    private double volumen;
    private double tiempo;
    private String estado;

    public ReporteDetalle(LocalDateTime fecha, String camion, String ruta, String producto, double volumen, double tiempo, String estado) {
        this.fecha = fecha;
        this.camion = camion;
        this.ruta = ruta;
        this.producto = producto;
        this.volumen = volumen;
        this.tiempo = tiempo;
        this.estado = estado;
    }

    public LocalDateTime getFecha() { return fecha; }
    public String getCamion() { return camion; }
    public String getRuta() { return ruta; }
    public String getProducto() { return producto; }
    public double getVolumen() { return volumen; }
    public double getTiempo() { return tiempo; }
    public String getEstado() { return estado; }
}
