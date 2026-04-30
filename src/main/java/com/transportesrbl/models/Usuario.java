package com.transportesrbl.models;

public class Usuario {
    private int id;
    private String nombre;
    private String username;
    private String contrasena;
    private int idRol;

    public Usuario(int id, String nombre, String username, String contrasena, int idRol) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.contrasena = contrasena;
        this.idRol = idRol;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getUsername() { return username; }
    public int getIdRol() { return idRol; }

    // Retorna el nombre del rol según su ID en la base de datos
    public String getRol() {
        if (this.idRol == 1) {
            return "COORDINADOR LOGISTICO";
        } else {
            return "OPERADOR";
        }
    }
}