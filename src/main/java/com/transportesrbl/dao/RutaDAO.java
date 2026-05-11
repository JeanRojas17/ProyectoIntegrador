package com.transportesrbl.dao;

import com.transportesrbl.config.DatabaseConnection;
import com.transportesrbl.models.RutaSeguimiento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RutaDAO {

    private static final DateTimeFormatter FECHA_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private boolean seguimientoDisponible = true;

    public List<RutaSeguimiento> listarRutasActivas() {
        List<RutaSeguimiento> rutas = new ArrayList<>();
        String sql = "SELECT ap.Id_Asignacion_Paquete, a.Fecha_Asignacion, " +
                     "c.modelo_camion, COALESCE(con.nombre_completo, 'Sin conductor') AS conductor, " +
                     "COALESCE(ap.Dir_Entrega, 'Destino sin registrar') AS destino, " +
                     "COALESCE(paq.Descripcion, p.Nombre, 'Producto sin nombre') AS producto, " +
                     "COALESCE(cli.Nombre_Empresa, 'Cliente sin registrar') AS cliente, " +
                     "COALESCE(paq.Volumen_m3, 0) AS volumen, " +
                     "COALESCE(h.estado, 'Pendiente') AS estado " +
                     "FROM ASIGNACION_PAQUETE ap " +
                     "JOIN ASIGNACION a ON ap.Id_Asignacion = a.Id_Asignacion " +
                     "JOIN CAMIONES c ON a.Id_Camion = c.id_camion " +
                     "LEFT JOIN CONDUCTORES con ON a.Id_Conductor = con.id_conductor " +
                     "JOIN PAQUETE paq ON ap.Id_Paquete = paq.Id_Paquete " +
                     "LEFT JOIN CLIENTE cli ON paq.Id_Cliente = cli.Id_Cliente " +
                     "LEFT JOIN Paquete_Producto pp ON paq.Id_Paquete = pp.Id_Paquete " +
                     "LEFT JOIN Productos p ON pp.Id_Producto = p.Id_Producto " +
                     "LEFT JOIN ( " +
                     "  SELECT DISTINCT ON (Id_Asig_Paq) Id_Asig_Paq, estado " +
                     "  FROM HISTORIAL_ESTADOS " +
                     "  ORDER BY Id_Asig_Paq, Fecha DESC " +
                     ") h ON ap.Id_Asignacion_Paquete = h.Id_Asig_Paq " +
                     "ORDER BY a.Fecha_Asignacion DESC, ap.Id_Asignacion_Paquete DESC";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return rutas;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RutaSeguimiento ruta = new RutaSeguimiento();
                    ruta.setIdEntrega(rs.getInt("Id_Asignacion_Paquete"));
                    ruta.setProducto(rs.getString("producto"));
                    ruta.setCliente(rs.getString("cliente"));
                    ruta.setDestino(rs.getString("destino"));
                    ruta.setEstado(rs.getString("estado"));
                    ruta.setCamion(rs.getString("modelo_camion"));
                    ruta.setConductor(rs.getString("conductor"));
                    ruta.setVolumen(rs.getDouble("volumen"));
                    if (rs.getTimestamp("Fecha_Asignacion") != null) {
                        ruta.setFechaAsignacion(rs.getTimestamp("Fecha_Asignacion").toLocalDateTime().format(FECHA_FORMAT));
                    } else {
                        ruta.setFechaAsignacion("Sin fecha");
                    }
                    rutas.add(ruta);
                }
            }
            cargarUbicacionesReales(conn, rutas);
        } catch (SQLException e) {
            System.err.println("Error en RutaDAO (listarRutasActivas): " + e.getMessage());
        }

        return rutas;
    }

    private void cargarUbicacionesReales(Connection conn, List<RutaSeguimiento> rutas) {
        if (!seguimientoDisponible || rutas.isEmpty()) {
            return;
        }

        String sql = "SELECT Latitud, Longitud " +
                     "FROM SEGUIMIENTO_RUTA " +
                     "WHERE Id_Asig_Paq = ? " +
                     "ORDER BY Fecha_Registro DESC " +
                     "LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (RutaSeguimiento ruta : rutas) {
                ps.setInt(1, ruta.getIdEntrega());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ruta.setActualLat(rs.getDouble("Latitud"));
                        ruta.setActualLon(rs.getDouble("Longitud"));
                        ruta.setUbicacionReal(true);
                    }
                }
            }
        } catch (SQLException e) {
            seguimientoDisponible = false;
            System.err.println("Seguimiento GPS no disponible, se usara simulacion de rutas: " + e.getMessage());
        }
    }
}