package com.transportesrbl.dao;

import com.transportesrbl.config.DatabaseConnection;
import com.transportesrbl.models.Reporte;
import com.transportesrbl.models.ReporteDetalle;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReporteDAO {

    public Reporte obtenerDatosReporteGeneral() {
        int totalEntregas = 0;
        double tasaExito = 0;
        double tiempoPromedio = 0;
        double volumenTotal = 0;
        Map<String, Integer> entregasPorSemana = new LinkedHashMap<>();
        List<ReporteDetalle> detalles = new ArrayList<>();

        // Consulta para métricas generales
        String sqlMetricas = "SELECT " +
            "COUNT(*) as total, " +
            "COUNT(*) FILTER (WHERE h.Estado = 'Entregado') as exitosas, " +
            "AVG(EXTRACT(EPOCH FROM (h.Fecha - a.Fecha_Asignacion))/3600) FILTER (WHERE h.Estado = 'Entregado') as tiempo_promedio, " +
            "SUM(p.Volumen_m3 * ap.Cantidad) as volumen " +
            "FROM ASIGNACION_PAQUETE ap " +
            "JOIN ASIGNACION a ON ap.Id_Asignacion = a.Id_Asignacion " +
            "JOIN PAQUETE p ON ap.Id_Paquete = p.Id_Paquete " +
            "LEFT JOIN ( " +
            "   SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, Estado, Fecha " +
            "   FROM HISTORIAL_ESTADOS " +
            "   ORDER BY Id_Asig_Paq, Fecha DESC " +
            ") h ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq";

        // Consulta para gráfico por semana
        String sqlGrafico = "SELECT " +
            "to_char(date_trunc('week', Fecha), '\"Sem \"WW') as semana, " +
            "COUNT(*) as cantidad " +
            "FROM HISTORIAL_ESTADOS " +
            "WHERE Estado = 'Entregado' " +
            "GROUP BY semana " +
            "ORDER BY semana DESC LIMIT 5";

        // Consulta para tabla de detalles
        String sqlDetalles = "SELECT " +
            "a.Fecha_Asignacion, c.modelo_camion, ap.Dir_Entrega, p.Descripcion, " +
            "(p.Volumen_m3 * ap.Cantidad) as volumen_total, " +
            "EXTRACT(EPOCH FROM (h.Fecha - a.Fecha_Asignacion))/3600 as tiempo_hrs, " +
            "COALESCE(h.Estado, 'Pendiente') as estado_actual " +
            "FROM ASIGNACION_PAQUETE ap " +
            "JOIN ASIGNACION a ON ap.Id_Asignacion = a.Id_Asignacion " +
            "JOIN CAMIONES c ON a.Id_Camion = c.id_camion " +
            "JOIN PAQUETE p ON ap.Id_Paquete = p.Id_Paquete " +
            "LEFT JOIN ( " +
            "   SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, Estado, Fecha " +
            "   FROM HISTORIAL_ESTADOS " +
            "   ORDER BY Id_Asig_Paq, Fecha DESC " +
            ") h ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq " +
            "ORDER BY a.Fecha_Asignacion DESC LIMIT 20";

        try (Connection conn = DatabaseConnection.getConnection()) {
            
            try (PreparedStatement ps = conn.prepareStatement(sqlMetricas);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalEntregas = rs.getInt("total");
                    int exitosas = rs.getInt("exitosas");
                    tasaExito = totalEntregas > 0 ? (exitosas * 100.0 / totalEntregas) : 0;
                    tiempoPromedio = rs.getDouble("tiempo_promedio");
                    volumenTotal = rs.getDouble("volumen");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlGrafico);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    entregasPorSemana.put(rs.getString("semana"), rs.getInt("cantidad"));
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlDetalles);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(new ReporteDetalle(
                        rs.getTimestamp("Fecha_Asignacion").toLocalDateTime(),
                        rs.getString("modelo_camion"),
                        rs.getString("Dir_Entrega"),
                        rs.getString("Descripcion"),
                        rs.getDouble("volumen_total"),
                        rs.getDouble("tiempo_hrs"),
                        rs.getString("estado_actual")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Reporte(totalEntregas, tasaExito, tiempoPromedio, volumenTotal, entregasPorSemana, detalles);
    }
}
