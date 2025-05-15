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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InicioSesionController extends Controller implements Initializable {

    @FXML
    private Button btnIniciarSesion;

    @FXML
    private Text btnAtras;

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void cambiarAtras() throws IOException {
        App.currentController.changeScene(Scenes.WELCOME, null);
    }

    public void iniciarSesion() {
        AppController.alertWarning("Has iniciado sesión con éxito.");
    }

    @Override
    public void onOpen(Object input) throws IOException {

    }

    @Override
    public void onClose(Object output) {

    }
}
