package com.transportesrbl.models;


public class Entrega {
    private int id;
    private String destino;
    private String estado;

    public Entrega(int id, String destino, String estado) {
        this.id = id;
        this.destino = destino;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getDestino() { return destino; }
    public String getEstado() { return estado; }
} 

    

