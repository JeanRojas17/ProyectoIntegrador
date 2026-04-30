package com.transportesrbl.dao;

import com.transportesrbl.models.Producto;
import com.transportesrbl.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        
        // Consulta SQL corregida para extraer la información real de los productos y paquetes
        String sql = "SELECT p.Id_Paquete AS id_producto, " +
                     "       p.Descripcion AS nombre_producto, " +
                     "       c.Nombre_Empresa AS proveedor, " +
                     "       c.Nombre_Empresa AS cliente, " +
                     "       p.Volumen_m3 AS volumen, " +
                     "       ap.Dir_Entrega AS destino, " +
                     "       h.Estado AS estado " +
                     "FROM PAQUETE p " +
                     "JOIN CLIENTE c ON p.Id_Cliente = c.Id_Cliente " +
                     "LEFT JOIN ASIGNACION_PAQUETE ap ON p.Id_Paquete = ap.Id_Paquete " +
                     "LEFT JOIN ( " +
                     "  SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, Estado " +
                     "  FROM HISTORIAL_ESTADOS " +
                     "  ORDER BY Id_Asig_Paq, Fecha DESC" +
                     ") h ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("nombre_producto"),
                        rs.getString("proveedor"),
                        rs.getString("cliente"),
                        rs.getDouble("volumen"),
                        rs.getString("destino"),
                        rs.getString("estado") != null ? rs.getString("estado") : "Pendiente"
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO PAQUETE (Id_Cliente, Nro_Paquete, Volumen_m3, Descripcion) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, 1); // Ajustar según el ID del cliente seleccionado en la vista
            ps.setString(2, "PKT-" + System.currentTimeMillis()); // Generador único
            ps.setDouble(3, producto.getVolumen());
            ps.setString(4, producto.getNombreProducto());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Producto producto) {
        String sql = "UPDATE PAQUETE SET Descripcion = ?, Volumen_m3 = ? WHERE Id_Paquete = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, producto.getNombreProducto());
            ps.setDouble(2, producto.getVolumen());
            ps.setInt(3, producto.getIdProducto());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM PAQUETE WHERE Id_Paquete = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}