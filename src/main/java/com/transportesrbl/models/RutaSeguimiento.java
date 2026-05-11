package com.transportesrbl.models;

public class RutaSeguimiento {

    private int idEntrega;
    private String producto;
    private String cliente;
    private String destino;
    private String estado;
    private String camion;
    private String conductor;
    private String fechaAsignacion;
    private double volumen;
    private double origenLat;
    private double origenLon;
    private double destinoLat;
    private double destinoLon;
    private double actualLat;
    private double actualLon;
    private double progreso;
    private int etaMinutos;
    private String color;
    private boolean ubicacionReal;

    public int getIdEntrega() {
        return idEntrega;
    }

    public void setIdEntrega(int idEntrega) {
        this.idEntrega = idEntrega;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public String getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(String fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public double getVolumen() {
        return volumen;
    }

    public void setVolumen(double volumen) {
        this.volumen = volumen;
    }

    public double getOrigenLat() {
        return origenLat;
    }

    public void setOrigenLat(double origenLat) {
        this.origenLat = origenLat;
    }

    public double getOrigenLon() {
        return origenLon;
    }

    public void setOrigenLon(double origenLon) {
        this.origenLon = origenLon;
    }

    public double getDestinoLat() {
        return destinoLat;
    }

    public void setDestinoLat(double destinoLat) {
        this.destinoLat = destinoLat;
    }

    public double getDestinoLon() {
        return destinoLon;
    }

    public void setDestinoLon(double destinoLon) {
        this.destinoLon = destinoLon;
    }

    public double getActualLat() {
        return actualLat;
    }

    public void setActualLat(double actualLat) {
        this.actualLat = actualLat;
    }

    public double getActualLon() {
        return actualLon;
    }

    public void setActualLon(double actualLon) {
        this.actualLon = actualLon;
    }

    public double getProgreso() {
        return progreso;
    }

    public void setProgreso(double progreso) {
        this.progreso = progreso;
    }

    public int getEtaMinutos() {
        return etaMinutos;
    }

    public void setEtaMinutos(int etaMinutos) {
        this.etaMinutos = etaMinutos;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isUbicacionReal() {
        return ubicacionReal;
    }

    public void setUbicacionReal(boolean ubicacionReal) {
        this.ubicacionReal = ubicacionReal;
    }
}