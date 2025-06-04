package org.quevedo.proyectofinal3ev.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.quevedo.proyectofinal3ev.App;
import org.quevedo.proyectofinal3ev.controller.AppController;
import org.quevedo.proyectofinal3ev.controller.Controller;
import org.quevedo.proyectofinal3ev.controller.Scenes;
import org.quevedo.proyectofinal3ev.model.Usuario;
import org.quevedo.proyectofinal3ev.DAO.UsuarioDAO;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void cambiarAtras() throws IOException {
        App.currentController.changeScene(Scenes.WELCOME, null);
    }

    public void cambiarPantallaRegistro() throws IOException {
        App.currentController.changeScene(Scenes.REGISTRO, null);
    }


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


    @Override
    public void onOpen(Object input) throws IOException {

    }

    @Override
    public void onClose(Object output) {

    }
}
