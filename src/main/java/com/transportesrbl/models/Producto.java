package com.transportesrbl.models;

public class Producto {
    private int idProducto;
    private String nombreProducto;
    private String proveedor;
    private String cliente;
    private double volumen;
    private String destino;
    private String estado;

    public Producto(int idProducto, String nombreProducto, String proveedor, String cliente, double volumen, String destino, String estado) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.proveedor = proveedor;
        this.cliente = cliente;
        this.volumen = volumen;
        this.destino = destino;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public double getVolumen() { return volumen; }
    public void setVolumen(double volumen) { this.volumen = volumen; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}