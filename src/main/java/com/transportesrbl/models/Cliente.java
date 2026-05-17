package com.transportesrbl.models;

public class Cliente {
    private int idCliente;
    private String nombreEmpresa;
    private String contacto;
    private Integer idUsuario;

    public Cliente(int idCliente, String nombreEmpresa, String contacto) {
        this.idCliente = idCliente;
        this.nombreEmpresa = nombreEmpresa;
        this.contacto = contacto;
    }

    public Cliente(int idCliente, String nombreEmpresa, String contacto, Integer idUsuario) {
        this.idCliente = idCliente;
        this.nombreEmpresa = nombreEmpresa;
        this.contacto = contacto;
        this.idUsuario = idUsuario;
    }

    // Getters
    public int getIdCliente() { return idCliente; }
    public String getNombreEmpresa() { return nombreEmpresa; }
    public String getContacto() { return contacto; }
    public Integer getIdUsuario() { return idUsuario; }
    
    // Setters
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
}
