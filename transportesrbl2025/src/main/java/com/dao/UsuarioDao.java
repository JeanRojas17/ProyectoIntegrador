package com.dao;


import com.Conexion.DatabaseConnection;
import java.sql.*;

public class UsuarioDao {
    
public boolean validarLogin(String user, String pass) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND contrasena = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user);
            stmt.setString(2, pass); // Nota: En producción, recuerda usar hashing para la contraseña
            
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Si devuelve true, el usuario existe
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
