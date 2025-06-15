package org.quevedo.proyectofinal3ev.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.quevedo.proyectofinal3ev.App;
import org.quevedo.proyectofinal3ev.controller.Controller;
import org.quevedo.proyectofinal3ev.controller.Scenes;
import org.quevedo.proyectofinal3ev.model.Usuario;
import org.quevedo.proyectofinal3ev.DAO.UsuarioDAO;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de inicio de sesión de la aplicación.
 * Gestiona la autenticación de usuarios y la navegación a las diferentes pantallas según el tipo de usuario.
 */
public class InicioSesionController extends Controller implements Initializable {


    @FXML
    private Text irRegistro;

    @FXML
    private Text btnAtras;

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Button btnIniciarSesion;

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
     * Cambia la escena actual de la aplicación de vuelta a la pantalla de bienvenida.
     *
     * @throws IOException Si ocurre un error al cargar la nueva escena.
     */
    public void cambiarAtras() throws IOException {
        App.currentController.changeScene(Scenes.WELCOME, null);
    }

    /**
     * Cambia la escena actual de la aplicación a la pantalla de registro de usuarios.
     *
     * @throws IOException Si ocurre un error al cargar la nueva escena.
     */
    public void cambiarPantallaRegistro() throws IOException {
        App.currentController.changeScene(Scenes.REGISTRO, null);
    }

    /**
     * Intenta iniciar sesión con el nombre de usuario y la contraseña proporcionados en los campos de texto.
     * Valida que los campos no estén vacíos, busca el usuario en la base de datos y, si la autenticación es exitosa,
     * redirige al usuario a la pantalla correspondiente a su tipo de usuario.
     * Muestra alertas en caso de campos vacíos, credenciales incorrectas o tipos de usuario desconocidos.
     *
     * @throws IOException Si ocurre un error al cargar la escena del usuario autenticado.
     */
    public void iniciarSesion() throws IOException {
        String usuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, complete todos los campos.");
            alert.showAndWait();
            return;
        }

        // Buscar el usuario en la base de datos
        Usuario usuarioEncontrado = UsuarioDAO.findByUsernameAndPassword(usuario, contrasena);

        if (usuarioEncontrado == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Usuario o contraseña incorrectos.");
            alert.showAndWait();
            return;
        }

        // Detectar el tipo de usuario y cambiar a la pantalla correspondiente
        switch (usuarioEncontrado.getTipoUsuario()) {
            case DUENO:
                App.currentController.changeScene(Scenes.DUENO, usuarioEncontrado);
                break;
            case VETERINARIA:
                App.currentController.changeScene(Scenes.VETERINARIA, usuarioEncontrado);
                break;
            case PELUQUERIA:
                App.currentController.changeScene(Scenes.PELUQUERIA, usuarioEncontrado);
                break;
            default:
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Tipo de usuario desconocido.");
                alert.showAndWait();
        }
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