package com.transportesrbl.models;

public class Entrega {
    private int id;
    private String producto;
    private String direccion;
    private String estado;

    public Entrega(int id, String producto, String direccion, String estado) {
        this.id = id;
        this.producto = producto;
        this.direccion = direccion;
        this.estado = estado;
    }

    // Getters
    public int getId() { return id; }
    public String getProducto() { return producto; }
    public String getDireccion() { return direccion; }
    public String getEstado() { return estado; }
}