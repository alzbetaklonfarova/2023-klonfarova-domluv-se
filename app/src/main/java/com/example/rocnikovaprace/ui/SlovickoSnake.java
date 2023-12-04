package com.example.rocnikovaprace.ui;

import java.io.Serializable;

public class SlovickoSnake implements Serializable {
    //Třída, díky které vytvářím objekty, které následně dávám yaml stringu (teda taková je momentálně myšlenka)
    String nazev;
    String obrazek;
    Boolean jeToSlovicko;
    String kategorie;

    public SlovickoSnake (String nazev, String obrazek, Boolean jeToSlovicko, String kategorie) {
        this.nazev = nazev;
        this.obrazek = obrazek;
        this.jeToSlovicko = jeToSlovicko;
        this.kategorie = kategorie;
    }

    @Override
    public String toString() {
        return "SlovickoSnake{" +
                "nazev='" + nazev + '\'' +
                ", obrazek='" + obrazek + '\'' +
                ", jeToSlovicko=" + jeToSlovicko +
                ", kategorie='" + kategorie + '\'' +
                '}';
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public String getObrazek() {
        return obrazek;
    }

    public void setObrazek(String obrazek) {
        this.obrazek = obrazek;
    }

    public Boolean getJeToSlovicko() {
        return jeToSlovicko;
    }

    public void setJeToSlovicko(Boolean jeToSlovicko) {
        this.jeToSlovicko = jeToSlovicko;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }
}
