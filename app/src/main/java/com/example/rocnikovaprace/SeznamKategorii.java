package com.example.rocnikovaprace;

import com.example.rocnikovaprace.ui.Kategorie;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SeznamKategorii {
    private static List<Kategorie> seznam = Arrays.asList(
            new Kategorie("Místa", "#FFFF00"), //žlutá
            new Kategorie("Lidé", "#FFA500"),  //oranžová
            new Kategorie("Věci", "#c41d1d"),  //červená
            new Kategorie("Činnosti", "#1d2bc4"), //modrá
            new Kategorie("Jídlo", "#5dc41d"), //zelená
            new Kategorie("Hračky", "#FF00FF") //fialová
    );

    public static List<Kategorie> komplet() {
        return seznam;
    }

    public static List<String> nazvy() {
        return seznam.stream()
                .map(Kategorie::getNazev)
                .collect(Collectors.toList());
    }

    public static Kategorie podleNazvu(String nazev) {
        // vyfiltrovat ze seznamu vsech kategorii tu, ktera ma stejny nazev jako pozadovana
        return seznam.stream()
                .filter(k -> k.nazev == nazev)
                .findFirst()  // v seznamu by mel byt nazev pouze jednou, proto bereme prvni
                .orElse(null);  // pokud neni nalezen, vracime null
    }
}
