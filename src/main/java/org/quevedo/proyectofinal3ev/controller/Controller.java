package org.quevedo.proyectofinal3ev.controller;

import org.quevedo.proyectofinal3ev.App;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class Controller {
    App app;
    public void setApp(App app) {
        this.app = app;
    }

    public abstract void initialize(URL url, ResourceBundle resourceBundle);

    public abstract void onOpen(Object input)throws IOException;
    public abstract void onClose(Object output);
}
