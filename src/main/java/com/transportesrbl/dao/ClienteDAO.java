package com.transportesrbl.dao;

import com.transportesrbl.config.Constantes;
import com.transportesrbl.config.DatabaseConnection;
import com.transportesrbl.models.Cliente;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClienteDAO {

    public Cliente getClienteByUsuarioId(int idUsuario) {
        String sql = "SELECT * FROM CLIENTE WHERE id_usuario = ?"; 
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                        rs.getInt("Id_Cliente"),
                        rs.getString("Nombre_Empresa"),
                        rs.getString("Contacto"),
                        rs.getInt("id_usuario")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cliente por idUsuario: " + e.getMessage());
        }
        return null;
    }

    public Map<String, Object> getMetricas(int idCliente) {
        Map<String, Object> metricas = new HashMap<>();
        String sql = "SELECT " +
                     "  COUNT(CASE WHEN h.estado IN ('En reparto', 'Siguiente') THEN 1 END) as en_camino, " +
                     "  COUNT(CASE WHEN h.estado = 'Entregado' THEN 1 END) as entregados, " +
                     "  SUM(p.Volumen_m3) as volumen_total " +
                     "FROM PAQUETE p " +
                     "LEFT JOIN ASIGNACION_PAQUETE ap ON p.Id_Paquete = ap.Id_Paquete " +
                      "LEFT JOIN (" + Constantes.SQL_HISTORIAL_RECIENTE_SIN_FECHA + ") h " +
                      "ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq " +
                      "WHERE p.Id_Cliente = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    metricas.put("en_camino", rs.getInt("en_camino"));
                    metricas.put("entregados", rs.getInt("entregados"));
                    metricas.put("volumen_total", rs.getDouble("volumen_total"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en getMetricas: " + e.getMessage());
        }
        return metricas;
    }

    public List<String> getNovedades(int idCliente) {
        List<String> novedades = new ArrayList<>();
        String sql = "SELECT h.Estado, h.Observacion, p.Nro_Paquete, h.Fecha " +
                     "FROM HISTORIAL_ESTADOS h " +
                     "JOIN ASIGNACION_PAQUETE ap ON h.Id_Asig_Paq = ap.Id_Asignacion_Paquete " +
                     "JOIN PAQUETE p ON ap.Id_Paquete = p.Id_Paquete " +
                     "WHERE p.Id_Cliente = ? " +
                     "ORDER BY h.Fecha DESC LIMIT 15";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String estado = rs.getString("Estado");
                    String nro = rs.getString("Nro_Paquete");
                    String obs = rs.getString("Observacion");
                    Timestamp fecha = rs.getTimestamp("Fecha");
                    String mensaje;

                    switch (estado != null ? estado.toLowerCase() : "") {
                        case "en ruta":
                            mensaje = "Su pedido " + nro + " está en camino en ruta.";
                            break;
                        case "en reparto":
                            mensaje = "Su pedido " + nro + " está transitando hacia su destino.";
                            break;
                        case "cancelado":
                            mensaje = "Su pedido " + nro + " ha sido cancelado.";
                            break;
                        case "pendiente":
                            mensaje = "Su pedido " + nro + " está en espera.";
                            break;
                        case "entregado":
                            mensaje = "Su pedido " + nro + " ha sido entregado exitosamente.";
                            break;
                        case "no entregado":
                            mensaje = "Su pedido " + nro + " no pudo ser entregado.";
                            break;
                        case "cargado":
                            mensaje = "Su pedido " + nro + " ya ha sido cargado al vehículo.";
                            break;
                        default:
                            mensaje = "El estado de su pedido " + nro + " ha cambiado a: " + (estado != null ? estado : "Desconocido") + ".";
                            break;
                    }

                    if (obs != null && !obs.isEmpty() && !obs.equalsIgnoreCase("Asignación inicial") && !obs.equalsIgnoreCase("Actualización manual desde Dashboard")) {
                        mensaje += "\nNota: " + obs;
                    }

                    String prefijo = (fecha != null) ? "[" + sdf.format(fecha) + "] " : "";
                    novedades.add(prefijo + mensaje);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en getNovedades: " + e.getMessage());
        }
        return novedades;
    }

    public List<Map<String, Object>> getPaquetesActivos(int idCliente) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT p.Id_Paquete, p.Nro_Paquete, p.Descripcion, p.Volumen_m3, " +
                     "ap.Dir_Entrega, ap.Id_Asignacion_Paquete, " +
                     "COALESCE(h.estado, 'Pendiente') as estado " +
                     "FROM PAQUETE p " +
                     "LEFT JOIN ASIGNACION_PAQUETE ap ON p.Id_Paquete = ap.Id_Paquete " +
                      "LEFT JOIN (" + Constantes.SQL_HISTORIAL_RECIENTE_SIN_FECHA + ") h " +
                       "ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq " +
                      "WHERE p.Id_Cliente = ? " +
                     "ORDER BY p.Id_Paquete DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> pkg = new HashMap<>();
                    pkg.put("id_paquete", rs.getInt("Id_Paquete"));
                    pkg.put("nro_paquete", rs.getString("Nro_Paquete"));
                    pkg.put("descripcion", rs.getString("Descripcion"));
                    pkg.put("volumen", rs.getDouble("Volumen_m3"));
                    pkg.put("direccion", rs.getString("Dir_Entrega"));
                    pkg.put("id_asig_paq", rs.getInt("Id_Asignacion_Paquete"));
                    pkg.put("estado", rs.getString("estado"));
                    lista.add(pkg);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en getPaquetesActivos: " + e.getMessage());
        }
        return lista;
    }

    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM CLIENTE ORDER BY Id_Cliente DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cliente(
                    rs.getInt("Id_Cliente"),
                    rs.getString("Nombre_Empresa"),
                    rs.getString("Contacto"),
                    rs.getObject("id_usuario") != null ? rs.getInt("id_usuario") : null
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error en listarTodos clientes: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(Cliente c, String usuario, String contrasena) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Crear usuario
            String sqlUser = "INSERT INTO usuario (nombre, usuario, contrasena, id_rol) VALUES (?, ?, ?, ?) RETURNING id_usuario";
            int idUsuario = -1;
            try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                ps.setString(1, c.getContacto());
                ps.setString(2, usuario);
                ps.setString(3, contrasena);
                ps.setInt(4, Constantes.ID_ROL_CLIENTE);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) idUsuario = rs.getInt(1);
            }

            // 2. Crear cliente vinculado
            if (idUsuario != -1) {
                String sqlCliente = "INSERT INTO CLIENTE (Nombre_Empresa, Contacto, id_usuario) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlCliente)) {
                    ps.setString(1, c.getNombreEmpresa());
                    ps.setString(2, c.getContacto());
                    ps.setInt(3, idUsuario);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Error al insertar cliente y usuario: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public boolean actualizar(Cliente c) {
        String sql = "UPDATE CLIENTE SET Nombre_Empresa = ?, Contacto = ? WHERE Id_Cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombreEmpresa());
            ps.setString(2, c.getContacto());
            ps.setInt(3, c.getIdCliente());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idCliente) {
        // Primero obtener el id_usuario para borrarlo también si se desea, 
        // o solo borrar el cliente (dependiendo de la integridad referencial)
        String sqlGet = "SELECT id_usuario FROM CLIENTE WHERE Id_Cliente = ?";
        String sqlDelCliente = "DELETE FROM CLIENTE WHERE Id_Cliente = ?";
        String sqlDelUser = "DELETE FROM usuario WHERE id_usuario = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int idUser = -1;
            try (PreparedStatement ps = conn.prepareStatement(sqlGet)) {
                ps.setInt(1, idCliente);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) idUser = rs.getInt(1);
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlDelCliente)) {
                ps.setInt(1, idCliente);
                ps.executeUpdate();
            }

            if (idUser != -1) {
                try (PreparedStatement ps = conn.prepareStatement(sqlDelUser)) {
                    ps.setInt(1, idUser);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }
}
