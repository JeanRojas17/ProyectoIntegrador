package com.transportesrbl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.transportesrbl.config.DatabaseConnection;
import com.transportesrbl.models.Asignacion;

public class AsignacionDAO {

    public List<Asignacion> listar() {
        List<Asignacion> lista = new ArrayList<>();
        String sql = "SELECT ap.id_asig_paquete, c.modelo_camion, con.nombre_completo, " +
                     "ap.dir_entrega, p.descripcion, h.estado " +
                     "FROM asignacion_paquete ap " +
                     "JOIN asignacion a ON ap.id_asignacion = a.id_asignacion " +
                     "JOIN camiones c ON a.id_camion = c.id_camion " +
                     "LEFT JOIN conductores con ON c.id_conductor = con.id_conductor " +
                     "JOIN paquete p ON ap.id_paquete = p.id_paquete " +
                     "LEFT JOIN ( " +
                     "  SELECT DISTINCT ON (id_asig_paq) id_asig_paq, estado " +
                     "  FROM historial_estados " +
                     "  ORDER BY id_asig_paq, fecha DESC " +
                     ") h ON ap.id_asig_paquete = h.id_asig_paq";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Asignacion(
                    rs.getInt("id_asig_paquete"),
                    rs.getString("modelo_camion"),
                    rs.getString("nombre_completo"),
                    rs.getString("dir_entrega"),
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
         String sql = "INSERT INTO ASIGNACION_PAQUETE (Id_Asignacion, Id_Paquete, Dir_Entrega, Cantidad) VALUES (?, ?, ?, ?)";
        //String sql = "INSERT INTO ASIGNACION_PAQUETE (Id_Asignacion, Id_Paquete, Dir_Entrega, Cantidad) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, 1); 
            ps.setInt(2, 1); 
            ps.setString(3, asig.getRuta()); 
            ps.setInt(4, 1);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Asignacion asig) {
    // SQL para actualizar la dirección de entrega en la tabla principal
    String sqlUpdate = "UPDATE ASIGNACION_PAQUETE SET Dir_Entrega = ? WHERE Id_Asig_Paquete = ?";
    
    // SQL para insertar el nuevo rastro en el historial
    String sqlHistorial = "INSERT INTO HISTORIAL_ESTADOS (Id_Asig_Paq, Estado, Observacion) VALUES (?, ?, ?)";

    // Usamos tu clase DatabaseConnection para obtener la sesión
    try (Connection conn = DatabaseConnection.getConnection()) {
        if (conn == null) return false;

        // Iniciamos la transacción para que ambos cambios ocurran al tiempo[cite: 1]
        conn.setAutoCommit(false); 

        try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
             PreparedStatement psHistorial = conn.prepareStatement(sqlHistorial)) {

            // 1. Ejecutar el Update de la dirección[cite: 1]
            psUpdate.setString(1, asig.getRuta()); 
            psUpdate.setInt(2, asig.getId());      
            psUpdate.executeUpdate();

            // 2. Ejecutar el Insert en el Historial[cite: 1]
            psHistorial.setInt(1, asig.getId());    
            psHistorial.setString(2, asig.getEstado()); // El estado que viene del ComboBox[cite: 3, 4]
            psHistorial.setString(3, "Actualización manual desde Dashboard"); 
            psHistorial.executeUpdate();

            // Si todo salió bien, confirmamos en la base de datos[cite: 1]
            conn.commit(); 
            System.out.println(">>> CRUD: Registro y Historial actualizados con éxito.");
            return true;
            
        } catch (SQLException e) {
            // Si algo falla, deshacemos los cambios para evitar datos inconsistentes[cite: 1]
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
        String sql = "DELETE FROM asignacion_paquete WHERE id_asig_paquete = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }
}