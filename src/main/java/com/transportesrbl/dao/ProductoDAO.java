package com.transportesrbl.dao;

import com.transportesrbl.models.Producto;
import com.transportesrbl.config.DatabaseConnection;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT p.Id_Paquete AS id_producto, p.Id_Cliente AS id_cliente, " +
                     "p.Descripcion AS nombre_producto, " +
                     "'' AS proveedor, " +
                     "c.Nombre_Empresa AS cliente, " +
                     "p.Volumen_m3 AS volumen, " +
                     "p.Cantidad AS cantidad, " +
                     "COALESCE(ap.Dir_Entrega, '') AS destino, " +
                     "COALESCE(h.Estado, 'Pendiente') AS estado " +
                     "FROM PAQUETE p " +
                     "JOIN CLIENTE c ON p.Id_Cliente = c.Id_Cliente " +
                     "LEFT JOIN ASIGNACION_PAQUETE ap ON p.Id_Paquete = ap.Id_Paquete " +
                     "LEFT JOIN ( " +
                     "  SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, Estado " +
                     "  FROM HISTORIAL_ESTADOS " +
                     "  ORDER BY Id_Asig_Paq, Fecha DESC " +
                     ") h ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id_producto"),
                    rs.getInt("id_cliente"),
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
        String sqlInsertPaquete = "INSERT INTO PAQUETE (Id_Cliente, Nro_Paquete, Volumen_m3, Descripcion, Cantidad) VALUES (?, ?, ?, ?, ?)";

        if (producto.getClienteId() <= 0) {
            System.err.println("Error: No se ha seleccionado un cliente para el paquete.");
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return false;
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertPaquete)) {
                    ps.setInt(1, producto.getClienteId());
                    ps.setString(2, "PKT-" + (System.currentTimeMillis() % 1000000));
                    ps.setDouble(3, producto.getVolumenUnitario());
                    ps.setString(4, producto.getNombreProducto());
                    ps.setInt(5, producto.getCantidad());
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
        String sqlUpdatePaquete = "UPDATE PAQUETE SET Descripcion = ?, Volumen_m3 = ?, Cantidad = ?, Id_Cliente = ? WHERE Id_Paquete = ?";
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
                    ps.setInt(4, producto.getClienteId());
                    ps.setInt(5, producto.getIdProducto());
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
        String sqlGetAsignacion = "SELECT Id_Asignacion FROM ASIGNACION_PAQUETE WHERE Id_Paquete = ? LIMIT 1";
        String sqlDeleteHistorial = "DELETE FROM HISTORIAL_ESTADOS WHERE Id_Asig_Paq IN (SELECT Id_Asignacion_Paquete FROM ASIGNACION_PAQUETE WHERE Id_Paquete = ?)";
        String sqlDeleteAsigPaq = "DELETE FROM ASIGNACION_PAQUETE WHERE Id_Paquete = ?";
        String sqlDeleteAsignacionIfUnused = "DELETE FROM ASIGNACION WHERE Id_Asignacion = ? AND NOT EXISTS (SELECT 1 FROM ASIGNACION_PAQUETE WHERE Id_Asignacion = ?)";
        String sqlDeletePaquete = "DELETE FROM PAQUETE WHERE Id_Paquete = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return false;
            conn.setAutoCommit(false);

            Integer idAsignacion = null;
            try (PreparedStatement psGet = conn.prepareStatement(sqlGetAsignacion)) {
                psGet.setInt(1, id);
                try (ResultSet rs = psGet.executeQuery()) {
                    if (rs.next()) {
                        idAsignacion = rs.getInt("Id_Asignacion");
                    }
                }
            }

            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlDeleteHistorial)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlDeleteAsigPaq)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                if (idAsignacion != null) {
                    try (PreparedStatement psAsignacion = conn.prepareStatement(sqlDeleteAsignacionIfUnused)) {
                        psAsignacion.setInt(1, idAsignacion);
                        psAsignacion.setInt(2, idAsignacion);
                        psAsignacion.executeUpdate();
                    }
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