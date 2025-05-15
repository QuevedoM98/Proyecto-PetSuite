package org.quevedo.proyectofinal3ev.model;

public class Veterinaria extends Establecimiento {
    private int id;

    public Veterinaria() {
    }

    public Veterinaria(int id, String nombre, String direccion, String telefono) {
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
