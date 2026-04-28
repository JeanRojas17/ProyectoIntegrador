package com.transportesrbl.models;

public class Camion {
    private String modelo;
    private double capacidad;
    private String estado;

    public Camion(String modelo, double capacidad, String estado) {
        this.modelo = modelo;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    // Getters (Importantes para la TableView)
    public String getModelo() { return modelo; }
    public double getCapacidad() { return capacidad; }
    public String getEstado() { return estado; }
}