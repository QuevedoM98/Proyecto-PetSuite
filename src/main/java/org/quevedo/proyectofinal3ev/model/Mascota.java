package org.quevedo.proyectofinal3ev.model;

import java.time.LocalDate;

public class Mascota {
    private int id;
    private String nombre;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;
    private DuenioMascota duenioMascota;

    public Mascota() {
    }

    public Mascota(int id, String nombre, String especie, String raza, LocalDate fechaNacimiento, DuenioMascota duenioMascota) {
        this.id = id;
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
        return duenioMascota != null ? duenioMascota.getId() : 0; // Corregido: devolver el ID del dueño
    }

    public void setDuenio(DuenioMascota duenioMascota) {
        this.duenioMascota = duenioMascota;
    }
}
