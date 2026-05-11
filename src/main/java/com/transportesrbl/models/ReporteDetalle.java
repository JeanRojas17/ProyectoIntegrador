package com.transportesrbl.models;

import java.time.LocalDateTime;

public class ReporteDetalle {
    private int idEntrega;
    private LocalDateTime fecha;
    private String camion;
    private String ruta;
    private String producto;
    private double volumen;
    private double tiempo;
    private String estado;
    private String tiempoEstimado;
    private String avanceRuta;

    public ReporteDetalle(LocalDateTime fecha, String camion, String ruta, String producto, double volumen, double tiempo, String estado) {
        this(0, fecha, camion, ruta, producto, volumen, tiempo, estado);
    }

    public ReporteDetalle(int idEntrega, LocalDateTime fecha, String camion, String ruta, String producto, double volumen, double tiempo, String estado) {
        this.idEntrega = idEntrega;
        this.fecha = fecha;
        this.camion = camion;
        this.ruta = ruta;
        this.producto = producto;
        this.volumen = volumen;
        this.tiempo = tiempo;
        this.estado = estado;
        this.tiempoEstimado = tiempo > 0 ? String.format("%.1f hrs", tiempo) : "Sin calculo";
        this.avanceRuta = "0%";
    }

    public int getIdEntrega() { return idEntrega; }
    public LocalDateTime getFecha() { return fecha; }
    public String getCamion() { return camion; }
    public String getRuta() { return ruta; }
    public String getProducto() { return producto; }
    public double getVolumen() { return volumen; }
    public double getTiempo() { return tiempo; }
    public String getEstado() { return estado; }
    public String getTiempoEstimado() { return tiempoEstimado; }
    public String getAvanceRuta() { return avanceRuta; }

    public void aplicarSeguimiento(int etaMinutos, double progreso, boolean usaUbicacionReal) {
        int avance = (int) Math.round(progreso * 100);
        String origen = usaUbicacionReal ? "GPS" : "estimado";
        String estadoNormalizado = estado == null ? "" : estado.toLowerCase();

        avanceRuta = avance + "%";

        if (estadoNormalizado.contains("entregado") && tiempo > 0) {
            tiempoEstimado = String.format("%.1f hrs", tiempo);
            avanceRuta = "100%";
            return;
        }

        if (estadoNormalizado.contains("no entregado") || estadoNormalizado.contains("cancelado")) {
            tiempo = etaMinutos / 60.0;
            tiempoEstimado = "Ruta detenida";
            return;
        }

        tiempo = etaMinutos / 60.0;
        if (etaMinutos <= 0) {
            tiempoEstimado = "Llegada cercana";
        } else if (estadoNormalizado.contains("pendiente")) {
            tiempoEstimado = etaMinutos + " min aprox.";
        } else {
            tiempoEstimado = "ETA " + etaMinutos + " min (" + origen + ")";
        }
    }
}