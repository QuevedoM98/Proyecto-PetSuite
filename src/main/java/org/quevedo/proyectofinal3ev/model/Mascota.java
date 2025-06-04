package org.quevedo.proyectofinal3ev.model;

import java.time.LocalDate;

public class Mascota {
    private int id;
    private String nombre;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;
    private Usuario duenioMascota;

    public Mascota() {
    }

    public Mascota(String nombre, String especie, String raza, LocalDate fechaNacimiento, Usuario duenioMascota) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.duenioMascota = duenioMascota;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getDuenio() {
        return duenioMascota != null ? duenioMascota.getId() : 0; // Devolver el ID del dueño
    }

    public void setDuenio(Usuario duenioMascota) {
        this.duenioMascota = duenioMascota;
    }
}
