package org.quevedo.proyectofinal3ev.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.quevedo.proyectofinal3ev.model.ServicioPeluqueria;
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.Peluqueria;

public class ServicioPeluqueriaDAO {
    private final Connection connection;

    public ServicioPeluqueriaDAO(Connection connection) {
        this.connection = connection;
    }

    public List<ServicioPeluqueria> getAllServicios() throws SQLException {
        String query = "SELECT * FROM ServicioPeluqueria";
        List<ServicioPeluqueria> servicios = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ServicioPeluqueria servicio = new ServicioPeluqueria(
                    rs.getInt("id"),
                    rs.getDate("fecha").toLocalDate(),
                    rs.getString("tipoServicio"),
                    rs.getDouble("precio"),
                    new Mascota(rs.getInt("mascota_id"), null, null, null, null, null), // Mascota con ID
                    new Peluqueria(rs.getInt("peluqueria_id"), null, null, null) // Peluqueria con ID
                );
                servicios.add(servicio);
            }
        }
        return servicios;
    }

    public void addServicio(ServicioPeluqueria servicio) throws SQLException {
        String query = "INSERT INTO ServicioPeluqueria (fecha, tipoServicio, precio, mascota_id, peluqueria_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(servicio.getFecha()));
            pstmt.setString(2, servicio.getTipoServicio());
            pstmt.setDouble(3, servicio.getPrecio());
            pstmt.setInt(4, servicio.getMascota().getId()); // Obtener ID de la mascota
            pstmt.setInt(5, servicio.getPeluqueria().getId()); // Obtener ID de la peluquería
            pstmt.executeUpdate();
        }
    }

    public void deleteServicio(int id) throws SQLException {
        String query = "DELETE FROM ServicioPeluqueria WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
