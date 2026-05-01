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

    public Asignacion(String camion, String conductor, String ruta, String producto, String estado) {
        this.camion = camion;
        this.conductor = conductor;
        this.ruta = ruta;
        this.producto = producto;
        this.estado = estado;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getAsignacionId() {
        return asignacionId;
    }

    public void setAsignacionId(Integer asignacionId) {
        this.asignacionId = asignacionId;
    }

    public Integer getCamionId() {
        return camionId;
    }

    public void setCamionId(Integer camionId) {
        this.camionId = camionId;
    }

    public Integer getConductorId() {
        return conductorId;
    }
    
    public void setConductorId(Integer conductorId) {
        this.conductorId = conductorId;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getCamion() {
        return camion;
    }

    public void setCamion(String camion) {
        this.camion = camion;
    }

    public String getConductor() {
        return conductor;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}