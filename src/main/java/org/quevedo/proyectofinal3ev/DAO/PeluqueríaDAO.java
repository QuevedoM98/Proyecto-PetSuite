package org.quevedo.proyectofinal3ev.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.quevedo.proyectofinal3ev.model.Peluqueria;

public class PeluqueríaDAO {
    private final Connection connection;

    public PeluqueríaDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Peluqueria> getAllPeluquerias() throws SQLException {
        String query = "SELECT * FROM Peluqueria";
        List<Peluqueria> peluquerias = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Peluqueria peluqueria = new Peluqueria(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("direccion"),
                    rs.getString("telefono")
                );
                peluquerias.add(peluqueria);
            }
        }
        return peluquerias;
    }

    public void addPeluqueria(Peluqueria peluqueria) throws SQLException {
        String query = "INSERT INTO Peluqueria (nombre, direccion, telefono) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, peluqueria.getNombre());
            pstmt.setString(2, peluqueria.getDireccion());
            pstmt.setString(3, peluqueria.getTelefono());
            pstmt.executeUpdate();
        }
    }

    public void deletePeluqueria(int id) throws SQLException {
        String query = "DELETE FROM Peluqueria WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
