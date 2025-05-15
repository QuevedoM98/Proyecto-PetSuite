package org.quevedo.proyectofinal3ev;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/petsuite";
        String user = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            if (connection != null) {
                System.out.println("Conexión exitosa a la base de datos.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
