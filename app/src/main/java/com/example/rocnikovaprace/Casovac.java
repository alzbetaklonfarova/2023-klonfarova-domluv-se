package com.example.rocnikovaprace;

import com.example.rocnikovaprace.Adaptery.Adapter;
import com.example.rocnikovaprace.ui.SlovickoSnake;
import com.example.rocnikovaprace.ui.Aktivity.Aktivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Casovac implements Runnable {
    List<SlovickoSnake> seznam;
    Thread vlakno;

    Adapter parent;

    public Casovac(SlovickoSnake polozka, Adapter fragment) {
        seznam = new ArrayList<SlovickoSnake>();
        seznam.add(polozka);
        this.parent = fragment;

        vlakno = new Thread(this);
        vlakno.start();
    }

    public void run() {
        while (seznam.size() > 0) {
            // vytahnu prvni element
            SlovickoSnake polozka = seznam.remove(0);
            // sleep
            try {
                Thread.sleep(polozka.cas * 1000 );
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // odstranim polozku
            int pozice = parent.getList().indexOf(polozka);
            parent.getList().remove(polozka);
            parent.getActivity().runOnUiThread( new Runnable(){
                @Override
                public void run() {
                    parent.notifyItemRemoved(pozice);
                }
            });
        }
        vlakno = null;
    }

    public void vlozCasovac(SlovickoSnake polozka){
        seznam.add(polozka);
        seznam.sort(Comparator.comparing(SlovickoSnake::getCas));

        // spustim nove vlakno pokud uz vsechny casovace vyexpirovaly
        if (vlakno == null) {
            vlakno = new Thread(this);
            vlakno.start();
        }
    }
}
