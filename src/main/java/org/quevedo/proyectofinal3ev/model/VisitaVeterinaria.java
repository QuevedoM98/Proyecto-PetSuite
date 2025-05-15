package org.quevedo.proyectofinal3ev.model;

import java.time.LocalDate;

public class VisitaVeterinaria implements Servicio {
    private int id;
    private LocalDate fecha;
    private String motivo;
    private String observaciones;
    private Mascota mascota;
    private Veterinaria veterinaria;

    public VisitaVeterinaria() {
    }

    public VisitaVeterinaria(int id, LocalDate fecha, String motivo, String observaciones, Mascota mascota, Veterinaria veterinaria) {
        this.id = id;
        this.fecha = fecha;
        this.motivo = motivo;
        this.observaciones = observaciones;
        this.mascota = mascota;
        this.veterinaria = veterinaria;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

    public Veterinaria getVeterinaria() {
        return veterinaria;
    }

    public void setVeterinaria(Veterinaria veterinaria) {
        this.veterinaria = veterinaria;
    }

    @Override
    public void realizarServicio() {
        System.out.println("Realizando visita veterinaria para la mascota: " + mascota.getNombre());
    }
}
