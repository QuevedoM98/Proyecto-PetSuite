package org.quevedo.proyectofinal3ev.model;

import java.time.LocalDate;

public class ServicioPeluqueria implements Servicio {
    private int id;
    private LocalDate fecha;
    private String tipoServicio;
    private double precio;
    private Mascota mascota;
    private Usuario peluqueria; // Cambiado a Usuario

    public ServicioPeluqueria() {
    }

    public ServicioPeluqueria(int id, LocalDate fecha, String tipoServicio, double precio, Mascota mascota, Usuario peluqueria) {
        this.id = id;
        this.fecha = fecha;
        this.tipoServicio = tipoServicio;
        this.precio = precio;
        this.mascota = mascota;
        this.peluqueria = peluqueria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

    public Usuario getPeluqueria() {
        return peluqueria;
    }

    public void setPeluqueria(Usuario peluqueria) {
        this.peluqueria = peluqueria;
    }

    @Override
    public void realizarServicio() {
        System.out.println("Realizando servicio de peluquería: " + tipoServicio + " para la mascota: " + mascota.getNombre());
    }
}