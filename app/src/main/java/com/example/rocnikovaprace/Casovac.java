package com.example.rocnikovaprace;

import android.util.Log;

import com.example.rocnikovaprace.Adaptery.Adapter;
import com.example.rocnikovaprace.ui.SlovickoSnake;
import com.example.rocnikovaprace.ui.Aktivity.Aktivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

            //Tady z databáze
            deleteObject(polozka.getNazev());


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

    private void deleteObject(String nazev) {
        DatabaseReference kartickyRef = FirebaseDatabase.getInstance().getReference("rozvrh");
        Query query = kartickyRef.orderByChild("nazev").equalTo(nazev);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot objectSnapshot : dataSnapshot.getChildren()) {
                    objectSnapshot.getRef().removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "Objekt úspěšně smazán z databáze");
                                } else {
                                    Log.e("TAG", "Nepodařilo se smazat objekt z databáze");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Zde můžete obsloužit případ, kdy se operace nepodaří
            }
        });
    }

}
