package com.transportesrbl.config;

import java.io.FileInputStream; // Cambiado para leer archivos externos
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final Properties properties = new Properties();

    static {
        // Al estar al lado del pom.xml, se accede como un archivo del sistema de archivos raíz
        try (FileInputStream input = new FileInputStream("db.properties")) {
            properties.load(input);
            // Cargar el driver para asegurar compatibilidad
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            System.err.println("Error crítico: No se pudo cargar db.properties desde la raíz.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password")
            );
        } catch (SQLException e) {
            System.err.println("Error al conectar a la DB de Neon: " + e.getMessage());
            return null;
        }
    }
}