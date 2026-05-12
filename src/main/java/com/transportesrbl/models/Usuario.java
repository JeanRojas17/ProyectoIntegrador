package com.transportesrbl.models;

public class Usuario {

    private int id;
    private String nombre;
    private String usuario;
    private String contrasena;
    private int idRol;
    private String rolNombre;

    public Usuario(int id, String nombre, String usuario, String contrasena, int idRol, String rolNombre) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.idRol = idRol;
        this.rolNombre = rolNombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public int getIdRol() {
        return idRol;
    }

    public String getRol() {
        return rolNombre;
    }
}