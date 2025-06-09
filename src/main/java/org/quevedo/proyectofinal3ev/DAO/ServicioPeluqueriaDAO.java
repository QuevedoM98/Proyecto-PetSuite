package org.quevedo.proyectofinal3ev.DAO;

import org.quevedo.proyectofinal3ev.basedatos.ConnectionDB;
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.ServicioPeluqueria;
import org.quevedo.proyectofinal3ev.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioPeluqueriaDAO {
    private static final String SQL_GET_ALL = "SELECT sp.id, sp.fecha, sp.fecha_hora, sp.tipoServicio, sp.precio, " +
            "m.id AS mascota_id, m.nombre AS mascota_nombre, m.especie AS mascota_especie, m.raza AS mascota_raza, " +
            "du.id AS duenio_id, du.nombre_usuario AS duenio_nombre, " +
            "u.id AS peluqueria_id, u.nombre_usuario AS peluqueria_nombre " +
            "FROM ServicioPeluqueria sp " +
            "JOIN Mascota m ON sp.mascota_id = m.id " +
            "JOIN Usuario du ON m.duenio_id = du.id " +
            "JOIN Usuario u ON sp.peluqueria_id = u.id";

    private static final String SQL_INSERT = "INSERT INTO ServicioPeluqueria (fecha, fecha_hora, tipoServicio, precio, mascota_id, peluqueria_id) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_DELETE = "DELETE FROM ServicioPeluqueria WHERE id = ?";

    private static final String SQL_FIND_BY_ID = "SELECT sp.id, sp.fecha, sp.fecha_hora, sp.tipoServicio, sp.precio, " +
            "m.id AS mascota_id, m.nombre AS mascota_nombre, " +
            "u.id AS peluqueria_id, u.nombre_usuario AS peluqueria_nombre " +
            "FROM ServicioPeluqueria sp " +
            "JOIN Mascota m ON sp.mascota_id = m.id " +
            "JOIN Usuario u ON sp.peluqueria_id = u.id " +
            "WHERE sp.id = ?";

    private static final String SQL_UPDATE = "UPDATE ServicioPeluqueria SET fecha = ?, fecha_hora = ?, tipoServicio = ?, precio = ?, mascota_id = ?, peluqueria_id = ? WHERE id = ?";

    private static final String SQL_FIND_BY_PELUQUERIA_ID = "SELECT sp.id, sp.fecha, sp.fecha_hora, sp.tipoServicio, sp.precio, " +
            "m.id AS mascota_id, m.nombre AS mascota_nombre, m.especie AS mascota_especie, m.raza AS mascota_raza, " +
            "du.id AS duenio_id, du.nombre_usuario AS duenio_nombre, " +
            "u.id AS peluqueria_id, u.nombre_usuario AS peluqueria_nombre " +
            "FROM ServicioPeluqueria sp " +
            "JOIN Mascota m ON sp.mascota_id = m.id " +
            "JOIN Usuario du ON m.duenio_id = du.id " +
            "JOIN Usuario u ON sp.peluqueria_id = u.id " +
            "WHERE sp.peluqueria_id = ?";

    private static final String SQL_GET_HISTORIAL_SERVICIOS = "SELECT sp.id, sp.fecha, sp.fecha_hora, sp.tipoServicio, sp.precio, " +
            "m.id AS mascota_id, m.nombre AS mascota_nombre, m.especie AS mascota_especie, m.raza AS mascota_raza, " +
            "du.id AS duenio_id, du.nombre_usuario AS duenio_nombre, " +
            "u.id AS peluqueria_id, u.nombre_usuario AS peluqueria_nombre " +
            "FROM ServicioPeluqueria sp " +
            "JOIN Mascota m ON sp.mascota_id = m.id " +
            "JOIN Usuario du ON m.duenio_id = du.id " +
            "JOIN Usuario u ON sp.peluqueria_id = u.id " +
            "WHERE sp.fecha < CURRENT_TIMESTAMP";

    private static final String SQL_GET_BY_MASCOTA_ID = "SELECT sp.id, sp.fecha, sp.fecha_hora, sp.tipoServicio, sp.precio, " +
            "m.id AS mascota_id, m.nombre AS mascota_nombre, m.especie AS mascota_especie, m.raza AS mascota_raza, " +
            "du.id AS duenio_id, du.nombre_usuario AS duenio_nombre, " +
            "u.id AS peluqueria_id, u.nombre_usuario AS peluqueria_nombre " +
            "FROM ServicioPeluqueria sp " +
            "JOIN Mascota m ON sp.mascota_id = m.id " +
            "JOIN Usuario du ON m.duenio_id = du.id " +
            "JOIN Usuario u ON sp.peluqueria_id = u.id " +
            "WHERE sp.mascota_id = ?";

    private static final String SQL_GET_CITAS_DIA_POR_PELUQUERIA =
            "SELECT sp.id, sp.fecha, sp.fecha_hora, sp.tipoServicio, sp.precio, " +
            "m.id AS mascota_id, m.nombre AS mascota_nombre, m.especie AS mascota_especie, m.raza AS mascota_raza, " +
            "du.id AS duenio_id, du.nombre_usuario AS duenio_nombre, " +
            "u.id AS peluqueria_id, u.nombre_usuario AS peluqueria_nombre " +
            "FROM ServicioPeluqueria sp " +
            "JOIN Mascota m ON sp.mascota_id = m.id " +
            "JOIN Usuario du ON m.duenio_id = du.id " +
            "JOIN Usuario u ON sp.peluqueria_id = u.id " +
            "WHERE DATE(sp.fecha_hora) = CURRENT_DATE AND sp.peluqueria_id = ?";

    private static final String SQL_GET_HISTORIAL_POR_PELUQUERIA =
            "SELECT sp.id, sp.fecha, sp.fecha_hora, sp.tipoServicio, sp.precio, " +
            "m.id AS mascota_id, m.nombre AS mascota_nombre, m.especie AS mascota_especie, m.raza AS mascota_raza, " +
            "du.id AS duenio_id, du.nombre_usuario AS duenio_nombre, " +
            "u.id AS peluqueria_id, u.nombre_usuario AS peluqueria_nombre " +
            "FROM ServicioPeluqueria sp " +
            "JOIN Mascota m ON sp.mascota_id = m.id " +
            "JOIN Usuario du ON m.duenio_id = du.id " +
            "JOIN Usuario u ON sp.peluqueria_id = u.id " +
            "WHERE sp.peluqueria_id = ?";

    public static List<ServicioPeluqueria> getAllServicios() {
        List<ServicioPeluqueria> servicios = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_GET_ALL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Usuario duenio = new Usuario();
                duenio.setId(rs.getInt("duenio_id"));
                duenio.setNombreUsuario(rs.getString("duenio_nombre"));

                Mascota mascota = new Mascota();
                mascota.setId(rs.getInt("mascota_id"));
                mascota.setNombre(rs.getString("mascota_nombre"));
                mascota.setEspecie(rs.getString("mascota_especie"));
                mascota.setRaza(rs.getString("mascota_raza"));
                mascota.setDuenioMascota(duenio);

                Usuario peluqueria = new Usuario();
                peluqueria.setId(rs.getInt("peluqueria_id"));
                peluqueria.setNombreUsuario(rs.getString("peluqueria_nombre"));

                ServicioPeluqueria servicio = new ServicioPeluqueria();
                servicio.setId(rs.getInt("id"));
                servicio.setFecha(rs.getDate("fecha").toLocalDate());
                Timestamp fechaHora = rs.getTimestamp("fecha_hora");
                if (fechaHora != null) {
                    servicio.setFechaHora(fechaHora.toLocalDateTime());
                }
                servicio.setTipoServicio(rs.getString("tipoServicio"));
                servicio.setPrecio(rs.getDouble("precio"));
                servicio.setMascota(mascota);
                servicio.setPeluqueria(peluqueria);

                servicios.add(servicio);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todos los servicios de peluquería: " + e.getMessage(), e);
        }
        return servicios;
    }

    public static List<ServicioPeluqueria> getServiciosByMascotaId(int mascotaId) {
        List<ServicioPeluqueria> servicios = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_GET_BY_MASCOTA_ID)) {

            pstmt.setInt(1, mascotaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Usuario duenio = new Usuario();
                    duenio.setId(rs.getInt("duenio_id"));
                    duenio.setNombreUsuario(rs.getString("duenio_nombre"));

                    Mascota mascota = new Mascota();
                    mascota.setId(rs.getInt("mascota_id"));
                    mascota.setNombre(rs.getString("mascota_nombre"));
                    mascota.setEspecie(rs.getString("mascota_especie"));
                    mascota.setRaza(rs.getString("mascota_raza"));
                    mascota.setDuenioMascota(duenio);

                    Usuario peluqueria = new Usuario();
                    peluqueria.setId(rs.getInt("peluqueria_id"));
                    peluqueria.setNombreUsuario(rs.getString("peluqueria_nombre"));

                    ServicioPeluqueria servicio = new ServicioPeluqueria();
                    servicio.setId(rs.getInt("id"));
                    servicio.setFecha(rs.getDate("fecha").toLocalDate());
                    Timestamp fechaHora = rs.getTimestamp("fecha_hora");
                    if (fechaHora != null) {
                        servicio.setFechaHora(fechaHora.toLocalDateTime());
                    }
                    servicio.setTipoServicio(rs.getString("tipoServicio"));
                    servicio.setPrecio(rs.getDouble("precio"));
                    servicio.setMascota(mascota);
                    servicio.setPeluqueria(peluqueria);

                    servicios.add(servicio);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener los servicios por ID de mascota: " + e.getMessage(), e);
        }
        return servicios;
    }

    public static void insert(ServicioPeluqueria servicio) {
        if (servicio != null) {
            try (Connection connection = ConnectionDB.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(SQL_INSERT)) {

                pstmt.setDate(1, Date.valueOf(servicio.getFecha()));
                if (servicio.getFechaHora() != null) {
                    pstmt.setTimestamp(2, Timestamp.valueOf(servicio.getFechaHora()));
                } else {
                    pstmt.setNull(2, Types.TIMESTAMP);
                }
                pstmt.setString(3, servicio.getTipoServicio());
                pstmt.setDouble(4, servicio.getPrecio());
                pstmt.setInt(5, servicio.getMascota().getId());
                pstmt.setInt(6, servicio.getPeluqueria().getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error al insertar el servicio de peluquería: " + e.getMessage(), e);
            }
        }
    }

    public static boolean delete(int id) {
        boolean deleted = false;
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_DELETE)) {

            pstmt.setInt(1, id);
            deleted = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el servicio de peluquería: " + e.getMessage(), e);
        }
        return deleted;
    }

    public static boolean update(ServicioPeluqueria servicio) {
        boolean updated = false;
        if (servicio != null) {
            try (Connection connection = ConnectionDB.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(SQL_UPDATE)) {

                pstmt.setDate(1, Date.valueOf(servicio.getFecha()));
                if (servicio.getFechaHora() != null) {
                    pstmt.setTimestamp(2, Timestamp.valueOf(servicio.getFechaHora()));
                } else {
                    pstmt.setNull(2, Types.TIMESTAMP);
                }
                pstmt.setString(3, servicio.getTipoServicio());
                pstmt.setDouble(4, servicio.getPrecio());
                pstmt.setInt(5, servicio.getMascota().getId());
                pstmt.setInt(6, servicio.getPeluqueria().getId());
                pstmt.setInt(7, servicio.getId());

                updated = pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar el servicio de peluquería: " + e.getMessage(), e);
            }
        }
        return updated;
    }

    public static List<ServicioPeluqueria> getServiciosByPeluqueriaId(int peluqueriaId) {
        List<ServicioPeluqueria> servicios = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_FIND_BY_PELUQUERIA_ID)) {

            pstmt.setInt(1, peluqueriaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Usuario duenio = new Usuario();
                    duenio.setId(rs.getInt("duenio_id"));
                    duenio.setNombreUsuario(rs.getString("duenio_nombre"));

                    Mascota mascota = new Mascota();
                    mascota.setId(rs.getInt("mascota_id"));
                    mascota.setNombre(rs.getString("mascota_nombre"));
                    mascota.setEspecie(rs.getString("mascota_especie"));
                    mascota.setRaza(rs.getString("mascota_raza"));
                    mascota.setDuenioMascota(duenio);

                    Usuario peluqueria = new Usuario();
                    peluqueria.setId(rs.getInt("peluqueria_id"));
                    peluqueria.setNombreUsuario(rs.getString("peluqueria_nombre"));

                    ServicioPeluqueria servicio = new ServicioPeluqueria();
                    servicio.setId(rs.getInt("id"));
                    servicio.setFecha(rs.getDate("fecha").toLocalDate());
                    Timestamp fechaHora = rs.getTimestamp("fecha_hora");
                    if (fechaHora != null) {
                        servicio.setFechaHora(fechaHora.toLocalDateTime());
                    }
                    servicio.setTipoServicio(rs.getString("tipoServicio"));
                    servicio.setPrecio(rs.getDouble("precio"));
                    servicio.setMascota(mascota);
                    servicio.setPeluqueria(peluqueria);

                    servicios.add(servicio);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener los servicios de peluquería por ID de peluquería: " + e.getMessage(), e);
        }
        return servicios;
    }

    public static List<ServicioPeluqueria> getHistorialServicios() {
        List<ServicioPeluqueria> historialServicios = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_GET_HISTORIAL_SERVICIOS);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Usuario duenio = new Usuario();
                duenio.setId(rs.getInt("duenio_id"));
                duenio.setNombreUsuario(rs.getString("duenio_nombre"));

                Mascota mascota = new Mascota();
                mascota.setId(rs.getInt("mascota_id"));
                mascota.setNombre(rs.getString("mascota_nombre"));
                mascota.setEspecie(rs.getString("mascota_especie"));
                mascota.setRaza(rs.getString("mascota_raza"));
                mascota.setDuenioMascota(duenio);

                Usuario peluqueria = new Usuario();
                peluqueria.setId(rs.getInt("peluqueria_id"));
                peluqueria.setNombreUsuario(rs.getString("peluqueria_nombre"));

                ServicioPeluqueria servicio = new ServicioPeluqueria();
                servicio.setId(rs.getInt("id"));
                servicio.setFecha(rs.getDate("fecha").toLocalDate());
                Timestamp fechaHora = rs.getTimestamp("fecha_hora");
                if (fechaHora != null) {
                    servicio.setFechaHora(fechaHora.toLocalDateTime());
                }
                servicio.setTipoServicio(rs.getString("tipoServicio"));
                servicio.setPrecio(rs.getDouble("precio"));
                servicio.setMascota(mascota);
                servicio.setPeluqueria(peluqueria);

                historialServicios.add(servicio);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener el historial de servicios de peluquería: " + e.getMessage(), e);
        }
        return historialServicios;
    }

    public static List<ServicioPeluqueria> getCitasDelDiaPorPeluqueria(int peluqueriaId) {
        List<ServicioPeluqueria> servicios = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_GET_CITAS_DIA_POR_PELUQUERIA)) {

            pstmt.setInt(1, peluqueriaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Usuario duenio = new Usuario();
                    duenio.setId(rs.getInt("duenio_id"));
                    duenio.setNombreUsuario(rs.getString("duenio_nombre"));

                    Mascota mascota = new Mascota();
                    mascota.setId(rs.getInt("mascota_id"));
                    mascota.setNombre(rs.getString("mascota_nombre"));
                    mascota.setEspecie(rs.getString("mascota_especie"));
                    mascota.setRaza(rs.getString("mascota_raza"));
                    mascota.setDuenioMascota(duenio);

                    Usuario peluqueria = new Usuario();
                    peluqueria.setId(rs.getInt("peluqueria_id"));
                    peluqueria.setNombreUsuario(rs.getString("peluqueria_nombre"));

                    ServicioPeluqueria servicio = new ServicioPeluqueria();
                    servicio.setId(rs.getInt("id"));
                    servicio.setFecha(rs.getDate("fecha").toLocalDate());
                    Timestamp fechaHora = rs.getTimestamp("fecha_hora");
                    if (fechaHora != null) {
                        servicio.setFechaHora(fechaHora.toLocalDateTime());
                    }
                    servicio.setTipoServicio(rs.getString("tipoServicio"));
                    servicio.setPrecio(rs.getDouble("precio"));
                    servicio.setMascota(mascota);
                    servicio.setPeluqueria(peluqueria);

                    servicios.add(servicio);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener las citas del día para la peluquería: " + e.getMessage(), e);
        }
        return servicios;
    }

    public static List<ServicioPeluqueria> getHistorialServiciosPorPeluqueria(int peluqueriaId) {
        List<ServicioPeluqueria> historialServicios = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_GET_HISTORIAL_POR_PELUQUERIA)) {

            pstmt.setInt(1, peluqueriaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Usuario duenio = new Usuario();
                    duenio.setId(rs.getInt("duenio_id"));
                    duenio.setNombreUsuario(rs.getString("duenio_nombre"));

                    Mascota mascota = new Mascota();
                    mascota.setId(rs.getInt("mascota_id"));
                    mascota.setNombre(rs.getString("mascota_nombre"));
                    mascota.setEspecie(rs.getString("mascota_especie"));
                    mascota.setRaza(rs.getString("mascota_raza"));
                    mascota.setDuenioMascota(duenio);

                    Usuario peluqueria = new Usuario();
                    peluqueria.setId(rs.getInt("peluqueria_id"));
                    peluqueria.setNombreUsuario(rs.getString("peluqueria_nombre"));

                    ServicioPeluqueria servicio = new ServicioPeluqueria();
                    servicio.setId(rs.getInt("id"));
                    servicio.setFecha(rs.getDate("fecha").toLocalDate());
                    Timestamp fechaHora = rs.getTimestamp("fecha_hora");
                    if (fechaHora != null) {
                        servicio.setFechaHora(fechaHora.toLocalDateTime());
                    }
                    servicio.setTipoServicio(rs.getString("tipoServicio"));
                    servicio.setPrecio(rs.getDouble("precio"));
                    servicio.setMascota(mascota);
                    servicio.setPeluqueria(peluqueria);

                    historialServicios.add(servicio);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener el historial de servicios para la peluquería: " + e.getMessage(), e);
        }
        return historialServicios;
    }
}