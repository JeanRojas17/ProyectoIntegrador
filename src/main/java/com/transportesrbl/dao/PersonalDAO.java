
package com.transportesrbl.dao;

import com.transportesrbl.config.DatabaseConnection;
import com.transportesrbl.models.Personal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalDAO {

    public List<Personal> listarTodo() {
        List<Personal> lista = new ArrayList<>();
        
        // Obtener Conductores
        String sqlConductores = "SELECT id_conductor, nombre_completo, licencia, telefono, estado FROM CONDUCTORES";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlConductores);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Personal(
                    rs.getInt("id_conductor"),
                    rs.getString("nombre_completo"),
                    "CONDUCTOR",
                    "", // No hay CC en la tabla original, usaremos vacío o licencia
                    rs.getString("telefono"),
                    "", // No hay correo en conductores
                    rs.getString("estado"),
                    rs.getString("licencia"),
                    ""
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Obtener Auxiliares
        String sqlAuxiliares = "SELECT a.Id_Auxiliar, u.nombre, u.usuario, a.Estado, a.Especialidad " +
                               "FROM AUXILIAR a JOIN usuario u ON a.Id_Usuario = u.id_usuario";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlAuxiliares);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Personal(
                    rs.getInt("Id_Auxiliar"),
                    rs.getString("nombre"),
                    "AUXILIAR",
                    "",
                    "", // No hay teléfono en auxiliar/usuario original
                    rs.getString("usuario") + "@transportesrbl.com", // Mockup de correo
                    rs.getString("Estado"),
                    "",
                    rs.getString("Especialidad")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean insertarConductor(Personal p) {
        String sql = "INSERT INTO CONDUCTORES (nombre_completo, licencia, telefono, estado) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getLicencia());
            ps.setString(3, p.getTelefono());
            ps.setString(4, p.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarConductor(Personal p) {
        String sql = "UPDATE CONDUCTORES SET nombre_completo = ?, licencia = ?, telefono = ?, estado = ? WHERE id_conductor = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getLicencia());
            ps.setString(3, p.getTelefono());
            ps.setString(4, p.getEstado());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarConductor(int id) {
        String sql = "DELETE FROM CONDUCTORES WHERE id_conductor = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertarAuxiliar(Personal p) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Crear usuario
            String sqlUser = "INSERT INTO usuario (nombre, usuario, contrasena, id_rol) VALUES (?, ?, ?, ?) RETURNING id_usuario";
            int idUsuario = -1;
            try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.getNombre().toLowerCase().replace(" ", "."));
                ps.setString(3, "123456"); // Password por defecto
                ps.setInt(4, 2); // Rol Operador por defecto
                ResultSet rs = ps.executeQuery();
                if (rs.next()) idUsuario = rs.getInt(1);
            }

            // 2. Crear auxiliar
            if (idUsuario != -1) {
                String sqlAux = "INSERT INTO AUXILIAR (Id_Usuario, Estado, Especialidad) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlAux)) {
                    ps.setInt(1, idUsuario);
                    ps.setString(2, p.getEstado());
                    ps.setString(3, p.getEspecialidad());
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public boolean actualizarAuxiliar(Personal p) {
        String sql = "UPDATE AUXILIAR SET Estado = ?, Especialidad = ? WHERE Id_Auxiliar = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getEstado());
            ps.setString(2, p.getEspecialidad());
            ps.setInt(3, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarAuxiliar(int id) {
        String sql = "DELETE FROM AUXILIAR WHERE Id_Auxiliar = ?";
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
