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

public class RegistroController extends Controller implements Initializable {
    @FXML
    private Button btnRegristrar;

    @FXML
    private Text btnAtras;

    @FXML
    private Text btnIniciarSesion;

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private TextField txtEmail;

    @FXML
    private RadioButton selectDuenio;

    @FXML
    private RadioButton selectVeterinaria;

    @FXML
    private RadioButton selectPeluqueria;

    private ToggleGroup userTypeGroup;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userTypeGroup = new ToggleGroup();
        selectDuenio.setToggleGroup(userTypeGroup);
        selectVeterinaria.setToggleGroup(userTypeGroup);
        selectPeluqueria.setToggleGroup(userTypeGroup);
    }


    public void cambiarAtras() throws IOException {
        App.currentController.changeScene(Scenes.WELCOME, null);
    }

    public void cambiarIniciarSesion() throws IOException {
        App.currentController.changeScene(Scenes.INICIO_SESION, null);
    }

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
            }
        }

        Usuario usuario = new Usuario(nombreUsuario, password, email, tipoUsuario);

        if (nombreUsuario.isEmpty() || password.isEmpty()) {
            AppController.alertError("Por favor, completa todos los campos.");
            return;
        }

        Usuario registrado = UsuarioDAO.insert(usuario);

        if (registrado != null) {
            AppController.alertWarning("Usuario registrado con éxito.");
        } else {
            AppController.alertError("Error al registrar el usuario.");
        }
    }

    @Override
    public void onOpen(Object input) throws IOException {

    }

    @Override
    public void onClose(Object output) {

    }
}