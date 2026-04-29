package com.transportesrbl.models;

public class Asignacion {
    private int id;
    private String camion;
    private String conductor;
    private String ruta;
    private String producto;
    private String estado;

    // Constructor para NUEVAS asignaciones (Sin ID)
    public Asignacion(String camion, String conductor, String ruta, String producto, String estado) {
        this.camion = camion;
        this.conductor = conductor;
        this.ruta = ruta;
        this.producto = producto;
        this.estado = estado;
    }

    // Constructor para CARGAR desde la BD (Con ID)
    public Asignacion(int id, String camion, String conductor, String ruta, String producto, String estado) {
        this.id = id;
        this.camion = camion;
        this.conductor = conductor;
        this.ruta = ruta;
        this.producto = producto;
        this.estado = estado;
    }

    // --- GETTERS ---
    public int getId() { return id; }
    public String getCamion() { return camion; }
    public String getConductor() { return conductor; }
    public String getRuta() { return ruta; }
    public String getProducto() { return producto; }
    public String getEstado() { return estado; }

    // --- SETTERS (Necesarios para Modificar) ---
    public void setId(int id) { this.id = id; }
    public void setCamion(String camion) { this.camion = camion; }
    public void setConductor(String conductor) { this.conductor = conductor; }
    public void setRuta(String ruta) { this.ruta = ruta; } // Este soluciona el error
    public void setProducto(String producto) { this.producto = producto; }
    public void setEstado(String estado) { this.estado = estado; }
}