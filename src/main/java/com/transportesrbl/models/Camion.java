package com.transportesrbl.models;

public class Camion {
    private int id;
    private String modelo;
    private double capacidad;
    private String estado;

    public Camion(int id, String modelo, double capacidad, String estado) {
        this.id = id;
        this.modelo = modelo;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    // Getters (Importantes para la TableView)
    public int getId() { return id; }
    public String getModelo() { return modelo; }
    public double getCapacidad() { return capacidad; }
    public String getEstado() { return estado; }
}