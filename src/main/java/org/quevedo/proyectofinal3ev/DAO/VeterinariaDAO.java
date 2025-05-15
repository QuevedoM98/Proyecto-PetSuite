package org.quevedo.proyectofinal3ev.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.quevedo.proyectofinal3ev.model.Veterinaria;

public class VeterinariaDAO {
    private final Connection connection;

    public VeterinariaDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Veterinaria> getAllVeterinarias() throws SQLException {
        String query = "SELECT * FROM Veterinaria";
        List<Veterinaria> veterinarias = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Veterinaria veterinaria = new Veterinaria(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("direccion"),
                    rs.getString("telefono")
                );
                veterinarias.add(veterinaria);
            }
        }
        return veterinarias;
    }

    public void addVeterinaria(Veterinaria veterinaria) throws SQLException {
        String query = "INSERT INTO Veterinaria (nombre, direccion, telefono) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, veterinaria.getNombre());
            pstmt.setString(2, veterinaria.getDireccion());
            pstmt.setString(3, veterinaria.getTelefono());
            pstmt.executeUpdate();
        }
    }

    public void deleteVeterinaria(int id) throws SQLException {
        String query = "DELETE FROM Veterinaria WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
