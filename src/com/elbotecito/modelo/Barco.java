package com.elbotecito.modelo;

public class Barco {
    //POJO de Barco
    private String matricula;  //PK

    private String capacidadMax;

    private String numeroRegMerc;

    private String fechaRegMerc;

    private String nombre;

    private String estado;

    private String tipoBarco;

    public Barco() {
    }

    public Barco(String matricula, String capacidadMax, String numeroRegMerc, String fechaRegMerc, String nombre, String estado, String tipoBarco) {
        this.matricula = matricula;
        this.capacidadMax = capacidadMax;
        this.numeroRegMerc = numeroRegMerc;
        this.fechaRegMerc = fechaRegMerc;
        this.nombre = nombre;
        this.estado = estado;
        this.tipoBarco = tipoBarco;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCapacidadMax() {
        return capacidadMax;
    }

    public void setCapacidadMax(String capacidadMax) {
        this.capacidadMax = capacidadMax;
    }

    public String getNumeroRegMerc() {
        return numeroRegMerc;
    }

    public void setNumeroRegMerc(String numeroRegMerc) {
        this.numeroRegMerc = numeroRegMerc;
    }

    public String getFechaRegMerc() {
        return fechaRegMerc;
    }

    public void setFechaRegMerc(String fechaRegMerc) {
        this.fechaRegMerc = fechaRegMerc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoBarco() {
        return tipoBarco;
    }
    public void setTipoBarco(String tipoBarco) {
        this.tipoBarco = tipoBarco;
    }

    @Override
    public String toString() {
        return "Barco{" +
                "matricula='" + matricula + '\'' +
                ", capacidadMax='" + capacidadMax + '\'' +
                ", numeroRegMerc='" + numeroRegMerc + '\'' +
                ", fechaRegMerc='" + fechaRegMerc + '\'' +
                ", nombre='" + nombre + '\'' +
                ", estado='" + estado + '\'' +
                ", tipoBarco='" + tipoBarco + '\'' +
                '}';
    }
}
