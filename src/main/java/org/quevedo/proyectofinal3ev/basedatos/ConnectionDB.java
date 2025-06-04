package org.quevedo.proyectofinal3ev.basedatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    private final static String FILE= "connection.xml";
    //private final static String FILE= "src/main/resources/connection.xml";
    private static Connection con;
    private static ConnectionDB _instance;



    private ConnectionDB() {
        ConnectionProperties properties = XMLManager.readXML(new ConnectionProperties(), FILE);
        try {
            System.out.println(properties.getUrl() + " "+properties.getUser() +" "+ properties.getPassword());
            con = DriverManager.getConnection(properties.getUrl(), properties.getUser(), properties.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
            con = null;
        }
    }

    public static Connection getConnection() throws SQLException {
        if (_instance == null || con.isClosed()) {
            _instance = new ConnectionDB();
        }
        return con;
    }

    public static void closeConnection() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
