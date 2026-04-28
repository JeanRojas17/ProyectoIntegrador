package com.transportesrbl.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("Error: No se encontró el archivo db.properties");
            } else {
                properties.load(input);
                // Cargar el driver manualmente (necesario en algunas versiones de JDBC)
                Class.forName("org.postgresql.Driver");
            }
        } catch (Exception e) {
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
        } catch (Exception e) {
            System.err.println("Error al conectar a la DB: " + e.getMessage());
            return null;
        }
    }
}