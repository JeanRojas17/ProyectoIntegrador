package com.transportesrbl.dao;

import com.transportesrbl.models.Producto;
import com.transportesrbl.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        
        String sql = "SELECT p.Id_Paquete AS id_producto, " +
                     "       p.Descripcion AS nombre_producto, " +
                     "       c.Nombre_Empresa AS proveedor, " +
                     "       c.Nombre_Empresa AS cliente, " +
                     "       p.Volumen_m3 AS volumen, " +
                     "       p.Cantidad AS cantidad, " +
                     "       ap.Dir_Entrega AS destino, " +
                     "       h.Estado AS estado " +
                     "FROM PAQUETE p " +
                     "JOIN CLIENTE c ON p.Id_Cliente = c.Id_Cliente " +
                     "LEFT JOIN ASIGNACION_PAQUETE ap ON p.Id_Paquete = ap.Id_Paquete " +
                     "LEFT JOIN ( " +
                     "  SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, Estado " +
                     "  FROM HISTORIAL_ESTADOS " +
                     "  ORDER BY Id_Asig_Paq, Fecha DESC" +
                     ") h ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq";
                     
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Utiliza el constructor completo para asignar las variables proveedor y cliente
                Producto p = new Producto(
                    rs.getInt("id_producto"),
                    rs.getString("nombre_producto"),
                    rs.getString("proveedor"),
                    rs.getString("cliente"),
                    rs.getDouble("volumen"),
                    rs.getInt("cantidad"), 
                    rs.getDouble("volumen") * rs.getInt("cantidad"),
                    rs.getString("estado"),
                    rs.getString("destino")
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean insertar(Producto producto) {
        String sqlGetCliente = "SELECT Id_Cliente FROM CLIENTE LIMIT 1";
        String sqlGetCamion = "SELECT id_camion FROM CAMIONES LIMIT 1";
        String sqlInsertPaquete = "INSERT INTO PAQUETE (Id_Cliente, Nro_Paquete, Volumen_m3, Descripcion, Cantidad) VALUES (?, ?, ?, ?, ?) RETURNING Id_Paquete";
        String sqlInsertAsignacion = "INSERT INTO ASIGNACION (Id_Camion) VALUES (?) RETURNING Id_Asignacion";
        String sqlInsertAsigPaq = "INSERT INTO ASIGNACION_PAQUETE (Id_Asignacion, Id_Paquete, Dir_Entrega, Cantidad) VALUES (?, ?, ?, ?)";
        String sqlInsertHistorial = "INSERT INTO HISTORIAL_ESTADOS (Id_Asig_Paq, Estado, Observacion) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return false;
            conn.setAutoCommit(false);

            try {
                // 1. Obtener Cliente y Camion por defecto
                int idCliente = -1;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sqlGetCliente)) {
                    if (rs.next()) idCliente = rs.getInt("Id_Cliente");
                }

                int idCamion = -1;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sqlGetCamion)) {
                    if (rs.next()) idCamion = rs.getInt("id_camion");
                }

                if (idCliente == -1 || idCamion == -1) {
                    System.err.println("Error: No hay clientes o camiones registrados.");
                    return false;
                }

                // 2. Insertar Paquete
                int idPaquete = -1;
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertPaquete)) {
                    ps.setInt(1, idCliente);
                    ps.setString(2, "PKT-" + (System.currentTimeMillis() % 1000000));
                    ps.setDouble(3, producto.getVolumenUnitario());
                    ps.setString(4, producto.getNombreProducto());
                    ps.setInt(5, producto.getCantidad());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) idPaquete = rs.getInt("Id_Paquete");
                    }
                }

                // 3. Crear Asignación automática
                int idAsignacion = -1;
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertAsignacion)) {
                    ps.setInt(1, idCamion);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) idAsignacion = rs.getInt("Id_Asignacion");
                    }
                }

                // 4. Vincular Paquete a Asignación (con Destino)
                int idAsigPaq = -1;
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertAsigPaq, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idAsignacion);
                    ps.setInt(2, idPaquete);
                    ps.setString(3, producto.getDestino());
                    ps.setInt(4, producto.getCantidad());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) idAsigPaq = rs.getInt(1);
                    }
                }

                // 5. Registrar Estado Inicial en Historial
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertHistorial)) {
                    ps.setInt(1, idAsigPaq);
                    ps.setString(2, producto.getEstado());
                    ps.setString(3, "Registro inicial de producto");
                    ps.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Producto producto) {
        String sqlUpdatePaquete = "UPDATE PAQUETE SET Descripcion = ?, Volumen_m3 = ?, Cantidad = ? WHERE Id_Paquete = ?";
        String sqlUpdateAsigPaq = "UPDATE ASIGNACION_PAQUETE SET Dir_Entrega = ? WHERE Id_Paquete = ?";
        String sqlInsertHistorial = "INSERT INTO HISTORIAL_ESTADOS (Id_Asig_Paq, Estado, Observacion) " +
                                    "SELECT Id_Asignacion_Paquete, ?, 'Actualización desde módulo productos' " +
                                    "FROM ASIGNACION_PAQUETE WHERE Id_Paquete = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return false;
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdatePaquete)) {
                    ps.setString(1, producto.getNombreProducto());
                    ps.setDouble(2, producto.getVolumenUnitario());
                    ps.setInt(3, producto.getCantidad());
                    ps.setInt(4, producto.getIdProducto());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateAsigPaq)) {
                    ps.setString(1, producto.getDestino());
                    ps.setInt(2, producto.getIdProducto());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlInsertHistorial)) {
                    ps.setString(1, producto.getEstado());
                    ps.setInt(2, producto.getIdProducto());
                    ps.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sqlDeleteHistorial = "DELETE FROM HISTORIAL_ESTADOS WHERE Id_Asig_Paq IN (SELECT Id_Asignacion_Paquete FROM ASIGNACION_PAQUETE WHERE Id_Paquete = ?)";
        String sqlDeleteAsigPaq = "DELETE FROM ASIGNACION_PAQUETE WHERE Id_Paquete = ?";
        String sqlDeletePaquete = "DELETE FROM PAQUETE WHERE Id_Paquete = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return false;
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlDeleteHistorial)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlDeleteAsigPaq)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlDeletePaquete)) {
                    ps.setInt(1, id);
                    int result = ps.executeUpdate();
                    conn.commit();
                    return result > 0;
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}