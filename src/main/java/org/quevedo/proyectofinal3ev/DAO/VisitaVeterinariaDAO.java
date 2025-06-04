package org.quevedo.proyectofinal3ev.DAO;

import org.quevedo.proyectofinal3ev.basedatos.ConnectionDB;
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.Usuario;
import org.quevedo.proyectofinal3ev.model.VisitaVeterinaria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisitaVeterinariaDAO {
    private final static String SQL_GET_ALL = "SELECT * FROM VisitaVeterinaria";
    private final static String SQL_INSERT = "INSERT INTO VisitaVeterinaria (fecha, motivo, observaciones, mascota_id, veterinaria_id) VALUES (?, ?, ?, ?, ?)";
    private final static String SQL_DELETE = "DELETE FROM VisitaVeterinaria WHERE id = ?";
    private final static String SQL_FIND_BY_ID = "SELECT * FROM VisitaVeterinaria WHERE id = ?";
    private final static String SQL_UPDATE = "UPDATE VisitaVeterinaria SET fecha = ?, motivo = ?, observaciones = ?, mascota_id = ?, veterinaria_id = ? WHERE id = ?";

    public static List<VisitaVeterinaria> getAllVisitas() {
        List<VisitaVeterinaria> visitas = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_GET_ALL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Mascota mascota = new Mascota();
                mascota.setId(rs.getInt("mascota_id"));

                Usuario veterinaria = UsuarioDAO.findById(rs.getInt("veterinaria_id"));

                VisitaVeterinaria visita = new VisitaVeterinaria(
                        rs.getInt("id"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getString("motivo"),
                        rs.getString("observaciones"),
                        mascota,
                        veterinaria
                );
                visitas.add(visita);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return visitas;
    }

    public static VisitaVeterinaria findById(int id) {
        VisitaVeterinaria visita = null;
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_FIND_BY_ID)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Mascota mascota = new Mascota();
                    mascota.setId(rs.getInt("mascota_id"));

                    Usuario veterinaria = UsuarioDAO.findById(rs.getInt("veterinaria_id"));

                    visita = new VisitaVeterinaria(
                            rs.getInt("id"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getString("motivo"),
                            rs.getString("observaciones"),
                            mascota,
                            veterinaria
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return visita;
    }

    public static VisitaVeterinaria insert(VisitaVeterinaria visita) {
        if (visita != null) {
            try (Connection connection = ConnectionDB.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(SQL_INSERT)) {
                pstmt.setDate(1, Date.valueOf(visita.getFecha()));
                pstmt.setString(2, visita.getMotivo());
                pstmt.setString(3, visita.getObservaciones());
                pstmt.setInt(4, visita.getMascota().getId());
                pstmt.setInt(5, visita.getVeterinaria().getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return visita;
    }

    public static boolean delete(int id) {
        boolean deleted = false;
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_DELETE)) {
            pstmt.setInt(1, id);
            deleted = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return deleted;
    }

    public static List<VisitaVeterinaria> getVisitasByUsuarioId(int id) {
        List<VisitaVeterinaria> visitas = new ArrayList<>();
        String sql = "SELECT * FROM VisitaVeterinaria WHERE veterinaria_id = ?";
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Mascota mascota = new Mascota();
                    mascota.setId(rs.getInt("mascota_id"));

                    Usuario veterinaria = UsuarioDAO.findById(rs.getInt("veterinaria_id"));

                    VisitaVeterinaria visita = new VisitaVeterinaria(
                            rs.getInt("id"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getString("motivo"),
                            rs.getString("observaciones"),
                            mascota,
                            veterinaria
                    );
                    visitas.add(visita);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener las visitas veterinarias: " + e.getMessage(), e);
        }
        return visitas;
    }

    /**
     * Actualiza los datos de una Visita Veterinaria en la base de datos.
     * @param visita El objeto VisitaVeterinaria con los datos actualizados.
     * @return True si la Visita fue actualizada exitosamente, false en caso contrario.
     */
    public static boolean update(VisitaVeterinaria visita) {
        boolean updated = false;
        if (visita != null) {
            try (Connection connection = ConnectionDB.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(SQL_UPDATE)) {

                pstmt.setDate(1, Date.valueOf(visita.getFecha()));
                pstmt.setString(2, visita.getMotivo());
                pstmt.setString(3, visita.getObservaciones());
                pstmt.setInt(4, visita.getMascota().getId());
                pstmt.setInt(5, visita.getVeterinaria().getId());
                pstmt.setInt(6, visita.getId());

                updated = pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar la visita veterinaria: " + e.getMessage(), e);
            }
        }
        return updated;
    }
}
