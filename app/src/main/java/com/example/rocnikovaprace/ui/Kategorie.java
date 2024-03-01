package com.example.rocnikovaprace.ui;

import java.io.Serializable;

public class Kategorie implements Serializable {

    public String nazev;

    public String barva;

    public Kategorie() {
    }

    public Kategorie(String nazev, String barva) {
        this.nazev = nazev;
        this.barva = barva;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public String getBarva() {
        return barva;
    }

    public void setBarva(String barva) {
        this.barva = barva;
    }
}
