package com.transportesrbl.models;

public class Producto {
    private int idProducto;
    private int clienteId;
    private String nombre;
    private String proveedor;
    private String cliente;
    private double volumenUnitario;
    private int cantidad;
    private double volumenTotal;
    private String estado;
    private String destino;

    // Constructor completo de 10 parámetros (incluye clienteId) para mapear proveedor y cliente
    public Producto(int idProducto, int clienteId, String nombre, String proveedor, String cliente, double volumenUnitario, int cantidad, double volumenTotal, String estado, String destino) {
        this.idProducto = idProducto;
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.proveedor = proveedor;
        this.cliente = cliente;
        this.volumenUnitario = volumenUnitario;
        this.cantidad = cantidad;
        this.volumenTotal = volumenTotal;
        this.estado = estado;
        this.destino = destino;
    }

    // Constructor completo de 9 parámetros (necesario para mapear proveedor y cliente sin clienteId)
    public Producto(int idProducto, String nombre, String proveedor, String cliente, double volumenUnitario, int cantidad, double volumenTotal, String estado, String destino) {
        this(idProducto, -1, nombre, proveedor, cliente, volumenUnitario, cantidad, volumenTotal, estado, destino);
    }

    // Constructor de 7 parámetros 
    public Producto(int idProducto, String nombre, double volumenUnitario, int cantidad, double volumenTotal, String estado, String destino) {
        this(idProducto, -1, nombre, "Sin Proveedor", "Sin Cliente", volumenUnitario, cantidad, volumenTotal, estado, destino);
    }

    // Constructor de 6 parámetros
    public Producto(String nombre, double volumenUnitario, int cantidad, double volumenTotal, String estado, String destino) {
        this(-1, nombre, "Sin Proveedor", "Sin Cliente", volumenUnitario, cantidad, volumenTotal, estado, destino);
    }

    // Getters requeridos por las TableColumn y PropertyValueFactory
    public int getIdProducto() { return idProducto; }

    public String getNombreProducto() { return nombre; }

    public String getProveedor() { return proveedor; }

    public String getCliente() { return cliente; }
    public int getClienteId() { return clienteId; }

    // Utilizado por el controlador para obtener el volumen de los indicadores
    public double getVolumen() { return volumenUnitario; }

    public double getVolumenUnitario() { return volumenUnitario; }

    public int getCantidad() { return cantidad; }

    public double getVolumenTotal() { return volumenTotal; }

    public String getEstado() { return estado; }

    public String getDestino() { return destino; }

    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
}
