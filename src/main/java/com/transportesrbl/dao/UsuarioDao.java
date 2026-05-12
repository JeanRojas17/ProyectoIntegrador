package com.transportesrbl.dao;

import com.transportesrbl.config.DatabaseConnection;
import com.transportesrbl.models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDao {

    public Usuario validarUsuario(String user, String pass) {
        String sql = "SELECT u.id_usuario, u.nombre, u.usuario, u.contrasena, u.id_rol, r.nombre_rol " +
                     "FROM usuario u " +
                     "JOIN rol r ON u.id_rol = r.id_rol " +
                     "WHERE u.usuario = ? AND u.contrasena = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user);
            stmt.setString(2, pass);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("usuario"),
                        rs.getString("contrasena"),
                        rs.getInt("id_rol"),
                        rs.getString("nombre_rol")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar usuario en Neon: " + e.getMessage());
        }
        return null;
    }
}