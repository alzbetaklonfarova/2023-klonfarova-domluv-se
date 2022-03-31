package com.example.rocnikovaprace;

import android.graphics.Bitmap;

public class Slovicka {
    public String slovo;
    public Bitmap bitmapa;

    //Konstruktor, aby bylo možné vytvořit objekty
    public Slovicka(String slovo, Bitmap bitmapa) {
        this.slovo = slovo;
        this.bitmapa = bitmapa;
    }


    @Override
    public String toString() {
        return slovo;
    }
}
