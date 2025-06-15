package org.quevedo.proyectofinal3ev.DAO;

import org.quevedo.proyectofinal3ev.basedatos.ConnectionDB;
import org.quevedo.proyectofinal3ev.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO (Data Access Object) para la entidad {@link Usuario}.
 * Proporciona métodos para interactuar con la base de datos y realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * y otras consultas relacionadas con los usuarios.
 */
public class UsuarioDAO {
    private final static String SQL_INSERT = "INSERT INTO Usuario (nombre_usuario, contraseña, email, tipo_usuario) VALUES (?, ?, ?, ?)";
    private final static String SQL_FIND_BY_ID = "SELECT * FROM Usuario WHERE id = ?";
    private final static String SQL_FIND_BY_TYPE = "SELECT * FROM Usuario WHERE tipo_usuario = ?";
    private final static String SQL_DELETE = "DELETE FROM Usuario WHERE id = ?";
    private final static String SQL_FIND_BY_USERNAME_AND_PASSWORD = "SELECT * FROM Usuario WHERE nombre_usuario = ? AND contraseña = ?";
    private final static String SQL_UPDATE = "UPDATE Usuario SET nombre_usuario = ?, contraseña = ?, email = ?, tipo_usuario = ? WHERE id = ?";
    private static final String SQL_FIND_SERVICIOS_VETERINARIA = "SELECT DISTINCT u.* " +
            "FROM Usuario u " +
            "JOIN VisitaVeterinaria v ON u.id = v.veterinaria_id";
    private static final String SQL_FIND_MASCOTAS = "SELECT DISTINCT u.* " +
            "FROM Usuario u " +
            "JOIN Mascota m ON u.id = m.duenio_id";
    private static final String SQL_FIND_SERVICIOS_PELUQUERIA = "SELECT DISTINCT u.* " +
            "FROM Usuario u " +
            "JOIN ServicioPeluqueria sp ON u.id = sp.peluqueria_id";

