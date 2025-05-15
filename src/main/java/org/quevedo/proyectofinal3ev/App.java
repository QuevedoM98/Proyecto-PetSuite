package org.quevedo.proyectofinal3ev;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.quevedo.proyectofinal3ev.controller.AppController;
import org.quevedo.proyectofinal3ev.controller.Scenes;
import org.quevedo.proyectofinal3ev.controller.View;

import java.io.IOException;

public class App extends Application {
    public static Scene scene;
    public static Stage stage;
    public static AppController currentController;

    @Override
    public void start(Stage stage) throws IOException {
        // Cargar la vista inicial
        View view = AppController.loadFXML(Scenes.ROOT);

        // Crear la escena con el contenido cargado
        scene = new Scene((Parent) view.scene);
        currentController = (AppController) view.controller;
        currentController.onOpen(null);

        // Configurar la ventana principal
        stage.setScene(scene);
        stage.setTitle("PetSuite - Gestión"); // Título de la ventana
        stage.setResizable(true); // Permitir redimensionar
        stage.show(); // Mostrar la ventana
    }

    public static void main(String[] args) {
        launch();
    }
}