package com.transportesrbl.services;

import com.transportesrbl.dao.UsuarioDao;
import com.transportesrbl.models.Usuario;

public class AuthService {
    private final UsuarioDao usuarioDao = new UsuarioDao();

    public Usuario login(String user, String pass) {
        return usuarioDao.validarUsuario(user, pass);
    }
}