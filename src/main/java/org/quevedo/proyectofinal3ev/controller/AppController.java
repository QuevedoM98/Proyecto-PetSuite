package org.quevedo.proyectofinal3ev.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.quevedo.proyectofinal3ev.App;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppController extends Controller implements Initializable {
    @FXML
    private BorderPane borderPane;

    @FXML
    private Controller centerController;

    /**
     * Method called when the connection is opened.
     * Changes the scene to the welcome screen.
     * @param input input data (not used in this method)
     * @throws IOException if an I/O error occurs while changing the scene
     */
    @Override
    public void onOpen(Object input) throws IOException {
        changeScene(Scenes.WELCOME,null);
    }

    /**
     * Changes the application scene.
     * @param scene the scene to change to
     * @param data data to pass to the new scene (can be null)
     * @throws IOException if an I/O error occurs while changing the scene
     */
    public void changeScene(Scenes scene,Object data) throws IOException{
        View view = loadFXML(scene);
        borderPane.setCenter(view.scene);
        this.centerController= view.controller;
        this.centerController.onOpen(data);
    }


    /**
     * Displays an error alert dialog.
     * @param text text to display in the alert dialog
     */
    public static void alertError(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(text);
        alert.setWidth(300);
        alert.setHeight(300);
        alert.showAndWait();

    }

    /**
     * Displays a warning alert dialog.
     * @param text text to display in the alert dialog
     */
    public static void alertWarning(String text){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(text);
        alert.setWidth(300);
        alert.setHeight(300);
        alert.showAndWait();
    }


    /**
     * Method called when the connection is closed.
     * @param output output data (not used in this method)
     */
    @Override
    public void onClose(Object output) {

    }


    /**
     * Initializes the controller.
     * @param url the location relative to the root of the FXML document being loaded
     * @param resourceBundle the resources that may be needed to initialize the controller (not used in this method)
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Loads an FXML file and returns the associated view.
     * @param scene the scene to load
     * @return the view associated with the scene
     * @throws IOException if an I/O error occurs while loading the FXML file
     */
    public static View loadFXML(Scenes scene) throws IOException {
        String url = scene.getUrl();
        FXMLLoader loader = new FXMLLoader(App.class.getResource(url));
        Parent parent = loader.load(); // Aseguramos que sea un Parent
        Controller controller = loader.getController();
        View view = new View();
        view.scene = parent; // Cambiado a Parent
        view.controller = controller;
        return view;
    }
}