package com.example.rocnikovaprace;

import com.example.rocnikovaprace.ui.Kategorie;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SeznamKategorii {
    private static List<Kategorie> seznam = Arrays.asList(
            new Kategorie("Jídlo", "#c41d1d"),
            new Kategorie("Sport", "#1d2bc4"),
            new Kategorie("Ovoce", "#5dc41d")
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