    /**
     * Valida si la conexión a la base de datos es nula o está cerrada.
     *
     * @param connection La conexión a validar.
     * @throws SQLException Si la conexión es nula o está cerrada.
     */
    private static void validateConnection(Connection connection) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("La conexión a la base de datos no está disponible o ya está cerrada.");
        }
    }

    /**
     * Inserta un nuevo usuario en la base de datos.
     * Si la inserción es exitosa, el ID generado para el usuario se establece en el objeto {@code usuario} proporcionado.
     *
     * @param usuario El objeto {@link Usuario} a insertar.
     * @return El objeto {@link Usuario} con el ID asignado si la inserción fue exitosa, o {@code null} si el objeto de entrada era nulo
     * o la inserción falló.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
    public static Usuario insert(Usuario usuario) {
        if (usuario != null) {
            try (Connection connection = ConnectionDB.getConnection();
                 PreparedStatement pst = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

                validateConnection(connection);

                pst.setString(1, usuario.getNombreUsuario());
                pst.setString(2, usuario.getPassword());
                pst.setString(3, usuario.getEmail());
                pst.setString(4, usuario.getTipoUsuario().name());
                int affectedRows = pst.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            usuario.setId(generatedKeys.getInt(1));
                        }
                    }
                } else {
                    usuario = null;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error al insertar el usuario: " + e.getMessage(), e);
            }
        } else {
            usuario = null;
        }
        return usuario;
    }

    /**
     * Busca un usuario por su ID en la base de datos.
     *
     * @param id El ID del usuario a buscar.
     * @return El objeto {@link Usuario} si se encuentra, o {@code null} si no existe ningún usuario con el ID dado.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
    public static Usuario findById(int id) {
        Usuario usuario = null;

        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_FIND_BY_ID)) {

            validateConnection(connection);

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
            throw new RuntimeException("Error al buscar el usuario por ID: " + e.getMessage(), e);
        }

        return usuario;
    }

    /**
     * Busca una lista de usuarios por su tipo en la base de datos.
     *
     * @param tipoUsuario El tipo de usuario a buscar (por ejemplo, VETERINARIO, CLIENTE, PELUQUERO).
     * @return Una lista de objetos {@link Usuario} que coinciden con el tipo especificado.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
    public static List<Usuario> findByType(Usuario.TipoUsuario tipoUsuario) {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_FIND_BY_TYPE)) {

            validateConnection(connection);

            pstmt.setString(1, tipoUsuario.name());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                    usuario.setPassword(rs.getString("contraseña"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTipoUsuario(Usuario.TipoUsuario.valueOf(rs.getString("tipo_usuario")));
                    usuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuarios por tipo: " + e.getMessage(), e);
        }
        return usuarios;
    }

    /**
     * Elimina un usuario de la base de datos por su ID.
     *
     * @param id El ID del usuario a eliminar.
     * @return {@code true} si el usuario fue eliminado con éxito, {@code false} en caso contrario.
     * @throws RuntimeException Si ocurre un error al eliminar el usuario de la base de datos.
     */
    public static boolean delete(int id) {
        boolean deleted = false;
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_DELETE)) {

            validateConnection(connection);

            pstmt.setInt(1, id);
            deleted = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el usuario: " + e.getMessage(), e);
        }
        return deleted;
    }

    /**
     * Busca una lista de usuarios que tienen servicios de veterinaria asociados.
     * Estos usuarios pueden ser veterinarios que han registrado visitas.
     *
     * @return Una lista de objetos {@link Usuario} que tienen servicios de veterinaria.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
    public static List<Usuario> FindwithServiciosVeterinaria() {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_FIND_SERVICIOS_VETERINARIA)) {

            validateConnection(connection);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                    usuario.setPassword(rs.getString("contraseña"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTipoUsuario(Usuario.TipoUsuario.valueOf(rs.getString("tipo_usuario")));
                    usuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuarios con servicios veterinarios: " + e.getMessage(), e);
        }
        return usuarios;
    }

    /**
     * Busca una lista de usuarios que son dueños de mascotas.
     *
     * @return Una lista de objetos {@link Usuario} que son dueños de al menos una mascota.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
    public static List<Usuario> FindwithMascotas() {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_FIND_MASCOTAS)) {

            validateConnection(connection);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                    usuario.setPassword(rs.getString("contraseña"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTipoUsuario(Usuario.TipoUsuario.valueOf(rs.getString("tipo_usuario")));
                    usuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuarios con mascotas: " + e.getMessage(), e);
        }
        return usuarios;
    }

    /**
     * Busca un usuario por su nombre de usuario y contraseña para fines de autenticación.
     *
     * @param usuario El nombre de usuario.
     * @param contrasena La contraseña del usuario.
     * @return El objeto {@link Usuario} si las credenciales son correctas, o {@code null} si no se encuentra ninguna coincidencia.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
    public static Usuario findByUsernameAndPassword(String usuario, String contrasena) {
        Usuario usuarioEncontrado = null;
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_FIND_BY_USERNAME_AND_PASSWORD)) {

            validateConnection(connection);

            pstmt.setString(1, usuario);
            pstmt.setString(2, contrasena);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuarioEncontrado = new Usuario();
                    usuarioEncontrado.setId(rs.getInt("id"));
                    usuarioEncontrado.setNombreUsuario(rs.getString("nombre_usuario"));
                    usuarioEncontrado.setPassword(rs.getString("contraseña"));
                    usuarioEncontrado.setEmail(rs.getString("email"));
                    usuarioEncontrado.setTipoUsuario(Usuario.TipoUsuario.valueOf(rs.getString("tipo_usuario")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el usuario por nombre de usuario y contraseña: " + e.getMessage(), e);
        }

        return usuarioEncontrado;
    }

    /**
     * Busca una lista de usuarios que tienen servicios de peluquería asociados.
     * Estos usuarios pueden ser peluqueros que han registrado servicios.
     *
     * @return Una lista de objetos {@link Usuario} que tienen servicios de peluquería.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
    public static List<Usuario> FindwithServiciosPeluqueria() {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection connection = ConnectionDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SQL_FIND_SERVICIOS_PELUQUERIA)) {

            validateConnection(connection);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                    usuario.setPassword(rs.getString("contraseña"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTipoUsuario(Usuario.TipoUsuario.valueOf(rs.getString("tipo_usuario")));
                    usuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuarios con servicios de peluquería: " + e.getMessage(), e);
        }
        return usuarios;
    }

    /**
     * Actualiza un usuario existente en la base de datos.
     *
     * @param usuario El objeto {@link Usuario} con los datos actualizados. El ID del usuario se utiliza para identificar el registro a actualizar.
     * @return {@code true} si el usuario fue actualizado con éxito, {@code false} en caso contrario.
     * @throws RuntimeException Si ocurre un error al actualizar el usuario en la base de datos.
     */
    public static boolean update(Usuario usuario) {
        boolean updated = false;
        if (usuario != null) {
            try (Connection connection = ConnectionDB.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(SQL_UPDATE)) {

                validateConnection(connection);

                pstmt.setString(1, usuario.getNombreUsuario());
                pstmt.setString(2, usuario.getPassword());
                pstmt.setString(3, usuario.getEmail());
                pstmt.setString(4, usuario.getTipoUsuario().name());
                pstmt.setInt(5, usuario.getId());

                updated = pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar el usuario: " + e.getMessage(), e);
            }
        }
        return updated;
    }
}