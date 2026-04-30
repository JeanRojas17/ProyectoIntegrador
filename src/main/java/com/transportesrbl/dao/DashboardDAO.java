package com.transportesrbl.dao;

import com.transportesrbl.models.*;
import com.transportesrbl.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public MetricasDashboard obtenerMetricas() {
        int activas = 0, completas = 0, pendientes = 0, total = 0, disp = 0;

        // SQL Corregido: Se utiliza el estado 'Entregado' para contar las completas correctamente
        String sql = "SELECT " +
            "COUNT(*) FILTER (WHERE COALESCE(h.Estado, 'Pendiente') = 'En reparto') as activas, " +
            "COUNT(*) FILTER (WHERE COALESCE(h.Estado, 'Pendiente') = 'Entregado') as completas, " +
            "COUNT(*) FILTER (WHERE COALESCE(h.Estado, 'Pendiente') = 'Pendiente') as pendientes " +
            "FROM ASIGNACION_PAQUETE ap " +
            "LEFT JOIN ( " +
            "   SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, Estado " +
            "   FROM HISTORIAL_ESTADOS " +
            "   ORDER BY Id_Asig_Paq, Fecha DESC " +
            ") h ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq";

        String sqlCamiones = "SELECT " +
            "(SELECT COUNT(*) FROM CAMIONES) as total, " +
            "(SELECT COUNT(*) FROM CAMIONES WHERE estado = 'Disponible') as disponibles";

        try (Connection conn = DatabaseConnection.getConnection()) {
            
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    activas = rs.getInt("activas");
                    completas = rs.getInt("completas");
                    pendientes = rs.getInt("pendientes");
                }
            } catch (SQLException e) {
                System.err.println("Error en conteo de métricas: " + e.getMessage());
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlCamiones);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    disp = rs.getInt("disponibles");
                    total = rs.getInt("total");
                }
            } catch (SQLException e) {
                System.err.println("Error en conteo de camiones: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }

        // Se retorna el objeto con 4 parámetros para mantener la compatibilidad del constructor
        return new MetricasDashboard(activas, completas, pendientes, disp + "/" + total);
    }


    public List<Entrega> obtenerEntregasRecientes() {
        List<Entrega> lista = new ArrayList<>();
        // Consulta unificada con los campos correctos para evitar referencias nulas
        String sql = "SELECT ap.Id_Asignacion_Paquete, p.Descripcion, ap.Dir_Entrega, h.Estado " +
                     "FROM ASIGNACION_PAQUETE ap " +
                     "JOIN PAQUETE p ON ap.Id_Paquete = p.Id_Paquete " +
                     "JOIN (SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, Estado, Fecha " +
                     "      FROM HISTORIAL_ESTADOS ORDER BY Id_Asig_Paq, Fecha DESC) h " +
                     "ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq " +
                     "ORDER BY h.Fecha DESC LIMIT 5";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Entrega(
                    rs.getInt("Id_Asignacion_Paquete"), 
                    rs.getString("Descripcion"), 
                    rs.getString("Dir_Entrega"), 
                    rs.getString("Estado")
                ));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return lista;
    }

    public List<Camion> obtenerEstadoFlota() {
        List<Camion> lista = new ArrayList<>();
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
            e.printStackTrace(); 
        }
        return lista;
    }
}