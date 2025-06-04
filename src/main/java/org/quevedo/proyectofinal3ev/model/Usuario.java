package org.quevedo.proyectofinal3ev.model;

import java.util.List;

public class Usuario {
    public enum TipoUsuario {
        DUENO, VETERINARIA, PELUQUERIA
    }

    private int id;
    private String nombreUsuario;
    private String password;
    private String email;
    private TipoUsuario tipoUsuario;
    private List<Mascota> mascotas;
    private List<VisitaVeterinaria> visitasVeterinarias;
    private List<ServicioPeluqueria> serviciosPeluqueria;

    public Usuario() {
    }

    public Usuario(String nombreUsuario, String password, String email, TipoUsuario tipoUsuario) {
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
    }

    public List<Mascota> getMascotas() {
        return mascotas;
    }

    public void setMascotas(List<Mascota> mascotas) {
        this.mascotas = mascotas;
    }

    public List<VisitaVeterinaria> getVisitasVeterinarias() {
        return visitasVeterinarias;
    }

    public void setVisitasVeterinarias(List<VisitaVeterinaria> visitasVeterinarias) {
        this.visitasVeterinarias = visitasVeterinarias;
    }

    public List<ServicioPeluqueria> getServiciosPeluqueria() {
        return serviciosPeluqueria;
    }

    public void setServiciosPeluqueria(List<ServicioPeluqueria> serviciosPeluqueria) {
        this.serviciosPeluqueria = serviciosPeluqueria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", tipoUsuario=" + tipoUsuario +
                '}';
    }
}