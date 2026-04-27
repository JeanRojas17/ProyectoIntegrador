package com.transportesrbl.models;

public class Usuario {
    private int id;
    private String nombreCompleto;
    private String rol;  
    private String usuario;
    private String contrasena;

    public Usuario(int id, String nombreCompleto, String rol, String usuario, String contrasena) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }
    
}