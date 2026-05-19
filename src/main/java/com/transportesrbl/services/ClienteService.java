package com.transportesrbl.services;

import com.transportesrbl.dao.ClienteDAO;
import com.transportesrbl.models.Cliente;
import java.util.List;

public class ClienteService {
    private final ClienteDAO dao = new ClienteDAO();

    public List<Cliente> listar() {
        return dao.listarTodos();
    }

    public boolean guardar(Cliente c, String usuario, String contrasena) {
        if (c.getIdCliente() == 0) {
            return dao.insertar(c, usuario, contrasena);
        } else {
            return dao.actualizar(c);
        }
    }

    public boolean eliminar(int id) {
        return dao.eliminar(id);
    }
}
