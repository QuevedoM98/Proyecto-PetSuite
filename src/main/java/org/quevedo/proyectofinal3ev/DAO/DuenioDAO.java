package org.quevedo.proyectofinal3ev.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.quevedo.proyectofinal3ev.model.DuenioMascota;

public class DuenioDAO {
    private final Connection connection;

    public DuenioDAO(Connection connection) {
        this.connection = connection;
    }

    public List<DuenioMascota> getAllDuenios() throws SQLException {
        String query = "SELECT * FROM DuenioMascota";
        List<DuenioMascota> duenios = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                DuenioMascota duenio = new DuenioMascota(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("direccion"),
                    rs.getString("email")
                );
                duenios.add(duenio);
            }
        }
        return duenios;
    }

    public void addDuenio(DuenioMascota duenio) throws SQLException {
        String query = "INSERT INTO DuenioMascota (id, direccion, email) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, duenio.getId());
            pstmt.setString(2, duenio.getDireccion());
            pstmt.setString(3, duenio.getEmail());
            pstmt.executeUpdate();
        }
    }

    public void deleteDuenio(int id) throws SQLException {
        String query = "DELETE FROM DuenioMascota WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
