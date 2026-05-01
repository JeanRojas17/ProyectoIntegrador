package com.transportesrbl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.transportesrbl.config.DatabaseConnection;
import com.transportesrbl.models.Asignacion;

public class AsignacionDAO {

    public List<Asignacion> listar() {
        List<Asignacion> lista = new ArrayList<>();
        // Se corrige la consulta usando el calificador en minúsculas/mayúsculas exacto de la base de datos
        String sql = "SELECT ap.Id_Asignacion_Paquete, c.modelo_camion, con.nombre_completo, " +
                     "ap.Dir_Entrega, p.descripcion, h.estado " +
                     "FROM ASIGNACION_PAQUETE ap " +
                     "JOIN ASIGNACION a ON ap.Id_Asignacion = a.Id_Asignacion " +
                     "JOIN CAMIONES c ON a.Id_Camion = c.id_camion " +
                     "LEFT JOIN CONDUCTORES con ON c.id_conductor = con.id_conductor " +
                     "JOIN PAQUETE p ON ap.Id_Paquete = p.Id_Paquete " +
                     "LEFT JOIN ( " +
                     "  SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, estado " +
                     "  FROM HISTORIAL_ESTADOS " +
                     "  ORDER BY Id_Asig_Paq, Fecha DESC " +
                     ") h ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Asignacion(
                    rs.getInt("Id_Asignacion_Paquete"),
                    rs.getString("modelo_camion"),
                    rs.getString("nombre_completo"),
                    rs.getString("Dir_Entrega"),
                    rs.getString("descripcion"),
                    rs.getString("estado") != null ? rs.getString("estado") : "Pendiente"
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error crítico en AsignacionDAO (listar): " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(Asignacion asig) {
        String sqlGetCamion = "SELECT id_camion FROM CAMIONES WHERE modelo_camion = ? LIMIT 1";
        String sqlGetPaquete = "SELECT Id_Paquete FROM PAQUETE WHERE Descripcion = ? LIMIT 1";
        String sqlInsertAsig = "INSERT INTO ASIGNACION (Id_Camion) VALUES (?) RETURNING Id_Asignacion";
        String sqlInsertAsigPaq = "INSERT INTO ASIGNACION_PAQUETE (Id_Asignacion, Id_Paquete, Dir_Entrega, Cantidad) VALUES (?, ?, ?, 1)";
        String sqlInsertHistorial = "INSERT INTO HISTORIAL_ESTADOS (Id_Asig_Paq, Estado, Observacion) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return false;
            conn.setAutoCommit(false);

            try {
                int idCamion = -1;
                try (PreparedStatement ps = conn.prepareStatement(sqlGetCamion)) {
                    ps.setString(1, asig.getCamion());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) idCamion = rs.getInt("id_camion");
                    }
                }

                int idPaquete = -1;
                try (PreparedStatement ps = conn.prepareStatement(sqlGetPaquete)) {
                    ps.setString(1, asig.getProducto());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) idPaquete = rs.getInt("Id_Paquete");
                    }
                }

                if (idCamion == -1 || idPaquete == -1) {
                    System.err.println("No se encontró el camión o el paquete.");
                    return false;
                }

                int idAsignacion = -1;
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertAsig)) {
                    ps.setInt(1, idCamion);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) idAsignacion = rs.getInt("Id_Asignacion");
                    }
                }

                int idAsigPaq = -1;
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertAsigPaq, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idAsignacion);
                    ps.setInt(2, idPaquete);
                    ps.setString(3, asig.getRuta());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) idAsigPaq = rs.getInt(1);
                    }
                }

                if (idAsigPaq != -1) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlInsertHistorial)) {
                        ps.setInt(1, idAsigPaq);
                        ps.setString(2, asig.getEstado());
                        ps.setString(3, "Asignación inicial");
                        ps.executeUpdate();
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error en transacción de inserción: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión al insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Asignacion asig) {
        String sqlUpdate = "UPDATE ASIGNACION_PAQUETE SET Dir_Entrega = ? WHERE Id_Asignacion_Paquete = ?";
        String sqlHistorial = "INSERT INTO HISTORIAL_ESTADOS (Id_Asig_Paq, Estado, Observacion) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return false;
            conn.setAutoCommit(false);
            
            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
                 PreparedStatement psHistorial = conn.prepareStatement(sqlHistorial)) {

                psUpdate.setString(1, asig.getRuta());
                psUpdate.setInt(2, asig.getId());      
                psUpdate.executeUpdate();

                psHistorial.setInt(1, asig.getId());
                psHistorial.setString(2, asig.getEstado());
                psHistorial.setString(3, "Actualización manual desde Dashboard");
                psHistorial.executeUpdate();

                conn.commit();
                System.out.println(">>> CRUD: Registro y Historial actualizados con éxito.");
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error en la transacción: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sqlHistorial = "DELETE FROM HISTORIAL_ESTADOS WHERE Id_Asig_Paq = ?";
        String sqlAsignacion = "DELETE FROM ASIGNACION_PAQUETE WHERE Id_Asignacion_Paquete = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return false;
            conn.setAutoCommit(false);
            
            try (PreparedStatement psH = conn.prepareStatement(sqlHistorial);
                 PreparedStatement psA = conn.prepareStatement(sqlAsignacion)) {
                
                psH.setInt(1, id);
                psH.executeUpdate();

                psA.setInt(1, id);
                int filasAfectadas = psA.executeUpdate();

                conn.commit();
                return filasAfectadas > 0;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error en transacción de eliminación: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión al eliminar: " + e.getMessage());
            return false;
        }
    }

    public List<Asignacion> buscarConFiltros(String producto, String estado, java.time.LocalDate fecha) {
        List<Asignacion> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT ap.Id_Asignacion_Paquete, c.modelo_camion, con.nombre_completo, " +
            "ap.Dir_Entrega, p.descripcion, h.estado " +
            "FROM ASIGNACION_PAQUETE ap " +
            "JOIN ASIGNACION a ON ap.Id_Asignacion = a.Id_Asignacion " +
            "JOIN CAMIONES c ON a.Id_Camion = c.id_camion " +
            "LEFT JOIN CONDUCTORES con ON c.id_conductor = con.id_conductor " +
            "JOIN PAQUETE p ON ap.Id_Paquete = p.Id_Paquete " +
            "LEFT JOIN ( " +
            "  SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, estado, fecha " +
            "  FROM HISTORIAL_ESTADOS " +
            "  ORDER BY Id_Asig_Paq, fecha DESC " +
            ") h ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq " +
            "WHERE 1=1 "
        );
        
        if (producto != null && !producto.trim().isEmpty()) sql.append("AND p.descripcion ILIKE ? ");
        if (estado != null && !estado.equals("Seleccionar")) sql.append("AND h.estado = ? ");
        if (fecha != null) sql.append("AND h.fecha::date = ? ");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int i = 1;
            if (producto != null && !producto.trim().isEmpty()) ps.setString(i++, "%" + producto + "%");
            if (estado != null && !estado.equals("Seleccionar")) ps.setString(i++, estado);
            if (fecha != null) ps.setDate(i++, java.sql.Date.valueOf(fecha));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Asignacion(
                        rs.getInt("Id_Asignacion_Paquete"),
                        rs.getString("modelo_camion"),
                        rs.getString("nombre_completo"),
                        rs.getString("Dir_Entrega"),
                        rs.getString("descripcion"),
                        rs.getString("estado") != null ? rs.getString("estado") : "Pendiente"
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en búsqueda filtrada: " + e.getMessage());
        }
        return lista;
    }
}