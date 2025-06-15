package org.quevedo.proyectofinal3ev.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.quevedo.proyectofinal3ev.App;
import org.quevedo.proyectofinal3ev.DAO.UsuarioDAO;
import org.quevedo.proyectofinal3ev.controller.AppController;
import org.quevedo.proyectofinal3ev.controller.Controller;
import org.quevedo.proyectofinal3ev.controller.Scenes;
import org.quevedo.proyectofinal3ev.model.Usuario;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de registro de nuevos usuarios en la aplicación.
 * Permite a los usuarios crear una nueva cuenta, seleccionando un nombre de usuario, contraseña, email
 * y un tipo de usuario (Dueño, Veterinaria o Peluquería).
 */
public class RegistroController extends Controller implements Initializable {
    /**
     * Botón FXML para iniciar el proceso de registro.
     */
    @FXML
    private Button btnRegristrar;

    /**
     * Elemento de texto FXML para navegar de vuelta a la pantalla anterior (Bienvenida).
     */
    @FXML
    private Text btnAtras;

    /**
     * Elemento de texto FXML para navegar a la pantalla de inicio de sesión.
     */
    @FXML
    private Text btnIniciarSesion;

    /**
     * Campo de texto FXML para que el usuario introduzca su nombre de usuario.
     */
    @FXML
    private TextField txtUsuario;

    /**
     * Campo de contraseña FXML para que el usuario introduzca su contraseña.
     */
    @FXML
    private PasswordField txtContrasena;

    /**
     * Campo de texto FXML para que el usuario introduzca su dirección de correo electrónico.
     */
    @FXML
    private TextField txtEmail;

    /**
     * RadioButton FXML para seleccionar el tipo de usuario "Dueño".
     */
    @FXML
    private RadioButton selectDuenio;

    /**
     * RadioButton FXML para seleccionar el tipo de usuario "Veterinaria".
     */
    @FXML
    private RadioButton selectVeterinaria;

    /**
     * RadioButton FXML para seleccionar el tipo de usuario "Peluquería".
     */
    @FXML
    private RadioButton selectPeluqueria;

    private ToggleGroup userTypeGroup;

    /**
     * Inicializa el controlador después de que su elemento raíz ha sido completamente procesado.
     * Configura el {@link ToggleGroup} para los RadioButtons de tipo de usuario,
     * asegurando que solo se pueda seleccionar uno a la vez.
     *
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userTypeGroup = new ToggleGroup();
        selectDuenio.setToggleGroup(userTypeGroup);
        selectVeterinaria.setToggleGroup(userTypeGroup);
        selectPeluqueria.setToggleGroup(userTypeGroup);
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
     * Cambia la escena actual de la aplicación a la pantalla de inicio de sesión.
     *
     * @throws IOException Si ocurre un error al cargar la nueva escena.
     */
    public void cambiarIniciarSesion() throws IOException {
        App.currentController.changeScene(Scenes.INICIO_SESION, null);
    }

    /**
     * Maneja el evento de registro de un nuevo usuario.
     * Recoge los datos de los campos de texto y el tipo de usuario seleccionado,
     * valida que los campos obligatorios no estén vacíos, y luego intenta insertar el nuevo usuario en la base de datos.
     * Muestra alertas de éxito o error según el resultado de la operación.
     */
    public void registrarme() {
        String nombreUsuario = txtUsuario.getText();
        String password = txtContrasena.getText();
        String email = txtEmail.getText();

        // Obtener el tipo de usuario seleccionado
        RadioButton selectedRadioButton = (RadioButton) userTypeGroup.getSelectedToggle();
        Usuario.TipoUsuario tipoUsuario = Usuario.TipoUsuario.DUENO; // Valor por defecto
        if (selectedRadioButton != null) {
            String tipoSeleccionado = selectedRadioButton.getText();
            switch (tipoSeleccionado) {
                case "Dueño":
                    tipoUsuario = Usuario.TipoUsuario.DUENO;
                    break;
                case "Veterinaria":
                    tipoUsuario = Usuario.TipoUsuario.VETERINARIA;
                    break;
                case "Peluquería":
                    tipoUsuario = Usuario.TipoUsuario.PELUQUERIA;
                    break;
                default:
                    // Si no se selecciona un tipo válido, se mantiene el valor por defecto o se lanza un error.
                    // Para este caso, se mantiene DUENO como default.
                    break;
            }
        } else {
            AppController.alertError("Por favor, selecciona un tipo de usuario (Dueño, Veterinaria o Peluquería).");
            return;
        }

        Usuario usuario = new Usuario(nombreUsuario, password, email, tipoUsuario);

        if (nombreUsuario.isEmpty() || password.isEmpty() || email.isEmpty()) {
            AppController.alertError("Por favor, completa todos los campos (nombre de usuario, contraseña y email).");
            return;
        }

        Usuario registrado = UsuarioDAO.insert(usuario);

        if (registrado != null) {
            AppController.alertWarning("Usuario registrado con éxito.");
            // Limpiar campos después de un registro exitoso
            txtUsuario.clear();
            txtContrasena.clear();
            txtEmail.clear();
            userTypeGroup.getSelectedToggle().setSelected(false); // Desseleccionar el radio button
        } else {
            AppController.alertError("Error al registrar el usuario. Es posible que el nombre de usuario o el email ya estén en uso.");
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