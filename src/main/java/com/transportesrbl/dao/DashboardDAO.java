package com.transportesrbl.dao;

import java.util.ArrayList;
import java.util.List;

import com.transportesrbl.models.Entrega;

public class DashboardDAO {
    // Aquí es donde en el futuro harás el SELECT a la base de datos
    public List<Entrega> obtenerEntregasRecientes() {
        List<Entrega> lista = new ArrayList<>();
        lista.add(new Entrega(101, "Cali", "En tránsito"));
        lista.add(new Entrega(102, "Bogotá", "Entregado"));
        return lista;
    }
}