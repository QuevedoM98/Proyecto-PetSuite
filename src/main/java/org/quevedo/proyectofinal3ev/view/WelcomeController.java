package org.quevedo.proyectofinal3ev.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.quevedo.proyectofinal3ev.App;
import org.quevedo.proyectofinal3ev.controller.Controller;
import org.quevedo.proyectofinal3ev.controller.Scenes;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de bienvenida de la aplicación.
 * Esta clase gestiona la interacción del usuario con la pantalla inicial,
 * permitiendo la navegación hacia las pantallas de inicio de sesión o registro.
 */
public class WelcomeController extends Controller implements Initializable {

    /**
     * Botón FXML para iniciar sesión en la aplicación.
     */
    @FXML
    private Button btnIniciarSesion;

    /**
     * Botón FXML para registrar una nueva cuenta de usuario.
     */
    @FXML
    private Button btnRegistro;


    /**
     * Inicializa el controlador después de que su elemento raíz ha sido completamente procesado.
     * En este caso, no hay lógica de inicialización específica para los componentes FXML.
     *
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Cambia la escena actual de la aplicación a la pantalla de inicio de sesión.
     * Este método se invoca cuando el usuario hace clic en el botón "Iniciar Sesión".
     *
     * @throws IOException Si ocurre un error al cargar la nueva escena (por ejemplo, si el archivo FXML no se encuentra).
     */
    public void cambiarPantallaSesion() throws IOException {
        App.currentController.changeScene(Scenes.INICIO_SESION, null);
    }

    /**
     * Cambia la escena actual de la aplicación a la pantalla de registro de usuarios.
     * Este método se invoca cuando el usuario hace clic en el botón "Registro".
     *
     * @throws IOException Si ocurre un error al cargar la nueva escena (por ejemplo, si el archivo FXML no se encuentra).
     */
    public void cambiarPantallaRegistro() throws IOException {
        App.currentController.changeScene(Scenes.REGISTRO, null);
    }

    /**
     * Método invocado cuando la vista se abre.
     * No realiza ninguna acción específica en este controlador, ya que el input no se utiliza directamente.
     *
     * @param input El objeto de entrada (no utilizado).
     * @throws IOException Si ocurre un error de E/S.
     */
    @Override
    public void onOpen(Object input) throws IOException {

    }

    /**
     * Método invocado cuando la vista se cierra.
     * No realiza ninguna acción específica en este controlador.
     *
     * @param output El objeto de salida (no utilizado).
     */
    @Override
    public void onClose(Object output) {

    }
}