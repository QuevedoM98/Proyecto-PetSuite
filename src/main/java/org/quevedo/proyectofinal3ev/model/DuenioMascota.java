package org.quevedo.proyectofinal3ev.model;

public class DuenioMascota extends Persona {
    private String direccion;
    private String email;
    private int id;

    public DuenioMascota(int id, String nombre, String telefono, String direccion, String email) {
        super(nombre, telefono);
        this.id = id;
        this.direccion = direccion;
        this.email = email;
    }

    public DuenioMascota(String nombre, String telefono, String direccion, String email) {
        super(nombre, telefono);
        this.direccion = direccion;
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
