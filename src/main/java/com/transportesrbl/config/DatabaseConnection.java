package com.transportesrbl.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Neon connection details - load from environment variables
    private static final String URL = System.getenv("DATABASE_URL") != null 
        ? System.getenv("DATABASE_URL") 
        : "jdbc:postgresql://ep-patient-poetry-amrjkydg-pooler.c-5.us-east-1.aws.neon.tech/neondb?channel_binding=require&sslmode=require";
    
    private static final String USER = System.getenv("DB_USER") != null 
        ? System.getenv("DB_USER") 
        : "neondb_owner";
    
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null 
        ? System.getenv("DB_PASSWORD") 
        : "npg_3T9pxfJoXhRt";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("PostgreSQL JDBC Driver not found");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}