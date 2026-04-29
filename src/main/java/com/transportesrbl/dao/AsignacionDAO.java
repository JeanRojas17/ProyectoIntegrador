package com.transportesrbl.dao;

import com.transportesrbl.config.DatabaseConnection;
import com.transportesrbl.models.Asignacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsignacionDAO {

    /**
     * Lista todas las asignaciones con sus relaciones
     */
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

    /**
     * Inserta una nueva asignación de paquete.
     */
    public boolean insertar(Asignacion asig) {
        String sql = "INSERT INTO ASIGNACION_PAQUETE (Id_Asignacion, Id_Paquete, Dir_Entrega, Cantidad) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, 1); // ID temporal
            ps.setInt(2, 1); // ID temporal
            ps.setString(3, asig.getRuta()); 
            ps.setInt(4, 1);

            System.out.println(">>> Ejecutando SQL: " + sql);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza la dirección o el paquete de una asignación existente
     */
    public boolean actualizar(int idAsigPaquete, String nuevaDireccion) {
        String sql = "UPDATE asignacion_paquete SET dir_entrega = ? WHERE id_asig_paquete = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nuevaDireccion);
            ps.setInt(2, idAsigPaquete);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina una asignación por su ID
     */
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

    /**
     * Carga camiones desde la BD
     */
    public List<String> obtenerCamionesDisponibles() {
        List<String> camiones = new ArrayList<>();
        String sql = "SELECT modelo_camion FROM camiones"; 
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) camiones.add(rs.getString("modelo_camion"));
        } catch (SQLException e) { e.printStackTrace(); }
        return camiones;
    }
} // <--- Esta es la única llave que debe cerrar la clase al final