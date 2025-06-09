package org.quevedo.proyectofinal3ev.DAO;

import org.quevedo.proyectofinal3ev.basedatos.ConnectionDB;
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.Usuario;
import org.quevedo.proyectofinal3ev.model.VisitaVeterinaria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisitaVeterinariaDAO {
    private static final String SQL_GET_ALL = "SELECT v.id, v.fecha, v.fecha_hora, v.motivo, v.observaciones, " +
            "m.id AS mascota_id, m.nombre AS mascota_nombre, " +
            "u.id AS veterinaria_id, u.nombre_usuario AS veterinaria_nombre " +
            "FROM VisitaVeterinaria v " +
            "JOIN Mascota m ON v.mascota_id = m.id " +
            "JOIN Usuario u ON v.veterinaria_id = u.id";
    private static final String SQL_INSERT = "INSERT INTO VisitaVeterinaria (fecha, fecha_hora, motivo, observaciones, mascota_id, veterinaria_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_DELETE = "DELETE FROM VisitaVeterinaria WHERE id = ?";
    private static final String SQL_FIND_BY_ID = "SELECT v.id, v.fecha, v.fecha_hora, v.motivo, v.observaciones, " +
            "m.id AS mascota_id, m.nombre AS mascota_nombre, " +
            "u.id AS veterinaria_id, u.nombre_usuario AS veterinaria_nombre " +
            "FROM VisitaVeterinaria v " +
            "JOIN Mascota m ON v.mascota_id = m.id " +
            "JOIN Usuario u ON v.veterinaria_id = u.id " +
            "WHERE v.id = ?";
    private static final String SQL_UPDATE = "UPDATE VisitaVeterinaria SET fecha = ?, fecha_hora = ?, motivo = ?, observaciones = ?, mascota_id = ?, veterinaria_id = ? WHERE id = ?";
    private static final String SQL_FIND_BY_MASCOTA_ID = "SELECT v.id, v.fecha, v.fecha_hora, v.motivo, v.observaciones, " +
            "m.id AS mascota_id, m.nombre AS mascota_nombre, " +
            "u.id AS veterinaria_id, u.nombre_usuario AS veterinaria_nombre " +
            "FROM VisitaVeterinaria v " +
            "JOIN Mascota m ON v.mascota_id = m.id " +
            "JOIN Usuario u ON v.veterinaria_id = u.id " +
            "WHERE v.mascota_id = ?";
    private static final String SQL_GET_TODAYS_VISITS =
            "SELECT v.id, v.fecha, v.fecha_hora, v.motivo, v.observaciones, " +
                    "m.id AS mascota_id, m.nombre AS mascota_nombre, " +
                    "duenio.id AS duenio_id, duenio.nombre_usuario AS duenio_nombre, " +
                    "u.id AS veterinaria_id, u.nombre_usuario AS veterinaria_nombre " +
                    "FROM VisitaVeterinaria v " +
                    "JOIN Mascota m ON v.mascota_id = m.id " +
                    "JOIN Usuario duenio ON m.duenio_id = duenio.id " +
                    "JOIN Usuario u ON v.veterinaria_id = u.id " +
                    "WHERE v.fecha = CURRENT_DATE";

    // Consulta para citas del día de una veterinaria concreta
    private static final String SQL_GET_TODAYS_VISITS_BY_VET =
            "SELECT v.id, v.fecha, v.fecha_hora, v.motivo, v.observaciones, " +
            "m.id AS mascota_id, m.nombre AS mascota_nombre, " +
            "duenio.id AS duenio_id, duenio.nombre_usuario AS duenio_nombre, " +
            "u.id AS veterinaria_id, u.nombre_usuario AS veterinaria_nombre " +
            "FROM VisitaVeterinaria v " +
            "JOIN Mascota m ON v.mascota_id = m.id " +
            "JOIN Usuario duenio ON m.duenio_id = duenio.id " +
            "JOIN Usuario u ON v.veterinaria_id = u.id " +
            "WHERE DATE(v.fecha_hora) = CURRENT_DATE AND v.veterinaria_id = ?";

    // Consulta para historial de citas de una veterinaria concreta
    private static final String SQL_GET_ALL_VISITS_BY_VET =
            "SELECT v.id, v.fecha, v.fecha_hora, v.motivo, v.observaciones, " +
            "m.id AS mascota_id, m.nombre AS mascota_nombre, " +
            "duenio.id AS duenio_id, duenio.nombre_usuario AS duenio_nombre, " +
            "u.id AS veterinaria_id, u.nombre_usuario AS veterinaria_nombre " +
            "FROM VisitaVeterinaria v " +
            "JOIN Mascota m ON v.mascota_id = m.id " +
            "JOIN Usuario duenio ON m.duenio_id = duenio.id " +
            "JOIN Usuario u ON v.veterinaria_id = u.id " +
            "WHERE v.veterinaria_id = ?";

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

                VisitaVeterinaria visita = new VisitaVeterinaria();
                visita.setId(rs.getInt("id"));
                visita.setFecha(rs.getDate("fecha").toLocalDate());
                Timestamp fechaHora = rs.getTimestamp("fecha_hora");
                if (fechaHora != null) {
                    visita.setFechaHora(fechaHora.toLocalDateTime());
                }
                visita.setMotivo(rs.getString("motivo"));
                visita.setObservaciones(rs.getString("observaciones"));
                visita.setMascota(mascota);
                visita.setVeterinaria(veterinaria);

                visitas.add(visita);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todas las visitas: " + e.getMessage(), e);
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

                    visita = new VisitaVeterinaria();
                    visita.setId(rs.getInt("id"));
                    visita.setFecha(rs.getDate("fecha").toLocalDate());
                    Timestamp fechaHora = rs.getTimestamp("fecha_hora");
                    if (fechaHora != null) {
                        visita.setFechaHora(fechaHora.toLocalDateTime());
                    }
                    visita.setMotivo(rs.getString("motivo"));
                    visita.setObservaciones(rs.getString("observaciones"));
                    visita.setMascota(mascota);
                    visita.setVeterinaria(veterinaria);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la visita por ID: " + e.getMessage(), e);
        }
        return visita;
    }

    public static VisitaVeterinaria insert(VisitaVeterinaria visita) {
        if (visita != null) {
            try (Connection connection = ConnectionDB.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setDate(1, Date.valueOf(visita.getFecha()));
                if (visita.getFechaHora() != null) {
                    pstmt.setTimestamp(2, Timestamp.valueOf(visita.getFechaHora()));
                } else {
                    pstmt.setNull(2, Types.TIMESTAMP);
                }
                pstmt.setString(3, visita.getMotivo());
                pstmt.setString(4, visita.getObservaciones());
                pstmt.setInt(5, visita.getMascota().getId());
                pstmt.setInt(6, visita.getVeterinaria().getId());

                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        visita.setId(generatedKeys.getInt(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error al insertar la visita: " + e.getMessage(), e);
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
            throw new RuntimeException("Error al eliminar la visita: " + e.getMessage(), e);
        }
        return deleted;
    }

    public static boolean update(VisitaVeterinaria visita) {
        boolean updated = false;
        if (visita != null) {
            try (Connection connection = ConnectionDB.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(SQL_UPDATE)) {

                pstmt.setDate(1, Date.valueOf(visita.getFecha()));
                if (visita.getFechaHora() != null) {
                    pstmt.setTimestamp(2, Timestamp.valueOf(visita.getFechaHora()));
                } else {
                    pstmt.setNull(2, Types.TIMESTAMP);
                }
                pstmt.setString(3, visita.getMotivo());
                pstmt.setString(4, visita.getObservaciones());
                pstmt.setInt(5, visita.getMascota().getId());
                pstmt.setInt(6, visita.getVeterinaria().getId());
                pstmt.setInt(7, visita.getId());

                updated = pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar la visita: " + e.getMessage(), e);
            }
        }
        return updated;
    }

    public static List<VisitaVeterinaria> getVisitasByMascotaId(int mascotaId) {
        List<VisitaVeterinaria> visitas = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_FIND_BY_MASCOTA_ID)) {

            pstmt.setInt(1, mascotaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Mascota mascota = new Mascota();
                    mascota.setId(rs.getInt("mascota_id"));
                    mascota.setNombre(rs.getString("mascota_nombre"));

                    Usuario veterinaria = new Usuario();
                    veterinaria.setId(rs.getInt("veterinaria_id"));
                    veterinaria.setNombreUsuario(rs.getString("veterinaria_nombre"));

                    VisitaVeterinaria visita = new VisitaVeterinaria();
                    visita.setId(rs.getInt("id"));
                    visita.setFecha(rs.getDate("fecha").toLocalDate());
                    Timestamp fechaHora = rs.getTimestamp("fecha_hora");
                    if (fechaHora != null) {
                        visita.setFechaHora(fechaHora.toLocalDateTime());
                    }
                    visita.setMotivo(rs.getString("motivo"));
                    visita.setObservaciones(rs.getString("observaciones"));
                    visita.setMascota(mascota);
                    visita.setVeterinaria(veterinaria);

                    visitas.add(visita);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener visitas por ID de mascota: " + e.getMessage(), e);
        }
        return visitas;
    }

    public static List<VisitaVeterinaria> getVisitasDelDia() {
        List<VisitaVeterinaria> visitas = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_GET_TODAYS_VISITS);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Mascota mascota = new Mascota();
                mascota.setId(rs.getInt("mascota_id"));
                mascota.setNombre(rs.getString("mascota_nombre"));

                Usuario duenio = new Usuario();
                duenio.setId(rs.getInt("duenio_id"));
                duenio.setNombreUsuario(rs.getString("duenio_nombre"));

                Usuario veterinaria = new Usuario();
                veterinaria.setId(rs.getInt("veterinaria_id"));
                veterinaria.setNombreUsuario(rs.getString("veterinaria_nombre"));

                VisitaVeterinaria visita = new VisitaVeterinaria();
                visita.setId(rs.getInt("id"));
                visita.setFecha(rs.getDate("fecha").toLocalDate());
                Timestamp fechaHora = rs.getTimestamp("fecha_hora");
                if (fechaHora != null) {
                    visita.setFechaHora(fechaHora.toLocalDateTime());
                }
                visita.setMotivo(rs.getString("motivo"));
                visita.setObservaciones(rs.getString("observaciones"));
                visita.setMascota(mascota);
                visita.setVeterinaria(veterinaria);

                visitas.add(visita);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener las visitas del día: " + e.getMessage(), e);
        }
        return visitas;
    }

    public static List<VisitaVeterinaria> getVisitasDelDiaPorVeterinaria(int veterinariaId) {
        List<VisitaVeterinaria> visitas = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_GET_TODAYS_VISITS_BY_VET)) {
            pstmt.setInt(1, veterinariaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Mascota mascota = new Mascota();
                    mascota.setId(rs.getInt("mascota_id"));
                    mascota.setNombre(rs.getString("mascota_nombre"));

                    Usuario duenio = new Usuario();
                    duenio.setId(rs.getInt("duenio_id"));
                    duenio.setNombreUsuario(rs.getString("duenio_nombre"));
                    mascota.setDuenioMascota(duenio);

                    Usuario veterinaria = new Usuario();
                    veterinaria.setId(rs.getInt("veterinaria_id"));
                    veterinaria.setNombreUsuario(rs.getString("veterinaria_nombre"));

                    VisitaVeterinaria visita = new VisitaVeterinaria();
                    // Usar fecha_hora para ambos campos
                    Timestamp fechaHora = rs.getTimestamp("fecha_hora");
                    if (fechaHora != null) {
                        visita.setFechaHora(fechaHora.toLocalDateTime());
                        visita.setFecha(fechaHora.toLocalDateTime().toLocalDate());
                    } else {
                        visita.setFecha(rs.getDate("fecha").toLocalDate());
                    }
                    visita.setId(rs.getInt("id"));
                    visita.setMotivo(rs.getString("motivo"));
                    visita.setObservaciones(rs.getString("observaciones"));
                    visita.setMascota(mascota);
                    visita.setVeterinaria(veterinaria);

                    visitas.add(visita);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener las visitas del día para la veterinaria: " + e.getMessage(), e);
        }
        return visitas;
    }

    public static List<VisitaVeterinaria> getAllVisitasPorVeterinaria(int veterinariaId) {
        List<VisitaVeterinaria> visitas = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_GET_ALL_VISITS_BY_VET)) {
            pstmt.setInt(1, veterinariaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Mascota mascota = new Mascota();
                    mascota.setId(rs.getInt("mascota_id"));
                    mascota.setNombre(rs.getString("mascota_nombre"));

                    Usuario duenio = new Usuario();
                    duenio.setId(rs.getInt("duenio_id"));
                    duenio.setNombreUsuario(rs.getString("duenio_nombre"));
                    mascota.setDuenioMascota(duenio);

                    Usuario veterinaria = new Usuario();
                    veterinaria.setId(rs.getInt("veterinaria_id"));
                    veterinaria.setNombreUsuario(rs.getString("veterinaria_nombre"));

                    VisitaVeterinaria visita = new VisitaVeterinaria();
                    // Usar fecha_hora para ambos campos
                    Timestamp fechaHora = rs.getTimestamp("fecha_hora");
                    if (fechaHora != null) {
                        visita.setFechaHora(fechaHora.toLocalDateTime());
                        visita.setFecha(fechaHora.toLocalDateTime().toLocalDate());
                    } else {
                        visita.setFecha(rs.getDate("fecha").toLocalDate());
                    }
                    visita.setId(rs.getInt("id"));
                    visita.setMotivo(rs.getString("motivo"));
                    visita.setObservaciones(rs.getString("observaciones"));
                    visita.setMascota(mascota);
                    visita.setVeterinaria(veterinaria);

                    visitas.add(visita);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todas las visitas para la veterinaria: " + e.getMessage(), e);
        }
        return visitas;
    }
}