package com.transportesrbl.models;

public class MetricasDashboard {
    
    private int entregasActivas;
    private int entregasCompletas;
    private int productosPendientes;
    private String camionesDisponibles;

    public MetricasDashboard(int activas, int completas, int pendientes, String camiones) {
        this.entregasActivas = activas;
        this.entregasCompletas = completas;
        this.productosPendientes = pendientes;
        this.camionesDisponibles = camiones;
    }

    public int getEntregasActivas() {
        return entregasActivas;
    }
    public int getEntregasCompletas() {
        return entregasCompletas;
    }
    public int getProductosPendientes() {
        return productosPendientes;
    }
    public String getCamionesDisponibles() {
        return camionesDisponibles;
    }
}