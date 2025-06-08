package org.quevedo.proyectofinal3ev;

import org.quevedo.proyectofinal3ev.DAO.ServicioPeluqueriaDAO;
import org.quevedo.proyectofinal3ev.DAO.UsuarioDAO;
import org.quevedo.proyectofinal3ev.DAO.MascotaDAO;
import org.quevedo.proyectofinal3ev.DAO.VisitaVeterinariaDAO;
import org.quevedo.proyectofinal3ev.basedatos.ConnectionDB;
import org.quevedo.proyectofinal3ev.model.ServicioPeluqueria;
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.Usuario;
import org.quevedo.proyectofinal3ev.model.VisitaVeterinaria;

import java.sql.Connection;
import java.time.LocalDate;

public class DatabaseTest {
    public static void main(String[] args) {
        try {
            // Verificar conexión
            try (Connection connection = ConnectionDB.getConnection()) {
                System.out.println("Conexión exitosa: " + connection.isValid(2));
            } catch (Exception e) {
                System.out.println("Error al conectar con la base de datos: " + e.getMessage());
                return;
            }

            // Insertar usuario
            Usuario nuevoUsuario = new Usuario("usuarioTest", "password123", "usuario@test.com", Usuario.TipoUsuario.DUENO);
            Usuario usuarioInsertado = UsuarioDAO.insert(nuevoUsuario);
            if (usuarioInsertado == null || usuarioInsertado.getId() <= 0) {
                System.out.println("Error al insertar el usuario.");
                return;
            }
            System.out.println("Usuario insertado correctamente: ID=" + usuarioInsertado.getId());

            // Insertar mascota
            Mascota nuevaMascota = new Mascota("Firulais", "Perro", "Labrador", LocalDate.of(2020, 5, 15), usuarioInsertado);
            Mascota mascotaInsertada = MascotaDAO.insert(nuevaMascota);
            if (mascotaInsertada == null || mascotaInsertada.getId() <= 0) {
                System.out.println("Error al insertar la mascota.");
                return;
            }
            System.out.println("Mascota insertada correctamente: ID=" + mascotaInsertada.getId());

            // Insertar servicio de peluquería
            ServicioPeluqueria nuevoServicio = new ServicioPeluqueria(
                    LocalDate.now(),
                    "Baño y corte",
                    30.0,
                    mascotaInsertada,
                    usuarioInsertado
            );
            try {
                ServicioPeluqueriaDAO.insert(nuevoServicio);
                System.out.println("Servicio de peluquería insertado correctamente.");
            } catch (Exception e) {
                System.out.println("Error al insertar el servicio de peluquería: " + e.getMessage());
            }

            // Insertar visita veterinaria
            VisitaVeterinaria nuevaVisita = new VisitaVeterinaria(
                    LocalDate.now(),
                    "Vacunación",
                    "Vacuna contra la rabia aplicada.",
                    mascotaInsertada,
                    usuarioInsertado
            );
            try {
                VisitaVeterinariaDAO.insert(nuevaVisita);
                System.out.println("Visita veterinaria insertada correctamente.");
            } catch (Exception e) {
                System.out.println("Error al insertar la visita veterinaria: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}