package com.transportesrbl.models;

public class Asignacion {
    private int id;
    private Integer asignacionId;
    private Integer camionId;
    private Integer conductorId;
    private Integer productoId;
    private String camion;
    private String conductor;
    private String ruta;
    private String producto;
    private String estado;

    // Constructor para NUEVAS asignaciones (Sin ID de Asignación)
    public Asignacion(String camion, String conductor, String ruta, String producto, String estado) {
        this.camion = camion;
        this.conductor = conductor;
        this.ruta = ruta;
        this.producto = producto;
        this.estado = estado;
    }

    // Constructor para NUEVAS asignaciones con IDs de artículos seleccionados
    public Asignacion(Integer camionId, Integer conductorId, Integer productoId, String camion, String conductor, String ruta, String producto, String estado) {
        this.camionId = camionId;
        this.conductorId = conductorId;
        this.productoId = productoId;
        this.camion = camion;
        this.conductor = conductor;
        this.ruta = ruta;
        this.producto = producto;
        this.estado = estado;
    }

    // Constructor para CARGAR desde la BD (Con ID de Asignación)
    public Asignacion(int id, Integer asignacionId, Integer camionId, Integer conductorId, Integer productoId, String camion, String conductor, String ruta, String producto, String estado) {
        this.id = id;
        this.asignacionId = asignacionId;
        this.camionId = camionId;
        this.conductorId = conductorId;
        this.productoId = productoId;
        this.camion = camion;
        this.conductor = conductor;
        this.ruta = ruta;
        this.producto = producto;
        this.estado = estado;
    }

    // --- GETTERS ---
    public int getId() { return id; }
    public Integer getAsignacionId() { return asignacionId; }
    public Integer getCamionId() { return camionId; }
    public Integer getConductorId() { return conductorId; }
    public Integer getProductoId() { return productoId; }
    public String getCamion() { return camion; }
    public String getConductor() { return conductor; }
    public String getRuta() { return ruta; }
    public String getProducto() { return producto; }
    public String getEstado() { return estado; }

    // --- SETTERS (Necesarios para Modificar) ---
    public void setId(int id) { this.id = id; }
    public void setAsignacionId(Integer asignacionId) { this.asignacionId = asignacionId; }
    public void setCamionId(Integer camionId) { this.camionId = camionId; }
    public void setConductorId(Integer conductorId) { this.conductorId = conductorId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }
    public void setCamion(String camion) { this.camion = camion; }
    public void setConductor(String conductor) { this.conductor = conductor; }
    public void setRuta(String ruta) { this.ruta = ruta; }
    public void setProducto(String producto) { this.producto = producto; }
    public void setEstado(String estado) { this.estado = estado; }
}