package com.transportesrbl.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Reemplaza con tus datos reales de Neon Console
    private static final String URL = "jdbc:postgresql://tu-host.neon.tech/neondb"; 
    private static final String USER = "tu_usuario";
    private static final String PASSWORD = "tu_password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}