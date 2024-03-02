package com.example.rocnikovaprace.ui.SpravujSlovicka;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocnikovaprace.Adaptery.Adapter;
import com.example.rocnikovaprace.Adaptery.MalyAdapter;
import com.example.rocnikovaprace.ImageSaver;
import com.example.rocnikovaprace.MainActivity;
import com.example.rocnikovaprace.R;
import com.example.rocnikovaprace.Slovicka;
import com.example.rocnikovaprace.databinding.FragmentGalleryBinding;
import com.example.rocnikovaprace.ui.SlovickoSnake;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.DatabaseReference;


import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class SpravujSlovicka extends Fragment implements Adapter.onNoteListener {

    private SpravujSlovickaModel spravujSlovickaModel;
    private @NonNull FragmentGalleryBinding binding;
    String nazevslova = "";
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    String cameraPermission[];
    String storagePermission[];
    ImageButton imageButton;
    private FirebaseAuth mAuth;
    private DatabaseReference kartickyRef;
    RecyclerView recyclerView;
    ArrayList<SlovickoSnake> source;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    Adapter adapter;
    LinearLayoutManager HorizontalLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        spravujSlovickaModel =
                new ViewModelProvider(this).get(SpravujSlovickaModel.class);
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Přidá položky do seznamu
        AddItemsToRecyclerViewArrayList();


        // Vytvoří dialog pro zadání hesla, bez kterého se nedá vstoupit do tohoto fragmentu
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.zadejte_heslo));

        // Nastaví vzhled dialogu
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.heslodialog,
                        null);
        builder.setView(customLayout);
        builder.setCancelable(false);
        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {

                    //Ověří správnost hesla
                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which) {
                        EditText editText
                                = customLayout
                                .findViewById(
                                        R.id.dialogoveheslo);
                        String zeSouboru = "";
                        File heslosoubor = new File(getContext().getFilesDir(), "heslo.txt");
                        try (BufferedReader br = new BufferedReader(new FileReader(heslosoubor))) {
                            zeSouboru = br.readLine();
                        } catch (Exception e) {
                            System.out.println("Chyba při čtení ze souboru.");
                        }
//Pokud je heslo špatné vrátí uživatele na domovskou obrazovku
                        if (zeSouboru.equals(editText.getText().toString())) {
                        } else {
                            Intent i = new Intent(getContext(), MainActivity.class);
                            startActivity(i);
                        }


                    }
                });

        // Zobrazí dialog
        AlertDialog dialog
                = builder.create();
        dialog.show();


        recyclerView
                = (RecyclerView) root.findViewById(
                R.id.recyclerview);
        RecyclerViewLayoutManager
                = new

                LinearLayoutManager(
                getContext());

        // Přiřadí LayoutManager k Recycler View
        recyclerView.setLayoutManager(
                RecyclerViewLayoutManager);


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


        return root;
    }


    // Uloží změněná data
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("karticky");

        reference.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error == null) {
                    System.out.println("Data byla úspěšně smazána.");
                } else {
                    System.err.println("Chyba při mazání dat: " + error.getMessage());
                }
            }
        });

        kartickyRef = FirebaseDatabase.getInstance().getReference("karticky");


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            int i = 0;
            while (i < source.size()){

                String kartickaId = kartickyRef.push().getKey();



                kartickyRef.child(kartickaId).setValue(source.get(i))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "úspěšně zapsáno do databáze");
                            } else {
                                Log.e("TAG", "do databáze se nepovedlo zapsat");
                            }
                        }); i++;
            }
        } else {
            // User is not signed in
        }

        binding = null;
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
                            Bitmap b = convertStringToBitmap(obrazek);
                            String itemKey = bookSnapshot.getKey();
                            if(jeToSlovicko == true){
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


    //Umožňuje přesouvat položky v RecycleView
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(source, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        //Po přetáhnutí nahoru, nebo dolu smaže položku ze seznamu
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            SlovickoSnake a = source.get(position);
            source.remove(position);
            recyclerView.getAdapter().notifyItemRemoved(position);
            //Vytvoří SnacBar s tlačítkem zpět, které umožňuje vrátit smazanou položku zpět
            Snackbar.make(recyclerView, a.getNazev(), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.zpet), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            File file = new File(getContext().getFilesDir(), "slovicka.txt");
                            source.add(position, a);
                            recyclerView.getAdapter().notifyItemInserted(position);


                        }
                    }).show();


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