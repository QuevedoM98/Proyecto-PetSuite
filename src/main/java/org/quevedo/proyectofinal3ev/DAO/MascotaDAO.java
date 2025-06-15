package org.quevedo.proyectofinal3ev.DAO;

import org.quevedo.proyectofinal3ev.basedatos.ConnectionDB;
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO encargada de gestionar las operaciones de base de datos relacionadas con la entidad {@link Mascota}.
 */
public class MascotaDAO {
    // Consultas SQL utilizadas por esta clase
    private static final String SQL_INSERT = "INSERT INTO Mascota (nombre, especie, raza, fechaNacimiento, duenio_id) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_FIND_BY_ID = "SELECT m.*, u.id AS duenio_id, u.nombre_usuario AS duenio_nombre " +
            "FROM Mascota m " +
            "JOIN Usuario u ON m.duenio_id = u.id " +
            "WHERE m.id = ?";
    private static final String SQL_FIND_BY_USUARIO_ID = "SELECT m.*, u.id AS duenio_id, u.nombre_usuario AS duenio_nombre " +
            "FROM Mascota m " +
            "JOIN Usuario u ON m.duenio_id = u.id " +
            "WHERE m.duenio_id = ?";
    private static final String SQL_UPDATE = "UPDATE Mascota SET nombre = ?, especie = ?, raza = ?, fechaNacimiento = ?, duenio_id = ? WHERE id = ?";
    private static final String SQL_GET_ALL = "SELECT m.*, u.id AS duenio_id, u.nombre_usuario AS duenio_nombre " +
            "FROM Mascota m " +
            "JOIN Usuario u ON m.duenio_id = u.id";
    private static final String SQL_DELETE = "DELETE FROM Mascota WHERE id = ?";

