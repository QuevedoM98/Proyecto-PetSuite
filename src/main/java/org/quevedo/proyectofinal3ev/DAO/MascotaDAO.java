package org.quevedo.proyectofinal3ev.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.DuenioMascota;

public class MascotaDAO {
    private final Connection connection;

    public MascotaDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Mascota> getAllMascotas() throws SQLException {
        String query = "SELECT m.id, m.nombre, m.especie, m.raza, m.fechaNacimiento, " +
                       "d.id AS duenio_id, d.nombre AS duenio_nombre, d.telefono AS duenio_telefono, " +
                       "d.direccion AS duenio_direccion, d.email AS duenio_email " +
                       "FROM Mascota m " +
                       "JOIN DuenioMascota d ON m.duenio_id = d.id";
        List<Mascota> mascotas = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                DuenioMascota duenio = new DuenioMascota(
                    rs.getInt("duenio_id"),
                    rs.getString("duenio_nombre"),
                    rs.getString("duenio_telefono"),
                    rs.getString("duenio_direccion"),
                    rs.getString("duenio_email")
                );
                Mascota mascota = new Mascota(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("especie"),
                    rs.getString("raza"),
                    rs.getDate("fechaNacimiento").toLocalDate(),
                    duenio
                );
                mascotas.add(mascota);
            }
        }
        return mascotas;
    }

    public void addMascota(Mascota mascota) throws SQLException {
        String query = "INSERT INTO Mascota (nombre, especie, raza, fechaNacimiento, duenio_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, mascota.getNombre());
            pstmt.setString(2, mascota.getEspecie());
            pstmt.setString(3, mascota.getRaza());
            pstmt.setDate(4, Date.valueOf(mascota.getFechaNacimiento()));
            pstmt.setInt(5, mascota.getDuenio()); // Obtener el ID del dueño
            pstmt.executeUpdate();
        }
    }

    public void deleteMascota(int id) throws SQLException {
        String query = "DELETE FROM Mascota WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
