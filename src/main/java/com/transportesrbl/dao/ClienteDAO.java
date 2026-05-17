package com.transportesrbl.dao;

import com.transportesrbl.config.DatabaseConnection;
import com.transportesrbl.models.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClienteDAO {

    public Cliente getClienteByUsuarioId(int idUsuario) {
        String sql = "SELECT * FROM CLIENTE WHERE id_usuario = ?"; 
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                        rs.getInt("Id_Cliente"),
                        rs.getString("Nombre_Empresa"),
                        rs.getString("Contacto"),
                        rs.getInt("id_usuario")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cliente por idUsuario: " + e.getMessage());
        }
        return null;
    }

    public Map<String, Object> getMetricas(int idCliente) {
        Map<String, Object> metricas = new HashMap<>();
        String sql = "SELECT " +
                     "  COUNT(CASE WHEN h.estado IN ('En reparto', 'Siguiente') THEN 1 END) as en_camino, " +
                     "  COUNT(CASE WHEN h.estado = 'Entregado' THEN 1 END) as entregados, " +
                     "  SUM(p.Volumen_m3) as volumen_total " +
                     "FROM PAQUETE p " +
                     "LEFT JOIN ASIGNACION_PAQUETE ap ON p.Id_Paquete = ap.Id_Paquete " +
                     "LEFT JOIN ( " +
                     "  SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, estado " +
                     "  FROM HISTORIAL_ESTADOS " +
                     "  ORDER BY Id_Asig_Paq, Fecha DESC " +
                     ") h ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq " +
                     "WHERE p.Id_Cliente = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    metricas.put("en_camino", rs.getInt("en_camino"));
                    metricas.put("entregados", rs.getInt("entregados"));
                    metricas.put("volumen_total", rs.getDouble("volumen_total"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en getMetricas: " + e.getMessage());
        }
        return metricas;
    }

    public List<Map<String, Object>> getPaquetesActivos(int idCliente) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT p.Id_Paquete, p.Nro_Paquete, p.Descripcion, p.Volumen_m3, " +
                     "ap.Dir_Entrega, ap.Id_Asignacion_Paquete, " +
                     "COALESCE(h.estado, 'Pendiente') as estado " +
                     "FROM PAQUETE p " +
                     "LEFT JOIN ASIGNACION_PAQUETE ap ON p.Id_Paquete = ap.Id_Paquete " +
                     "LEFT JOIN ( " +
                     "  SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, estado " +
                     "  FROM HISTORIAL_ESTADOS " +
                     "  ORDER BY Id_Asig_Paq, Fecha DESC " +
                     ") h ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq " +
                     "WHERE p.Id_Cliente = ? " +
                     "ORDER BY p.Id_Paquete DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> pkg = new HashMap<>();
                    pkg.put("id_paquete", rs.getInt("Id_Paquete"));
                    pkg.put("nro_paquete", rs.getString("Nro_Paquete"));
                    pkg.put("descripcion", rs.getString("Descripcion"));
                    pkg.put("volumen", rs.getDouble("Volumen_m3"));
                    pkg.put("direccion", rs.getString("Dir_Entrega"));
                    pkg.put("id_asig_paq", rs.getInt("Id_Asignacion_Paquete"));
                    pkg.put("estado", rs.getString("estado"));
                    lista.add(pkg);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en getPaquetesActivos: " + e.getMessage());
        }
        return lista;
    }

    public List<String> getNovedades(int idCliente) {
        List<String> novedades = new ArrayList<>();
        String sql = "SELECT h.Observacion, p.Nro_Paquete " +
                     "FROM HISTORIAL_ESTADOS h " +
                     "JOIN ASIGNACION_PAQUETE ap ON h.Id_Asig_Paq = ap.Id_Asignacion_Paquete " +
                     "JOIN PAQUETE p ON ap.Id_Paquete = p.Id_Paquete " +
                     "WHERE p.Id_Cliente = ? AND h.Observacion IS NOT NULL AND h.Observacion != '' " +
                     "ORDER BY h.Fecha DESC LIMIT 10";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    novedades.add("Paquete " + rs.getString("Nro_Paquete") + ": " + rs.getString("Observacion"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en getNovedades: " + e.getMessage());
        }
        return novedades;
    }
}
