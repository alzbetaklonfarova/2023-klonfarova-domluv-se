package com.example.rocnikovaprace.ui.Aktivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocnikovaprace.Adaptery.Adapter;
import com.example.rocnikovaprace.ImageSaver;
import com.example.rocnikovaprace.R;
import com.example.rocnikovaprace.Slovicka;
import com.example.rocnikovaprace.ui.SlovickoSnake;
import com.example.rocnikovaprace.ui.SpravujSlovicka.RecyclerViewClickInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Aktivity extends Fragment {

    private AktivityViewModel mViewModel;
    String nazevslova = "";

    private FirebaseAuth mAuth;
    private DatabaseReference kartickyRef;
    RecyclerView recyclerView;
    ArrayList<SlovickoSnake> source;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    Adapter adapter;
    LinearLayoutManager HorizontalLayout;

    public static Aktivity newInstance() {
        return new Aktivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.aktivity_fragment, container, false);


        recyclerView
                = (RecyclerView) view.findViewById(
                R.id.recyclerview100);
        RecyclerViewLayoutManager
                = new

                LinearLayoutManager(
                getContext());

        // Přiřadí LayoutManager k Recycler View
        recyclerView.setLayoutManager(
                RecyclerViewLayoutManager);

        // Přidá položky do seznamu
        AddItemsToRecyclerViewArrayList();

        // Zavolá konstruktor
        RecyclerViewClickInterface inter = new RecyclerViewClickInterface() {
            @Override
            public void setClick(int abc) {

            }
        };
        adapter = new Adapter(source, getContext(), inter);

        // Nastaví Horizontal Layout Manager pro Recycler view
        HorizontalLayout
                = new

                LinearLayoutManager(
                getActivity().

                        getApplicationContext(),

                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(HorizontalLayout);

        // Nastaví adapter pro recycler view
        recyclerView.setAdapter(adapter);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AktivityViewModel.class);
        // TODO: Use the ViewModel
    }


    //Přidá položky do seznamu
    public void AddItemsToRecyclerViewArrayList() {
        source = new ArrayList<>();

        //Načte slovíčka z databáze
        mAuth = FirebaseAuth.getInstance();
        kartickyRef = FirebaseDatabase.getInstance().getReference("karticky");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {


            kartickyRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                        SlovickoSnake sl = bookSnapshot.getValue(SlovickoSnake.class);
                        if (sl != null) {
                            String nazev = sl.nazev;
                            String obrazek = sl.obrazek;
                            Boolean jeToSlovicko = sl.jeToSlovicko;
                            String kategorie = sl.kategorie;
                            Bitmap b = convertStringToBitmap(obrazek);
                            if(jeToSlovicko == false){
                                source.add(sl);}
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("TAG", "Error reading data");
                }
            });


        } else {
            // User is not signed in
        }


    }
    public static Bitmap convertStringToBitmap(String string) {
        byte[] byteArray1;
        byteArray1 = Base64.decode(string, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray1, 0,
                byteArray1.length);
        return bmp;
    }

}