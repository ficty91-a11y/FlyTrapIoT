package com.example.flytrapiot;

/**
 * Modelo de datos: representa una fila de la tabla "trampas".
 */
public class Trampa {

    // Opciones del Spinner "Tipo de trampa"
    public static final String[] TIPOS = {"Jackson", "McPhail", "Tephri", "Multilure"};

    private int id;                 // id INTEGER PRIMARY KEY AUTOINCREMENT
    private String codigoSector;    // codigo_sector TEXT NOT NULL
    private String tipoTrampa;      // tipo_trampa TEXT NOT NULL
    private int nivelCaptura;       // nivel_captura INTEGER NOT NULL
    private String observaciones;   // observaciones TEXT

    public Trampa(int id, String codigoSector, String tipoTrampa, int nivelCaptura, String observaciones) {
        this.id = id;
        this.codigoSector = codigoSector;
        this.tipoTrampa = tipoTrampa;
        this.nivelCaptura = nivelCaptura;
        this.observaciones = observaciones;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoSector() {
        return codigoSector;
    }

    public String getTipoTrampa() {
        return tipoTrampa;
    }

    public int getNivelCaptura() {
        return nivelCaptura;
    }

    public String getObservaciones() {
        return observaciones;
    }
}