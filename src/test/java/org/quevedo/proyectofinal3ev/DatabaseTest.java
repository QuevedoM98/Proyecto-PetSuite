package org.quevedo.proyectofinal3ev;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {
    private static final String URL = "jdbc:mysql://localhost:3306/PetCareManager";
    private static final String USER = "root"; // Cambia esto si tu usuario es diferente
    private static final String PASSWORD = ""; // Cambia esto si tienes contraseña

    public static void main(String[] args) {
        System.out.println("Comprobando conectividad con la base de datos...");
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (connection != null) {
                System.out.println("Conexión exitosa a la base de datos PetCareManager.");
            } else {
                System.out.println("No se pudo establecer la conexión con la base de datos.");
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }
}
