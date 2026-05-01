package com.transportesrbl.models;

public class Usuario {
    private int id;
    private String nombre;
    private String usuario;
    private String contrasena;
    private int idRol;

    public Usuario(int id, String nombre, String usuario, String contrasena, int idRol) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.idRol = idRol;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUsuario() { return usuario; }
    public String getContrasena() { return contrasena; }
    public int getIdRol() { return idRol; }

    // Retorna el nombre del rol según su ID en la base de datos
    public String getRol() {
        switch (this.idRol) {
            case 1: return "Administrador";
            case 2: return "Operador";
            case 3: return "Supervisor";
            default: return "Desconocido";
        }
    }
}