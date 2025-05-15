package org.quevedo.proyectofinal3ev.model;

public class Peluqueria extends Establecimiento {
    private int id;

    public Peluqueria() {
    }

    public Peluqueria(int id, String nombre, String direccion, String telefono) {
        super(nombre, direccion, telefono);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
