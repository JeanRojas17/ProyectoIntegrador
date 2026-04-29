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

    // ... Mantén tus getters y setters actuales ...
    public int getId() { return id; }
    public String getCamion() { return camion; }
    public String getConductor() { return conductor; }
    public String getRuta() { return ruta; }
    public String getProducto() { return producto; }
    public String getEstado() { return estado; }
}