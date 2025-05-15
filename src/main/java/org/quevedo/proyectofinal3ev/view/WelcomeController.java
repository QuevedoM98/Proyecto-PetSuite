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

public class WelcomeController extends Controller implements Initializable {

    @FXML
    private Button btnIniciarSesion;

    @FXML
    private Button btnRegistro;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void cambiarPantallaSesion() throws IOException {
        App.currentController.changeScene(Scenes.INICIO_SESION, null);
    }

    public void cambiarPantallaRegistro() throws IOException {
        App.currentController.changeScene(Scenes.REGISTRO, null);
    }


    @Override
    public void onOpen(Object input) throws IOException {

    }

    @Override
    public void onClose(Object output) {

    }
}
