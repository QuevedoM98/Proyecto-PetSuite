package org.quevedo.proyectofinal3ev.DAO;

import org.quevedo.proyectofinal3ev.basedatos.ConnectionDB;
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.Usuario;
import org.quevedo.proyectofinal3ev.model.VisitaVeterinaria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO (Data Access Object) para la entidad {@link VisitaVeterinaria}.
 * Proporciona métodos para interactuar con la base de datos y realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * y otras consultas relacionadas con las visitas veterinarias.
 */
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

    /**
     * Obtiene una lista de todas las visitas veterinarias registradas en la base de datos.
     *
     * @return Una lista de objetos {@link VisitaVeterinaria} con todas las visitas.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
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

    /**
     * Busca una visita veterinaria por su ID en la base de datos.
     *
     * @param id El ID de la visita a buscar.
     * @return El objeto {@link VisitaVeterinaria} si se encuentra, o {@code null} si no existe ninguna visita con el ID dado.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
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

    /**
     * Inserta una nueva visita veterinaria en la base de datos.
     * Si la inserción es exitosa, el ID generado para la visita se establece en el objeto {@code visita} proporcionado.
     *
     * @param visita El objeto {@link VisitaVeterinaria} a insertar.
     * @return El objeto {@link VisitaVeterinaria} con el ID asignado si la inserción fue exitosa, o {@code null} si el objeto de entrada era nulo.
     * @throws RuntimeException Si ocurre un error al insertar la visita en la base de datos.
     */
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

    /**
     * Elimina una visita veterinaria de la base de datos por su ID.
     *
     * @param id El ID de la visita a eliminar.
     * @return {@code true} si la visita fue eliminada con éxito, {@code false} en caso contrario.
     * @throws RuntimeException Si ocurre un error al eliminar la visita de la base de datos.
     */
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

    /**
     * Actualiza una visita veterinaria existente en la base de datos.
     *
     * @param visita El objeto {@link VisitaVeterinaria} con los datos actualizados. El ID de la visita se utiliza para identificar el registro a actualizar.
     * @return {@code true} si la visita fue actualizada con éxito, {@code false} en caso contrario.
     * @throws RuntimeException Si ocurre un error al actualizar la visita en la base de datos.
     */
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

    /**
     * Obtiene una lista de visitas veterinarias asociadas a una mascota específica.
     *
     * @param mascotaId El ID de la mascota de la que se desean obtener las visitas.
     * @return Una lista de objetos {@link VisitaVeterinaria} que pertenecen a la mascota con el ID dado.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
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

    /**
     * Obtiene una lista de todas las visitas veterinarias programadas para el día actual.
     *
     * @return Una lista de objetos {@link VisitaVeterinaria} que representan las visitas del día.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
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

    /**
     * Obtiene una lista de citas de visitas veterinarias programadas para el día actual y para una veterinaria específica.
     *
     * @param veterinariaId El ID de la veterinaria de la que se desean obtener las citas del día.
     * @return Una lista de objetos {@link VisitaVeterinaria} que representan las citas del día para la veterinaria.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
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

    /**
     * Obtiene el historial completo de visitas veterinarias asociadas a una veterinaria específica.
     *
     * @param veterinariaId El ID de la veterinaria de la que se desea obtener el historial de visitas.
     * @return Una lista de objetos {@link VisitaVeterinaria} que forman parte del historial de visitas de la veterinaria.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
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