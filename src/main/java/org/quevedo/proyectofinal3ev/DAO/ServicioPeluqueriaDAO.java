package org.quevedo.proyectofinal3ev.DAO;

import org.quevedo.proyectofinal3ev.basedatos.ConnectionDB;
import org.quevedo.proyectofinal3ev.model.ServicioPeluqueria;
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioPeluqueriaDAO {
    private final static String SQL_GET_ALL = "SELECT * FROM ServicioPeluqueria";
    private final static String SQL_INSERT = "INSERT INTO ServicioPeluqueria (fecha, tipoServicio, precio, mascota_id, peluqueria_id) VALUES (?, ?, ?, ?, ?)";
    private final static String SQL_DELETE = "DELETE FROM ServicioPeluqueria WHERE id = ?";
    private final static String SQL_FIND_BY_ID = "SELECT * FROM ServicioPeluqueria WHERE id = ?";
    private final static String SQL_UPDATE = "UPDATE ServicioPeluqueria SET fecha = ?, tipoServicio = ?, precio = ?, mascota_id = ?, peluqueria_id = ? WHERE id = ?";

    public static List<ServicioPeluqueria> getAllServicios() {
        List<ServicioPeluqueria> servicios = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_GET_ALL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Mascota mascota = new Mascota();
                mascota.setId(rs.getInt("mascota_id"));

                Usuario peluqueria = UsuarioDAO.findById(rs.getInt("peluqueria_id"));

                ServicioPeluqueria servicio = new ServicioPeluqueria(
                        rs.getInt("id"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getString("tipoServicio"),
                        rs.getDouble("precio"),
                        mascota,
                        peluqueria
                );
                servicios.add(servicio);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return servicios;
    }

    public static ServicioPeluqueria findById(int id) {
        ServicioPeluqueria servicio = null;
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_FIND_BY_ID)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Mascota mascota = new Mascota();
                    mascota.setId(rs.getInt("mascota_id"));

                    Usuario peluqueria = UsuarioDAO.findById(rs.getInt("peluqueria_id"));

                    servicio = new ServicioPeluqueria(
                            rs.getInt("id"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getString("tipoServicio"),
                            rs.getDouble("precio"),
                            mascota,
                            peluqueria
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return servicio;
    }

    public static void insert(ServicioPeluqueria servicio) {
        if (servicio != null) {
            try (Connection connection = ConnectionDB.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(SQL_INSERT)) {
                pstmt.setDate(1, Date.valueOf(servicio.getFecha()));
                pstmt.setString(2, servicio.getTipoServicio());
                pstmt.setDouble(3, servicio.getPrecio());
                pstmt.setInt(4, servicio.getMascota().getId());
                pstmt.setInt(5, servicio.getPeluqueria().getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
        return deleted;
    }

    public static List<ServicioPeluqueria> getServiciosByUsuarioId(int id) {
        List<ServicioPeluqueria> servicios = new ArrayList<>();
        String sql = "SELECT * FROM ServicioPeluqueria WHERE peluqueria_id = ?";
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Mascota mascota = new Mascota();
                    mascota.setId(rs.getInt("mascota_id"));

                    Usuario peluqueria = UsuarioDAO.findById(rs.getInt("peluqueria_id"));

                    ServicioPeluqueria servicio = new ServicioPeluqueria(
                            rs.getInt("id"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getString("tipoServicio"),
                            rs.getDouble("precio"),
                            mascota,
                            peluqueria
                    );
                    servicios.add(servicio);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener los servicios de peluquería: " + e.getMessage(), e);
        }
        return servicios;
    }

    /**
     * Actualiza los datos de un Servicio de Peluquería en la base de datos.
     * @param servicio El objeto ServicioPeluqueria con los datos actualizados.
     * @return True si el Servicio fue actualizado exitosamente, false en caso contrario.
     */
    public static boolean update(ServicioPeluqueria servicio) {
        boolean updated = false;
        if (servicio != null) {
            try (Connection connection = ConnectionDB.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(SQL_UPDATE)) {

                pstmt.setDate(1, Date.valueOf(servicio.getFecha()));
                pstmt.setString(2, servicio.getTipoServicio());
                pstmt.setDouble(3, servicio.getPrecio());
                pstmt.setInt(4, servicio.getMascota().getId());
                pstmt.setInt(5, servicio.getPeluqueria().getId());
                pstmt.setInt(6, servicio.getId());

                updated = pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar el servicio de peluquería: " + e.getMessage(), e);
            }
        }
        return updated;
    }
}
