package org.quevedo.proyectofinal3ev.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.quevedo.proyectofinal3ev.model.VisitaVeterinaria;
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.Veterinaria;

public class VisitaVeterinariaDAO {
    private final Connection connection;

    public VisitaVeterinariaDAO(Connection connection) {
        this.connection = connection;
    }

    public List<VisitaVeterinaria> getAllVisitas() throws SQLException {
        String query = "SELECT * FROM VisitaVeterinaria";
        List<VisitaVeterinaria> visitas = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                VisitaVeterinaria visita = new VisitaVeterinaria(
                    rs.getInt("id"),
                    rs.getDate("fecha").toLocalDate(),
                    rs.getString("motivo"),
                    rs.getString("observaciones"),
                    new Mascota(rs.getInt("mascota_id"), null, null, null, null, null), // Mascota con ID
                    new Veterinaria(rs.getInt("veterinaria_id"), null, null, null) // Veterinaria con ID
                );
                visitas.add(visita);
            }
        }
        return visitas;
    }

    public void addVisita(VisitaVeterinaria visita) throws SQLException {
        String query = "INSERT INTO VisitaVeterinaria (fecha, motivo, observaciones, mascota_id, veterinaria_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(visita.getFecha()));
            pstmt.setString(2, visita.getMotivo());
            pstmt.setString(3, visita.getObservaciones());
            pstmt.setInt(4, visita.getMascota().getId()); // Obtener ID de la mascota
            pstmt.setInt(5, visita.getVeterinaria().getId()); // Obtener ID de la veterinaria
            pstmt.executeUpdate();
        }
    }

    public void deleteVisita(int id) throws SQLException {
        String query = "DELETE FROM VisitaVeterinaria WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
