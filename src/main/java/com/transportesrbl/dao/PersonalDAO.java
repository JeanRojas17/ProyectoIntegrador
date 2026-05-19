
package com.transportesrbl.dao;

import com.transportesrbl.config.Constantes;
import com.transportesrbl.config.DatabaseConnection;
import com.transportesrbl.models.Personal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalDAO {

    public List<Personal> listarTodo() {
        List<Personal> lista = new ArrayList<>();
        
        // Obtener Conductores con su usuario
        String sqlConductores = "SELECT c.id_conductor, c.nombre_completo, c.licencia, c.telefono, c.estado, u.id_usuario, u.usuario, u.contrasena " +
                               "FROM CONDUCTORES c LEFT JOIN usuario u ON c.id_usuario = u.id_usuario";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlConductores);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Personal p = new Personal(
                    rs.getInt("id_conductor"),
                    rs.getString("nombre_completo"),
                    "CONDUCTOR",
                    "", 
                    rs.getString("telefono"),
                    "", 
                    rs.getString("estado"),
                    rs.getString("licencia"),
                    ""
                );
                p.setIdUsuario(rs.getInt("id_usuario"));
                p.setUsuario(rs.getString("usuario"));
                p.setContrasena(rs.getString("contrasena"));
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Obtener Auxiliares con su usuario
        String sqlAuxiliares = "SELECT a.Id_Auxiliar, u.id_usuario, u.nombre, u.usuario, u.contrasena, a.Estado, a.Especialidad " +
                               "FROM AUXILIAR a JOIN usuario u ON a.Id_Usuario = u.id_usuario";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlAuxiliares);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Personal p = new Personal(
                    rs.getInt("Id_Auxiliar"),
                    rs.getString("nombre"),
                    "AUXILIAR",
                    "",
                    "", 
                    rs.getString("usuario") + "@transportesrbl.com", 
                    rs.getString("Estado"),
                    "",
                    rs.getString("Especialidad")
                );
                p.setIdUsuario(rs.getInt("id_usuario"));
                p.setUsuario(rs.getString("usuario"));
                p.setContrasena(rs.getString("contrasena"));
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean insertarConductor(Personal p) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Crear usuario si tiene credenciales
            int idUsuario = 0;
            if (p.getUsuario() != null && !p.getUsuario().isEmpty()) {
                String sqlUser = "INSERT INTO usuario (nombre, usuario, contrasena, id_rol) VALUES (?, ?, ?, ?) RETURNING id_usuario";
                try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                    ps.setString(1, p.getNombre());
                    ps.setString(2, p.getUsuario());
                    ps.setString(3, p.getContrasena() != null ? p.getContrasena() : "123456");
                    ps.setInt(4, Constantes.ID_ROL_CONDUCTOR);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) idUsuario = rs.getInt(1);
                }
            }

            // 2. Crear conductor
            String sql = "INSERT INTO CONDUCTORES (nombre_completo, licencia, telefono, estado, id_usuario) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.getLicencia());
                ps.setString(3, p.getTelefono());
                ps.setString(4, p.getEstado());
                if (idUsuario > 0) ps.setInt(5, idUsuario);
                else ps.setNull(5, Types.INTEGER);
                ps.executeUpdate();
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

    public boolean actualizarConductor(Personal p) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Actualizar conductor
            String sql = "UPDATE CONDUCTORES SET nombre_completo = ?, licencia = ?, telefono = ?, estado = ? WHERE id_conductor = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.getLicencia());
                ps.setString(3, p.getTelefono());
                ps.setString(4, p.getEstado());
                ps.setInt(5, p.getId());
                ps.executeUpdate();
            }

            // 2. Actualizar o crear usuario
            if (p.getUsuario() != null && !p.getUsuario().isEmpty()) {
                if (p.getIdUsuario() > 0) {
                    String sqlUser = "UPDATE usuario SET usuario = ?, contrasena = ?, nombre = ? WHERE id_usuario = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                        ps.setString(1, p.getUsuario());
                        ps.setString(2, p.getContrasena());
                        ps.setString(3, p.getNombre());
                        ps.setInt(4, p.getIdUsuario());
                        ps.executeUpdate();
                    }
                } else {
                    // Crear si no existía
                    String sqlUser = "INSERT INTO usuario (nombre, usuario, contrasena, id_rol) VALUES (?, ?, ?, ?) RETURNING id_usuario";
                    try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                        ps.setString(1, p.getNombre());
                        ps.setString(2, p.getUsuario());
                        ps.setString(3, p.getContrasena());
                        ps.setInt(4, Constantes.ID_ROL_CONDUCTOR);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            int idUser = rs.getInt(1);
                            String sqlUpdateC = "UPDATE CONDUCTORES SET id_usuario = ? WHERE id_conductor = ?";
                            try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdateC)) {
                                ps2.setInt(1, idUser);
                                ps2.setInt(2, p.getId());
                                ps2.executeUpdate();
                            }
                        }
                    }
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
                ps.setString(2, p.getUsuario() != null ? p.getUsuario() : p.getNombre().toLowerCase().replace(" ", "."));
                ps.setString(3, p.getContrasena() != null ? p.getContrasena() : Constantes.DEFAULT_PASSWORD_AUXILIAR);
                ps.setInt(4, Constantes.ID_ROL_AUXILIAR);
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
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Actualizar auxiliar
            String sql = "UPDATE AUXILIAR SET Estado = ?, Especialidad = ? WHERE Id_Auxiliar = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getEstado());
                ps.setString(2, p.getEspecialidad());
                ps.setInt(3, p.getId());
                ps.executeUpdate();
            }

            // 2. Actualizar usuario
            if (p.getIdUsuario() > 0) {
                String sqlUser = "UPDATE usuario SET usuario = ?, contrasena = ?, nombre = ? WHERE id_usuario = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                    ps.setString(1, p.getUsuario());
                    ps.setString(2, p.getContrasena());
                    ps.setString(3, p.getNombre());
                    ps.setInt(4, p.getIdUsuario());
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