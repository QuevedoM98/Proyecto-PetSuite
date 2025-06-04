package org.quevedo.proyectofinal3ev.DAO;

import org.quevedo.proyectofinal3ev.basedatos.ConnectionDB;
import org.quevedo.proyectofinal3ev.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private final static String SQL_INSERT = "INSERT INTO Usuario (nombre_usuario, contraseña, email, tipo_usuario) VALUES (?,?,?,?)";
    private final static String SQL_FIND_BY_ID = "SELECT * FROM Usuario WHERE id = ?";
    private final static String SQL_FIND_BY_TYPE = "SELECT * FROM Usuario WHERE tipo_usuario = ?";
    private final static String SQL_DELETE = "DELETE FROM Usuario WHERE id = ?";
    private final static String SQL_FIND_BY_USERNAME_AND_PASSWORD = "SELECT * FROM Usuario WHERE nombre_usuario = ? AND contraseña = ?";
    private final static String SQL_FIND_SERVICIOS_PELUQUERIA = "SELECT * FROM Usuario WHERE id IN (SELECT peluqueria_id FROM ServicioPeluqueria)";
    private final static String SQL_FIND_SERVICIOS_VETERINARIA = "SELECT * FROM Usuario WHERE id IN (SELECT veterinaria_id FROM VisitaVeterinaria)";
    private final static String SQL_FIND_MASCOTAS = "SELECT * FROM Usuario WHERE id IN (SELECT duenio_id FROM Mascota)";
    private final static String SQL_UPDATE = "UPDATE Usuario SET nombre_usuario = ?, contraseña = ?, email = ?, tipo_usuario = ? WHERE id = ?";

    private static void validateConnection(Connection connection) throws SQLException {

        if (connection == null || connection.isClosed()) {
            throw new SQLException("La conexión a la base de datos no está disponible o ya está cerrada.");
        }
    }


    /**
     * Inserta un nuevo Usuario en la base de datos.
     * @param usuario El objeto Usuario que se va a insertar.
     * @return El objeto Usuario insertado con su ID generado, o null si la inserción falló.
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
     * Busca un Usuario por su ID.
     * Operación: Lazy (se realiza solo cuando se solicita el Usuario).
     * @param id El ID del Usuario que se desea buscar.
     * @return El objeto Usuario si se encuentra, o null si no existe un Usuario con el ID dado.
     */
    public static Usuario findById(int id) {
        Usuario usuario = null;

        try {
            try (Connection connection = ConnectionDB.getConnection()) {
                if (connection == null || connection.isClosed()) {
                    throw new SQLException("La conexión está cerrada o no disponible.");
                }

                try (PreparedStatement pstmt = connection.prepareStatement(SQL_FIND_BY_ID)) {
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
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el usuario por ID: " + e.getMessage(), e);
        }

        return usuario;
    }

    /**
     * Busca todos los Usuarios de un tipo específico.
     * Operación: Lazy (se realiza solo cuando se solicita la lista de Usuarios).
     * @param tipoUsuario El tipo de Usuario que se desea buscar.
     * @return Una lista de objetos Usuario que coinciden con el tipo especificado.
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
     * Elimina un Usuario por su ID.
     * Operación: Eager (se realiza inmediatamente).
     * @param id El ID del Usuario que se desea eliminar.
     * @return True si el Usuario fue eliminado exitosamente, false en caso contrario.
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
     * Busca todos los Usuarios que tienen al menos un servicio veterinario.
     * Operación: Lazy (se realiza solo cuando se solicita la lista de Usuarios).
     * @return Una lista de objetos Usuario que tienen servicios veterinarios asociados.
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
                    usuario.setVisitasVeterinarias(VisitaVeterinariaDAO.getVisitasByUsuarioId(usuario.getId()));
                    usuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuarios con servicios veterinarios: " + e.getMessage(), e);
        }
        return usuarios;
    }

    /**
     * Busca todos los Usuarios que tienen al menos una mascota.
     * Operación: Lazy (se realiza solo cuando se solicita la lista de Usuarios).
     * @return Una lista de objetos Usuario que tienen mascotas asociadas.
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
                    usuario.setMascotas(MascotaDAO.getMascotasByUsuarioId(usuario.getId()));
                    usuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuarios con mascotas: " + e.getMessage(), e);
        }
        return usuarios;
    }

    /**
     * Busca un Usuario por su nombre de usuario y contraseña.
     * Operación: Lazy (se realiza solo cuando se solicita el Usuario).
     * @param usuario El nombre de usuario del Usuario.
     * @param contrasena La contraseña del Usuario.
     * @return El objeto Usuario si se encuentra, o null si no existe un Usuario que coincida con las credenciales dadas.
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
     * Busca usuarios relacionados con servicios de peluquería.
     * Operación: Lazy (se realiza solo cuando se solicita la lista de usuarios).
     * @return Una lista de objetos Usuario relacionados con servicios de peluquería.
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
                    usuario.setServiciosPeluqueria(ServicioPeluqueriaDAO.getServiciosByUsuarioId(usuario.getId()));
                    usuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuarios con servicios de peluquería: " + e.getMessage(), e);
        }
        return usuarios;
    }

    /**
     * Actualiza los datos de un Usuario en la base de datos.
     * @param usuario El objeto Usuario con los datos actualizados.
     * @return True si el Usuario fue actualizado exitosamente, false en caso contrario.
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
