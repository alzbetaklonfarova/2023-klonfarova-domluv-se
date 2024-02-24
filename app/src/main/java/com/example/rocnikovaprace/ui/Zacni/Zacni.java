package com.example.rocnikovaprace.ui.Zacni;

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
import com.example.rocnikovaprace.Adaptery.MalyAdapter;
import com.example.rocnikovaprace.ImageSaver;
import com.example.rocnikovaprace.R;
import com.example.rocnikovaprace.databinding.FragmentHomeBinding;
import com.example.rocnikovaprace.Adaptery.StredniAdapter;
import com.example.rocnikovaprace.ui.SlovickoSnake;
import com.example.rocnikovaprace.ui.SpravujSlovicka.RecyclerViewClickInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Zacni extends Fragment implements MalyAdapter.onNoteListener {

    private ZacniViewModel zacniViewModel;
    private @NonNull FragmentHomeBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference kartickyRef;
    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    ArrayList<SlovickoSnake> source = new ArrayList<>();
    ArrayList<SlovickoSnake> source2;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    RecyclerView.LayoutManager RecyclerViewLayoutManager2;
    MalyAdapter adapter;
    StredniAdapter adapter2;
    LinearLayoutManager HorizontalLayout;
    LinearLayoutManager HorizontalLayout2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        zacniViewModel =
                new ViewModelProvider(this).get(ZacniViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        recyclerView
                = (RecyclerView) root.findViewById(
                R.id.recyclerview3);
        RecyclerViewLayoutManager
                = new

                LinearLayoutManager(
                getContext());


        recyclerView2
                = (RecyclerView) root.findViewById(
                R.id.recyclerview2);
        RecyclerViewLayoutManager
                = new

                LinearLayoutManager(
                getContext());


        // Přiřadí LayoutManager k Recycler View
        recyclerView.setLayoutManager(
                RecyclerViewLayoutManager);

        recyclerView2.setLayoutManager(
                RecyclerViewLayoutManager2);

        // Přidá položka do ArrayListu
        AddItemsToRecyclerViewArrayList();
        AddItemsToRecyclerViewArrayList2();

        // Zavolá konstruktor
        adapter = new MalyAdapter(source);
        adapter2 = new StredniAdapter(source2);

        // Nastaví Horizontal Layout Manager pro Recycler view
        HorizontalLayout
                = new

                LinearLayoutManager(
                getActivity().

                        getApplicationContext(),

                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(HorizontalLayout);


        HorizontalLayout2
                = new

                LinearLayoutManager(
                getActivity().

                        getApplicationContext(),

                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView2.setLayoutManager(HorizontalLayout2);


        // Set adapter on recycler view
        recyclerView.setAdapter(adapter);
        recyclerView2.setAdapter(adapter2);

        //adapter.notifyDataSetChanged();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(simpleCallback2);
        itemTouchHelper2.attachToRecyclerView(recyclerView2);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //Přidá položky do seznamu
    public void AddItemsToRecyclerViewArrayList() {

        //File file = new File(getContext().getFilesDir(), "slovicka.txt");

//Načte slovíčka z databáze
        mAuth = FirebaseAuth.getInstance();
        kartickyRef = FirebaseDatabase.getInstance().getReference("karticky");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {


            kartickyRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    source = new ArrayList<>();
                    System.out.println("onDataChange " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                        SlovickoSnake sl = bookSnapshot.getValue(SlovickoSnake.class);
                        if (sl != null) {
                            String nazev = sl.nazev;
                            String obrazek = sl.obrazek;
                            Boolean jeToSlovicko = sl.jeToSlovicko;
                            String kategorie = sl.kategorie;
                            Bitmap b = convertStringToBitmap(obrazek);
                            source.add(sl);
                        }
                    }
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






      /*//Načte slovíčka ze souboru
        Yaml yaml = new Yaml(new Constructor(SlovickoSnake.class, new LoaderOptions()));
        try {
            InputStream inputStream = new FileInputStream(file);

            int count = 0;
            for (Object object : yaml.loadAll(inputStream)) {
                count++;
                if (object instanceof SlovickoSnake) {
                    source.add((SlovickoSnake) object);
                } else {
                    System.err.println("Nepodařilo se načíst objekt třídy SlovickoSnake.");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/



/*//Načte slovíčka ze souboru
        Yaml yaml = new Yaml(new Constructor(SlovickoSnake.class, new LoaderOptions()));
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("slovicka.yaml");

        int count = 0;
        for (Object object : yaml.loadAll(inputStream)) {
            count++;
            source.add((SlovickoSnake) object);
        }*/







    public void AddItemsToRecyclerViewArrayList2() {
        source2 = new ArrayList<>();


    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.UP) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            return false;
        }

        //Po přetáhnutí nahoru, nebo dolu smaže položku ze seznamu
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            source2.add(source.get(position));
            source.remove(position);
            recyclerView.getAdapter().notifyItemRemoved(position);
            recyclerView2.getAdapter().notifyItemInserted(source2.size());

        }
    };


    ItemTouchHelper.SimpleCallback simpleCallback2 = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.DOWN) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(source2, fromPosition, toPosition);
            recyclerView2.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        //Po přetáhnutí nahoru, nebo dolu smaže položku ze seznamu
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            SlovickoSnake slovicka = source2.get(position);
            source.add(slovicka);
            source2.remove(position);
            recyclerView2.getAdapter().notifyItemRemoved(position);
            recyclerView.getAdapter().notifyItemInserted(source.size());


        }
    };

    public static Bitmap convertStringToBitmap(String string) {
        byte[] byteArray1;
        byteArray1 = Base64.decode(string, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray1, 0,
                byteArray1.length);
        return bmp;
    }
}