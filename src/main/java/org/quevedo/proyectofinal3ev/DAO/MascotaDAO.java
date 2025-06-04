package org.quevedo.proyectofinal3ev.DAO;

import org.quevedo.proyectofinal3ev.basedatos.ConnectionDB;
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MascotaDAO {
    private final static String SQL_GET_ALL = "SELECT m.id, m.nombre, m.especie, m.raza, m.fechaNacimiento, " +
            "u.id AS duenio_id, u.nombre_usuario AS duenio_nombre, u.email AS duenio_email, u.tipo_usuario " +
            "FROM Mascota m " +
            "JOIN Usuario u ON m.duenio_id = u.id WHERE u.tipo_usuario = ?";
    private final static String SQL_FIND_BY_ID = "SELECT m.id, m.nombre, m.especie, m.raza, m.fechaNacimiento, " +
            "u.id AS duenio_id, u.nombre_usuario AS duenio_nombre, u.email AS duenio_email, u.tipo_usuario " +
            "FROM Mascota m " +
            "JOIN Usuario u ON m.duenio_id = u.id WHERE m.id = ?";
    private final static String SQL_INSERT = "INSERT INTO Mascota (nombre, especie, raza, fechaNacimiento, duenio_id) VALUES (?, ?, ?, ?, ?)";
    private final static String SQL_DELETE = "DELETE FROM Mascota WHERE id = ?";
    private final static String SQL_UPDATE = "UPDATE Mascota SET nombre = ?, especie = ?, raza = ?, fechaNacimiento = ?, duenio_id = ? WHERE id = ?";

    public static List<Mascota> getAllMascotas() {
        List<Mascota> mascotas = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_GET_ALL)) {
            pstmt.setString(1, Usuario.TipoUsuario.DUENO.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Usuario duenio = new Usuario();
                    duenio.setId(rs.getInt("duenio_id"));
                    duenio.setNombreUsuario(rs.getString("duenio_nombre"));
                    duenio.setEmail(rs.getString("duenio_email"));
                    duenio.setTipoUsuario(Usuario.TipoUsuario.valueOf(rs.getString("tipo_usuario")));

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
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener las mascotas: " + e.getMessage(), e);
        }
        return mascotas;
    }

    public static Usuario findBasicById(int id) {
        Usuario usuario = null;
        String sql = "SELECT * FROM Usuario WHERE id = ?";
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                    usuario.setPassword(rs.getString("contraseña"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTipoUsuario(Usuario.TipoUsuario.valueOf(rs.getString("tipo_usuario")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar el usuario básico: " + e.getMessage(), e);
        }
        return usuario;
    }


    public static Usuario findCompleteById(int id) {
        Usuario usuario = findBasicById(id);
        if (usuario != null) {
            usuario.setMascotas(MascotaDAO.getMascotasByUsuarioId(id));
            usuario.setVisitasVeterinarias(VisitaVeterinariaDAO.getVisitasByUsuarioId(id));
            usuario.setServiciosPeluqueria(ServicioPeluqueriaDAO.getServiciosByUsuarioId(id));
        }
        return usuario;
    }

    public static Mascota insert(Mascota mascota) {
        if (mascota != null && mascota.getDuenio() > 0) {
            try (Connection connection = ConnectionDB.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, mascota.getNombre());
                pstmt.setString(2, mascota.getEspecie());
                pstmt.setString(3, mascota.getRaza());
                pstmt.setDate(4, Date.valueOf(mascota.getFechaNacimiento()));
                pstmt.setInt(5, mascota.getDuenio()); // Referenciar el ID del dueño

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Mascota insertada correctamente en la base de datos.");
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            mascota.setId(generatedKeys.getInt(1));
                        }
                    }
                } else {
                    mascota = null; // Null si no se logra insertar
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error al insertar la mascota: " + e.getMessage(), e);
            }
        } else {
            System.err.println("La información de la mascota o del dueño no es válida. Verifique los datos.");
            return null;
        }
        return mascota;
    }

    public static boolean delete(int id) {
        boolean deleted = false;
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_DELETE)) {
            pstmt.setInt(1, id);
            deleted = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la mascota: " + e.getMessage(), e);
        }
        return deleted;
    }

    public static List<Mascota> getMascotasByUsuarioId(int id) {
        List<Mascota> mascotas = new ArrayList<>();
        String sql = "SELECT m.id, m.nombre, m.especie, m.raza, m.fechaNacimiento, " +
                "u.id AS duenio_id, u.nombre_usuario AS duenio_nombre, u.email AS duenio_email, u.tipo_usuario " +
                "FROM Mascota m " +
                "JOIN Usuario u ON m.duenio_id = u.id WHERE m.duenio_id = ?";
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Usuario duenio = new Usuario();
                    duenio.setId(rs.getInt("duenio_id"));
                    duenio.setNombreUsuario(rs.getString("duenio_nombre"));
                    duenio.setEmail(rs.getString("duenio_email"));
                    duenio.setTipoUsuario(Usuario.TipoUsuario.valueOf(rs.getString("tipo_usuario")));

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
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener las mascotas por usuario ID: " + e.getMessage(), e);
        }
        return mascotas;
    }

    /**
     * Actualiza los datos de una Mascota en la base de datos.
     * @param mascota El objeto Mascota con los datos actualizados.
     * @return True si la Mascota fue actualizada exitosamente, false en caso contrario.
     */
    public static boolean update(Mascota mascota) {
        boolean updated = false;
        if (mascota != null) {
            try (Connection connection = ConnectionDB.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(SQL_UPDATE)) {

                pstmt.setString(1, mascota.getNombre());
                pstmt.setString(2, mascota.getEspecie());
                pstmt.setString(3, mascota.getRaza());
                pstmt.setDate(4, Date.valueOf(mascota.getFechaNacimiento()));
                pstmt.setInt(5, mascota.getDuenio()); // Referenciar el ID del dueño
                pstmt.setInt(6, mascota.getId());

                updated = pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar la mascota: " + e.getMessage(), e);
            }
        }
        return updated;
    }
}