    /**
     * Valida que la conexión a la base de datos esté activa.
     *
     * @param connection conexión a validar.
     * @throws SQLException si la conexión es nula o está cerrada.
     */
    private static void validateConnection(Connection connection) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("La conexión a la base de datos no está disponible o ya está cerrada.");
        }
    }

    /**
     * Inserta una nueva mascota en la base de datos.
     *
     * @param mascota Objeto {@link Mascota} a insertar.
     * @return La mascota insertada con su ID actualizado, o {@code null} si no se pudo insertar.
     * @throws RuntimeException si ocurre un error durante la inserción.
     */
    public static Mascota insert(Mascota mascota) {
        if (mascota != null) {
            try (Connection connection = ConnectionDB.getConnection();
                 PreparedStatement pst = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

                validateConnection(connection);

                pst.setString(1, mascota.getNombre());
                pst.setString(2, mascota.getEspecie());
                pst.setString(3, mascota.getRaza());
                pst.setDate(4, Date.valueOf(mascota.getFechaNacimiento()));
                pst.setInt(5, mascota.getDuenioId());

                int affectedRows = pst.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            mascota.setId(generatedKeys.getInt(1));
                        }
                    }
                } else {
                    mascota = null;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error al insertar la mascota: " + e.getMessage(), e);
            }
        } else {
            mascota = null;
        }
        return mascota;
    }

    /**
     * Busca una mascota en la base de datos por su ID.
     *
     * @param id Identificador único de la mascota.
     * @return La mascota encontrada, o {@code null} si no existe.
     * @throws RuntimeException si ocurre un error durante la búsqueda.
     */
    public static Mascota findById(int id) {
        Mascota mascota = null;

        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pst = connection.prepareStatement(SQL_FIND_BY_ID)) {

            validateConnection(connection);

            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Usuario duenio = new Usuario();
                    duenio.setId(rs.getInt("duenio_id"));
                    duenio.setNombreUsuario(rs.getString("duenio_nombre"));

                    mascota = new Mascota(
                            rs.getString("nombre"),
                            rs.getString("especie"),
                            rs.getString("raza"),
                            rs.getDate("fechanacimiento").toLocalDate(),
                            duenio
                    );
                    mascota.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la mascota por ID: " + e.getMessage(), e);
        }

        return mascota;
    }

    /**
     * Obtiene todas las mascotas registradas por un usuario específico.
     *
     * @param usuarioId ID del usuario propietario de las mascotas.
     * @return Lista de mascotas asociadas al usuario.
     * @throws RuntimeException si ocurre un error al obtener los datos.
     */
    public static List<Mascota> getMascotasByUsuarioId(int usuarioId) {
        List<Mascota> mascotas = new ArrayList<>();

        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pst = connection.prepareStatement(SQL_FIND_BY_USUARIO_ID)) {

            validateConnection(connection);

            pst.setInt(1, usuarioId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Usuario duenio = new Usuario();
                    duenio.setId(rs.getInt("duenio_id"));
                    duenio.setNombreUsuario(rs.getString("duenio_nombre"));

                    Mascota mascota = new Mascota(
                            rs.getString("nombre"),
                            rs.getString("especie"),
                            rs.getString("raza"),
                            rs.getDate("fechanacimiento").toLocalDate(),
                            duenio
                    );
                    mascota.setId(rs.getInt("id"));
                    mascotas.add(mascota);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener las mascotas por ID de usuario: " + e.getMessage(), e);
        }

        return mascotas;
    }

    /**
     * Elimina una mascota de la base de datos por su ID.
     *
     * @param id Identificador único de la mascota a eliminar.
     * @return {@code true} si la eliminación fue exitosa; {@code false} en caso contrario.
     * @throws RuntimeException si ocurre un error durante la eliminación.
     */
    public static boolean delete(int id) {
        boolean deleted = false;

        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pst = connection.prepareStatement(SQL_DELETE)) {

            validateConnection(connection);

            pst.setInt(1, id);
            deleted = pst.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la mascota: " + e.getMessage(), e);
        }

        return deleted;
    }

    /**
     * Actualiza los datos de una mascota existente en la base de datos.
     *
     * @param mascota Objeto {@link Mascota} con los datos actualizados.
     * @return {@code true} si la actualización fue exitosa; {@code false} en caso contrario.
     * @throws RuntimeException si ocurre un error durante la actualización.
     */
    public static boolean update(Mascota mascota) {
        boolean updated = false;

        if (mascota != null) {
            try (Connection connection = ConnectionDB.getConnection();
                 PreparedStatement pst = connection.prepareStatement(SQL_UPDATE)) {

                validateConnection(connection);

                pst.setString(1, mascota.getNombre());
                pst.setString(2, mascota.getEspecie());
                pst.setString(3, mascota.getRaza());
                pst.setDate(4, Date.valueOf(mascota.getFechaNacimiento()));
                pst.setInt(5, mascota.getDuenioId());
                pst.setInt(6, mascota.getId());

                updated = pst.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar la mascota: " + e.getMessage(), e);
            }
        }

        return updated;
    }

    /**
     * Recupera todas las mascotas registradas en la base de datos.
     *
     * @return Lista de todas las mascotas.
     * @throws RuntimeException si ocurre un error durante la recuperación.
     */
    public static List<Mascota> getAllMascotas() {
        List<Mascota> mascotas = new ArrayList<>();

        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pst = connection.prepareStatement(SQL_GET_ALL);
             ResultSet rs = pst.executeQuery()) {

            validateConnection(connection);

            while (rs.next()) {
                Usuario duenio = new Usuario();
                duenio.setId(rs.getInt("duenio_id"));
                duenio.setNombreUsuario(rs.getString("duenio_nombre"));

                Mascota mascota = new Mascota(
                        rs.getString("nombre"),
                        rs.getString("especie"),
                        rs.getString("raza"),
                        rs.getDate("fechaNacimiento").toLocalDate(),
                        duenio
                );
                mascota.setId(rs.getInt("id"));
                mascotas.add(mascota);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todas las mascotas: " + e.getMessage(), e);
        }

        return mascotas;
    }
}
