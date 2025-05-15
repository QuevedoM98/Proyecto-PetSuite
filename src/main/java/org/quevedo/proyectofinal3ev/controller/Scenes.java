package org.quevedo.proyectofinal3ev.controller;

public enum Scenes {
    WELCOME("/org/quevedo/proyectofinal3ev/view/inicio.fxml"),
    ROOT("/org/quevedo/proyectofinal3ev/view/layout.fxml"),
    INICIO_SESION("/org/quevedo/proyectofinal3ev/view/iniciosesion.fxml"),
    REGISTRO("/org/quevedo/proyectofinal3ev/view/registro.fxml");

    private String url;

    Scenes(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
