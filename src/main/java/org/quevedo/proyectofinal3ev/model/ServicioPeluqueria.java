package org.quevedo.proyectofinal3ev.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ServicioPeluqueria implements Servicio {
    private int id;
    private LocalDate fecha;
    private String tipoServicio;
    private double precio;
    private Mascota mascota;
    private Usuario peluqueria; // Cambiado a Usuario
    private LocalDateTime fechaHora;


    public ServicioPeluqueria() {
    }

    public ServicioPeluqueria(LocalDate fecha, String tipoServicio, double precio, Mascota mascota, Usuario peluqueria) {

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

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
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

    public void setIdMascota(int id) {
        if (mascota != null) {
            mascota.setId(id);
        } else {
            System.out.println("No se puede establecer el ID de la mascota porque es nula.");
        }
    }
}