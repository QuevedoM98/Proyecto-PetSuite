package org.quevedo.proyectofinal3ev.DAO;

import org.quevedo.proyectofinal3ev.basedatos.ConnectionDB;
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.Usuario;
import org.quevedo.proyectofinal3ev.model.VisitaVeterinaria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisitaVeterinariaDAO {
    private static final String SQL_GET_ALL = "SELECT v.id, v.fecha, v.motivo, v.observaciones, " +
            "m.id AS mascota_id, m.nombre AS mascota_nombre, " +
            "u.id AS veterinaria_id, u.nombre_usuario AS veterinaria_nombre " +
            "FROM VisitaVeterinaria v " +
            "JOIN Mascota m ON v.mascota_id = m.id " +
            "JOIN Usuario u ON v.veterinaria_id = u.id";
    private static final String SQL_INSERT = "INSERT INTO VisitaVeterinaria (fecha, motivo, observaciones, mascota_id, veterinaria_id) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_DELETE = "DELETE FROM VisitaVeterinaria WHERE id = ?";
    private static final String SQL_FIND_BY_ID = "SELECT v.id, v.fecha, v.motivo, v.observaciones, " +
            "m.id AS mascota_id, m.nombre AS mascota_nombre, " +
            "u.id AS veterinaria_id, u.nombre_usuario AS veterinaria_nombre " +
            "FROM VisitaVeterinaria v " +
            "JOIN Mascota m ON v.mascota_id = m.id " +
            "JOIN Usuario u ON v.veterinaria_id = u.id " +
            "WHERE v.id = ?";
    private static final String SQL_UPDATE = "UPDATE VisitaVeterinaria SET fecha = ?, motivo = ?, observaciones = ?, mascota_id = ?, veterinaria_id = ? WHERE id = ?";
    private static final String SQL_FIND_BY_MASCOTA_ID = "SELECT v.id, v.fecha, v.motivo, v.observaciones, " +
            "m.id AS mascota_id, m.nombre AS mascota_nombre, " +
            "u.id AS veterinaria_id, u.nombre_usuario AS veterinaria_nombre " +
            "FROM VisitaVeterinaria v " +
            "JOIN Mascota m ON v.mascota_id = m.id " +
            "JOIN Usuario u ON v.veterinaria_id = u.id " +
            "WHERE v.mascota_id = ?";

    public static List<VisitaVeterinaria> getAllVisitas() {
        List<VisitaVeterinaria> visitas = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_GET_ALL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Mascota mascota = new Mascota();
                mascota.setId(rs.getInt("mascota_id"));
                mascota.setNombre(rs.getString("mascota_nombre"));

                Usuario veterinaria = new Usuario();
                veterinaria.setId(rs.getInt("veterinaria_id"));
                veterinaria.setNombreUsuario(rs.getString("veterinaria_nombre"));

                VisitaVeterinaria visita = new VisitaVeterinaria(
                        rs.getDate("fecha").toLocalDate(),
                        rs.getString("motivo"),
                        rs.getString("observaciones"),
                        mascota,
                        veterinaria
                );
                visitas.add(visita);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todas las visitas veterinarias: " + e.getMessage(), e);
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
                    mascota.setNombre(rs.getString("mascota_nombre"));

                    Usuario veterinaria = new Usuario();
                    veterinaria.setId(rs.getInt("veterinaria_id"));
                    veterinaria.setNombreUsuario(rs.getString("veterinaria_nombre"));

                    visita = new VisitaVeterinaria(
                            rs.getDate("fecha").toLocalDate(),
                            rs.getString("motivo"),
                            rs.getString("observaciones"),
                            mascota,
                            veterinaria
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la visita veterinaria por ID: " + e.getMessage(), e);
        }
        return visita;
    }

    public static VisitaVeterinaria insert(VisitaVeterinaria visita) {
        if (visita != null) {
            try (Connection connection = ConnectionDB.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setDate(1, Date.valueOf(visita.getFecha()));
                pstmt.setString(2, visita.getMotivo());
                pstmt.setString(3, visita.getObservaciones());
                pstmt.setInt(4, visita.getMascota().getId());
                pstmt.setInt(5, visita.getVeterinaria().getId());
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        visita.setId(generatedKeys.getInt(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error al insertar la visita veterinaria: " + e.getMessage(), e);
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
            throw new RuntimeException("Error al eliminar la visita veterinaria: " + e.getMessage(), e);
        }
        return deleted;
    }

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

    public static List<VisitaVeterinaria> getVisitasByMascotaId(int mascotaId) {
        List<VisitaVeterinaria> visitas = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_FIND_BY_MASCOTA_ID)) {

            pstmt.setInt(1, mascotaId); // Asegúrate de pasar el ID correcto

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Mascota mascota = new Mascota();
                    mascota.setId(rs.getInt("mascota_id"));
                    mascota.setNombre(rs.getString("mascota_nombre"));

                    Usuario veterinaria = new Usuario();
                    veterinaria.setId(rs.getInt("veterinaria_id"));
                    veterinaria.setNombreUsuario(rs.getString("veterinaria_nombre"));

                    VisitaVeterinaria visita = new VisitaVeterinaria(
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
            throw new RuntimeException("Error al obtener las visitas por ID de mascota: " + e.getMessage(), e);
        }
        return visitas;
    }
}