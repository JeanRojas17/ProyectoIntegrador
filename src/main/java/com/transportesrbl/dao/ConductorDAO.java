package com.transportesrbl.dao;

import com.transportesrbl.config.DatabaseConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ConductorDAO {

    public int obtenerIdConductor(int idUsuario) {
        String sql = "SELECT id_conductor FROM CONDUCTORES WHERE id_usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_conductor");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Map<String, Object> obtenerDatosDashboard(int idUsuario) {
        Map<String, Object> datos = new HashMap<>();
        
        String sqlConductor = "SELECT id_conductor, nombre_completo FROM CONDUCTORES WHERE id_usuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            int idConductor = -1;
            String nombreConductor = "";
            
            try (PreparedStatement ps = conn.prepareStatement(sqlConductor)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idConductor = rs.getInt("id_conductor");
                        nombreConductor = rs.getString("nombre_completo");
                        datos.put("nombreConductor", nombreConductor);
                    }
                }
            }

            if (idConductor != -1) {
                // Obtener vehículo asignado desde la última asignación
                String sqlVehiculo = "SELECT c.modelo_camion, c.id_camion " +
                                   "FROM ASIGNACION a " +
                                   "JOIN CAMIONES c ON a.Id_Camion = c.id_camion " +
                                   "WHERE a.Id_Conductor = ? " +
                                   "ORDER BY a.Fecha_Asignacion DESC LIMIT 1";
                int idCamion = -1;
                try (PreparedStatement ps = conn.prepareStatement(sqlVehiculo)) {
                    ps.setInt(1, idConductor);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            datos.put("vehiculo", rs.getString("modelo_camion"));
                            idCamion = rs.getInt("id_camion");
                        }
                    }
                }

                if (idCamion != -1) {
                    // Obtener estado del vehículo
                    String sqlEstadoVehiculo = "SELECT estado FROM CAMIONES WHERE id_camion = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlEstadoVehiculo)) {
                        ps.setInt(1, idCamion);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                datos.put("estadoVehiculo", rs.getString("estado"));
                            }
                        }
                    }

                    // Obtener entrega en curso (la más reciente que no esté entregada)
                    String sqlEntrega = "SELECT COALESCE(p.Nombre, paq.Descripcion) as nombre_item, " +
                                       "ap.Dir_Entrega, c.Nombre_Empresa, paq.Volumen_m3, ap.Id_Asignacion_Paquete " +
                                       "FROM ASIGNACION a " +
                                       "JOIN ASIGNACION_PAQUETE ap ON a.Id_Asignacion = ap.Id_Asignacion " +
                                       "JOIN PAQUETE paq ON ap.Id_Paquete = paq.Id_Paquete " +
                                       "JOIN CLIENTE c ON paq.Id_Cliente = c.Id_Cliente " +
                                       "LEFT JOIN Paquete_Producto pp ON paq.Id_Paquete = pp.Id_Paquete " +
                                       "LEFT JOIN Productos p ON pp.Id_Producto = p.Id_Producto " +
                                       "LEFT JOIN (SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, Estado FROM HISTORIAL_ESTADOS ORDER BY Id_Asig_Paq, Fecha DESC) h " +
                                       "ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq " +
                                       "WHERE a.Id_Conductor = ? AND (h.Estado IS NULL OR h.Estado NOT IN ('Entregado', 'Cancelado')) " +
                                       "ORDER BY a.Fecha_Asignacion DESC LIMIT 1";
                    
                    try (PreparedStatement ps = conn.prepareStatement(sqlEntrega)) {
                        ps.setInt(1, idConductor);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                datos.put("producto", rs.getString("nombre_item"));
                                datos.put("direccion", rs.getString("Dir_Entrega"));
                                datos.put("cliente", rs.getString("Nombre_Empresa"));
                                datos.put("volumen", rs.getDouble("Volumen_m3") + " m³");
                                datos.put("idEntrega", rs.getInt("Id_Asignacion_Paquete"));
                            }
                        }
                    }

                    // Métricas de progreso
                    String sqlMetricas = "SELECT " +
                                        "COUNT(*) as total, " +
                                        "COUNT(*) FILTER (WHERE h.Estado = 'Entregado') as completadas " +
                                        "FROM ASIGNACION a " +
                                        "JOIN ASIGNACION_PAQUETE ap ON a.Id_Asignacion = ap.Id_Asignacion " +
                                        "LEFT JOIN (SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, Estado FROM HISTORIAL_ESTADOS ORDER BY Id_Asig_Paq, Fecha DESC) h " +
                                        "ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq " +
                                        "WHERE a.Id_Conductor = ? AND a.Fecha_Asignacion::date = CURRENT_DATE";
                    
                    try (PreparedStatement ps = conn.prepareStatement(sqlMetricas)) {
                        ps.setInt(1, idConductor);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                datos.put("totalEntregas", rs.getInt("total"));
                                datos.put("completadas", rs.getInt("completadas"));
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return datos;
    }

    public boolean finalizarEntrega(int idAsignacionPaquete, String observacion) {
        String sql = "INSERT INTO HISTORIAL_ESTADOS (Id_Asig_Paq, Estado, Observacion) VALUES (?, 'Entregado', ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAsignacionPaquete);
            ps.setString(2, observacion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
