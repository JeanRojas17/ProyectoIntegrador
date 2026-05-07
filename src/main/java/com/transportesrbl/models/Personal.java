package com.transportesrbl.models;

public class Personal {
    private int id;
    private String nombre;
    private String tipo; // CONDUCTOR, AUXILIAR
    private String identificacion; // CC
    private String telefono;
    private String correo;
    private String estado;
    private String licencia; // Solo para conductores
    private String especialidad; // Solo para auxiliares

    public Personal() {}

    public Personal(int id, String nombre, String tipo, String identificacion, String telefono, String correo, String estado, String licencia, String especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.identificacion = identificacion;
        this.telefono = telefono;
        this.correo = correo;
        this.estado = estado;
        this.licencia = licencia;
        this.especialidad = especialidad;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getLicencia() { return licencia; }
    public void setLicencia(String licencia) { this.licencia = licencia; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
}
