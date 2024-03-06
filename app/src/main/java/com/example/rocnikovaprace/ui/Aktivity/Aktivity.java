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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocnikovaprace.Adaptery.Adapter;
import com.example.rocnikovaprace.Casovac;
import com.example.rocnikovaprace.ImageSaver;
import com.example.rocnikovaprace.R;
import com.example.rocnikovaprace.Slovicka;
import com.example.rocnikovaprace.ui.SlovickoSnake;
import com.example.rocnikovaprace.ui.SpravujSlovicka.RecyclerViewClickInterface;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

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

private int q =0;

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

        adapter = new Adapter(this.getActivity(), source, getContext(), inter);

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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        //Načte slovíčka z databáze
        mAuth = FirebaseAuth.getInstance();
        kartickyRef = FirebaseDatabase.getInstance().getReference(userID + "rozvrh");
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
                            Bitmap b = convertStringToBitmap(obrazek);
                            // Možná FIX if(jeToSlovicko == false){source.add(sl);}
                            source.add(sl);
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

    private void deleteObject(String nazev) {
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


    //Umožňuje přesouvat položky v RecycleView
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        //Po přetáhnutí nahoru, nebo dolu smaže položku ze seznamu
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//FIX: Někam sem musím přidat, aby položku smazal z databáze
            int position = viewHolder.getAdapterPosition();

            SlovickoSnake a = source.get(position);
            source.remove(position);
            recyclerView.getAdapter().notifyItemRemoved(position);
            //Vytvoří SnacBar s tlačítkem zpět, které umožňuje vrátit smazanou položku zpět
            Snackbar.make(recyclerView, a.getNazev(), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.zpet), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //File file = new File(getContext().getFilesDir(), "slovicka.txt");
                            source.add(position, a);

                            recyclerView.getAdapter().notifyItemInserted(position);
                            q =1;


                        }
                    }).show();
                  if (q==0){deleteObject(a.getNazev()); }else {q=0;}

        }
    };




}