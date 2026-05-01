package com.transportesrbl.models;

public class SesionUsuario {

    private static SesionUsuario instancia;
    private Usuario usuarioActivo;

    private SesionUsuario() {
    }

    public static SesionUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }

        return instancia;
    }

    public Usuario getUsuarioActivo() {
        return usuarioActivo;
    }

    public void setUsuarioActivo(Usuario usuario) {
        this.usuarioActivo = usuario;
    }
}