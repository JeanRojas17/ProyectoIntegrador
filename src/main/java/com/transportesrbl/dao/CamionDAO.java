package com.transportesrbl.dao;

import com.transportesrbl.config.DatabaseConnection;
import com.transportesrbl.models.Camion;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class CamionDAO {

    public List<Camion> listar() {
        List<Camion> lista = new ArrayList<>();
        String sql = "SELECT id_camion, modelo_camion, capacidad_m3, estado FROM CAMIONES ORDER BY id_camion DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Camion(
                    rs.getInt("id_camion"),
                    rs.getString("modelo_camion"),
                    rs.getDouble("capacidad_m3"),
                    rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean insertar(Camion camion) {
        String sql = "INSERT INTO CAMIONES (modelo_camion, capacidad_m3, estado) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, camion.getModelo());
            ps.setDouble(2, camion.getCapacidad());
            ps.setString(3, camion.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Camion camion) {
        String sql = "UPDATE CAMIONES SET modelo_camion = ?, capacidad_m3 = ?, estado = ? WHERE id_camion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, camion.getModelo());
            ps.setDouble(2, camion.getCapacidad());
            ps.setString(3, camion.getEstado());
            ps.setInt(4, camion.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sqlCheck = "SELECT COUNT(*) AS total FROM ASIGNACION WHERE Id_Camion = ?";
        String sqlDelete = "DELETE FROM CAMIONES WHERE id_camion = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return false;
            }

            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                psCheck.setInt(1, id);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt("total") > 0) {
                        System.err.println("No se puede eliminar el camión porque tiene asignaciones activas.");
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}