package org.quevedo.proyectofinal3ev.DAO;

import org.quevedo.proyectofinal3ev.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    private final Connection connection;

    public UsuarioDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para insertar un usuario
    public boolean insert(Usuario usuario) throws SQLException {
        String query = "INSERT INTO Usuario (nombreUsuario, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getPassword());
            return pstmt.executeUpdate() > 0;
        }
    }

    // Método para buscar un usuario por ID
    public Usuario findById(int id) throws SQLException {
        String query = "SELECT * FROM Usuario WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombreUsuario"),
                            rs.getString("password")
                    );
                }
            }
        }
        return null;
    }

    // Método para eliminar un usuario por ID
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM Usuario WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}
