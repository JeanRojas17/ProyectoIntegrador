package com.transportesrbl.dao;

import com.transportesrbl.models.MetricasDashboard;
import com.transportesrbl.models.Entrega;
import com.transportesrbl.models.Camion; // Asegúrate de crear este modelo
import com.transportesrbl.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    /**
     * Obtiene las métricas para las 4 tarjetas superiores
     */
    public MetricasDashboard obtenerMetricas() {
        int activas = 0, completas = 0, pendientes = 0, totalCamiones = 0, dispCamiones = 0;

        String sqlMetricas = "SELECT " +
            "(SELECT COUNT(*) FROM HISTORIAL_ESTADOS WHERE Estado = 'En reparto') as activas, " +
            "(SELECT COUNT(*) FROM HISTORIAL_ESTADOS WHERE Estado = 'Entregado') as completas, " +
            "(SELECT COUNT(*) FROM PAQUETE) as pendientes, " +
            "(SELECT COUNT(*) FROM CAMIONES) as total, " +
            "(SELECT COUNT(*) FROM CAMIONES WHERE estado = 'Disponible') as disponibles";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlMetricas);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                activas = rs.getInt("activas");
                completas = rs.getInt("completas");
                pendientes = rs.getInt("pendientes");
                dispCamiones = rs.getInt("disponibles");
                totalCamiones = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerMetricas: " + e.getMessage());
        }

        return new MetricasDashboard(activas, completas, pendientes, dispCamiones + "/" + totalCamiones);
    }

    /**
     * Obtiene datos para la tabla "ENTREGAS RECIENTES"
     */
    public List<Entrega> obtenerEntregasRecientes() {
        List<Entrega> lista = new ArrayList<>();
        
        String sql = "SELECT ap.Id_Asig_Paquete, p.Descripcion, ap.Dir_Entrega, h.Estado " +
                     "FROM ASIGNACION_PAQUETE ap " +
                     "JOIN PAQUETE p ON ap.Id_Paquete = p.Id_Paquete " +
                     "JOIN (SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, Estado, Fecha " +
                     "      FROM HISTORIAL_ESTADOS ORDER BY Id_Asig_Paq, Fecha DESC) h " +
                     "ON ap.Id_Asig_Paquete = h.Id_Asig_Paq " +
                     "ORDER BY h.Fecha DESC LIMIT 5";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Entrega(
                    rs.getInt("Id_Asig_Paquete"),
                    rs.getString("Descripcion"),
                    rs.getString("Dir_Entrega"),
                    rs.getString("Estado")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerEntregasRecientes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene datos para la tabla "ESTADO DE LA FLOTA"
     */
    public List<Camion> obtenerEstadoFlota() {
        List<Camion> lista = new ArrayList<>();
        
        // Consulta simple a la tabla camiones
        String sql = "SELECT modelo_camion, capacidad_m3, estado FROM CAMIONES ORDER BY id_camion ASC LIMIT 5";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Camion(
                    rs.getString("modelo_camion"),
                    rs.getDouble("capacidad_m3"),
                    rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerEstadoFlota: " + e.getMessage());
        }
        return lista;
    }
}