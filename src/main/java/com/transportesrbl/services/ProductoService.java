package com.transportesrbl.services;

import com.transportesrbl.dao.ProductoDAO;
import com.transportesrbl.models.Producto;

import java.util.List;

public class ProductoService {
    private final ProductoDAO productoDAO;

    public ProductoService() {
        this.productoDAO = new ProductoDAO();
    }

    public List<Producto> obtenerTodosLosProductos() {
        return productoDAO.listar();
    }

    public boolean registrarProducto(Producto producto) {
        return productoDAO.insertar(producto);
    }

    public boolean modificarProducto(Producto producto) {
        return productoDAO.actualizar(producto);
    }

    public boolean eliminarProducto(int id) {
        return productoDAO.eliminar(id);
    }
}