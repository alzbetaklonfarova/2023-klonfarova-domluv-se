package com.example.rocnikovaprace.ui.SpravujSlovicka;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
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
import com.example.rocnikovaprace.ImageSaver;
import com.example.rocnikovaprace.MainActivity;
import com.example.rocnikovaprace.R;
import com.example.rocnikovaprace.Slovicka;
import com.example.rocnikovaprace.databinding.FragmentGalleryBinding;
import com.example.rocnikovaprace.ui.SlovickoSnake;
import com.google.android.material.snackbar.Snackbar;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class SpravujSlovicka extends Fragment {

    private SpravujSlovickaModel spravujSlovickaModel;
    private @NonNull FragmentGalleryBinding binding;
    String nazevslova = "";
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    String cameraPermission[];
    String storagePermission[];
    ImageButton imageButton;
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

        // Přidá položky do seznamu
        AddItemsToRecyclerViewArrayList();
//?????
      /*  // Zavolá konstruktor
        RecyclerViewClickInterface inter = new RecyclerViewClickInterface() {
            @Override
            public void setClick(int abc) {


                AlertDialog.Builder builder
                        = new AlertDialog.Builder(getContext());

                File heslosoubor = new File(getContext().getFilesDir(), "zkouska.txt");
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(heslosoubor, false))) {
                    bw.write("ne");
                    bw.newLine();
                    bw.flush();

                } catch (Exception e) {
                    System.out.println("Do souboru se nepovedlo zapsat.");
                }

                // Nastaví vzhled dialogu
                final View customLayout
                        = getLayoutInflater()
                        .inflate(
                                R.layout.layoutzmen,
                                null);

                EditText editText
                        = customLayout
                        .findViewById(
                                R.id.Slovonove);
                editText.setHint(source.get(abc).getNazev());


                builder.setView(customLayout);
                builder.setCancelable(true);
                builder.setPositiveButton(
                        getString(R.string.ulozit_zmeny),
                        new DialogInterface.OnClickListener() {

                            //Ověří správnost hesla
                            @RequiresApi(api = Build.VERSION_CODES.S)
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                //Prohlídne všechna slovíčka a pokud, už takové slovíčko existuje, upozorní na to uživatele
                                int p = 0;
                                while (p < source.size()) {
                                    if (editText.getText().toString().equals(source.get(p).getNazev()) && p != abc) {
                                        AlertDialog dialog2 = new AlertDialog.Builder(getContext())
                                                .setMessage(getString(R.string.vyzva1))
                                                .setPositiveButton("ok", null)
                                                .show();
                                        return;
                                    }
                                    p++;
                                }
                                // Pokud uživatel nezadal název slovíčka, vytvoří dialog, který ho na to upozorní
                                if (editText.getText() == null || editText.getText().toString().equals("")) {
                                    AlertDialog dialog2 = new AlertDialog.Builder(getContext())
                                            .setMessage(getString(R.string.vyzva2))
                                            .setPositiveButton("ok", null)
                                            .show();
                                    return;

                                }

                                BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();
                                Bitmap bitmapa = drawable.getBitmap();
                                Slovicka sl = new Slovicka(editText.getText().toString(), bitmapa);
                                source.set(abc, sl);
                                adapter.notifyItemChanged(abc);
                                File file = new File(getContext().getFilesDir(), "slovicka.txt");
                                BitmapDrawable drawable2 = (BitmapDrawable) imageButton.getDrawable();
                                Bitmap bitmap = drawable.getBitmap();
                                new ImageSaver(getContext()).
                                        setFileName(source.get(abc).slovo + ".png").
                                        setDirectoryName(file.getName()).
                                        save(bitmap);


                            }
                        });


                // Zobrazí dialog
                AlertDialog dialog
                        = builder.create();
                dialog.show();


            }
        };
        adapter = new Adapter(source, getContext(), inter);*/

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
        int i = 0;
        File file = new File(getContext().getFilesDir(), "slovicka.txt");

        try {
            FileWriter fw = new FileWriter(file, false);
            PrintWriter pw = new PrintWriter(fw, false);
            pw.flush();
            pw.close();
            fw.close();
        } catch (Exception exception) {

            System.out.println("Chyba v mazání");

        }

        while (i < source.size()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write(source.get(i).toString());
                bw.newLine();
                bw.flush();


            } catch (Exception e) {
                System.out.println("Do souboru se nepovedlo zapsat.");
            }

            i++;

        }


        binding = null;
    }

    //Přidá položky do seznamu
    public void AddItemsToRecyclerViewArrayList() {
        source = new ArrayList<>();
        File file = new File(getContext().getFilesDir(), "slovicka.txt");
        //Nejdřív je načte ze souboru
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String yamlStr;
            int p = 0;
            while ((yamlStr = br.readLine()) != null) {
                Yaml yaml = new Yaml();
                SlovickoSnake slovicko = yaml.loadAs(yamlStr, SlovickoSnake.class);


                //Přidá je do ArrayListu
                source.add(slovicko);
                p++;
            }
        } catch (Exception e) {
            System.out.println("Chyba při čtení ze souboru.");
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


}