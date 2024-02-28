package com.example.rocnikovaprace.ui.Organizace;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocnikovaprace.ImageSaver;
import com.example.rocnikovaprace.MainActivity;
import com.example.rocnikovaprace.Adaptery.MalyAdapter;
import com.example.rocnikovaprace.R;
import com.example.rocnikovaprace.Slovicka;
import com.example.rocnikovaprace.Adaptery.StredniAdapter;
import com.example.rocnikovaprace.ui.SlovickoSnake;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

public class Organizace extends Fragment implements MalyAdapter.onNoteListener {

    private OrganizaceViewModel mViewModel;
    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    ArrayList<SlovickoSnake> source;
    ArrayList<SlovickoSnake> source2;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    RecyclerView.LayoutManager RecyclerViewLayoutManager2;
    MalyAdapter adapter;
    StredniAdapter adapter2;
    LinearLayoutManager HorizontalLayout;
    LinearLayoutManager HorizontalLayout2;

     SlovickoSnake s;
    String yamlStr;

    public static Organizace newInstance() {
        return new Organizace();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

       /* File file = new File(getContext().getFilesDir(), "rozvrh.txt");
        if (source2.size() > 0) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
                SlovickoSnake s = new SlovickoSnake(source2.get(0).getNazev(), source2.get(0).getObrazek(), source2.get(0).getJeToSlovicko(), source2.get(0).getKategorie());
                Yaml yaml = new Yaml();
                yamlStr = yaml.dump(s);
                bw.write(yamlStr);
                bw.newLine();
                bw.flush();
                source2.remove(0);


            } catch (Exception e) {
                System.out.println("Do souboru se nepovedlo zapsat.");
            }

        }
        while (0 < source2.size()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                SlovickoSnake s = new SlovickoSnake(source2.get(0).getNazev(), source2.get(0).getObrazek(), source2.get(0).getJeToSlovicko(), source2.get(0).getKategorie());
                Yaml yaml = new Yaml();
                yamlStr = yaml.dump(s);
                bw.write(yamlStr);
                bw.newLine();
                bw.flush();
                source2.remove(0);


            } catch (Exception e) {
                System.out.println("Do souboru se nepovedlo zapsat.");
            }
        }*/
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.organizace_fragment, container, false);

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
                = (RecyclerView) view.findViewById(
                R.id.recyclerview103);
        RecyclerViewLayoutManager
                = new

                LinearLayoutManager(
                getContext());


        recyclerView2
                = (RecyclerView) view.findViewById(
                R.id.recyclerview102);
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


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(simpleCallback2);
        itemTouchHelper2.attachToRecyclerView(recyclerView2);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OrganizaceViewModel.class);
        // TODO: Use the ViewModel
    }

    //Přidá položky do seznamu
    public void AddItemsToRecyclerViewArrayList() {
        source = new ArrayList<>();
        File file = new File(getContext().getFilesDir(), "aktivity.txt");
        //Načte slovíčka ze souboru
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


    public void AddItemsToRecyclerViewArrayList2() {
        source2 = new ArrayList<>();
        File file = new File(getContext().getFilesDir(), "rozvrh.txt");
        File file2 = new File(getContext().getFilesDir(), "aktivity.txt");
        //Načte slovíčka ze souboru
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String yamlStr;
            int p = 0;
            while ((yamlStr = br.readLine()) != null) {
                Yaml yaml = new Yaml();
                SlovickoSnake slovicko = yaml.loadAs(yamlStr, SlovickoSnake.class);


                //Přidá je do ArrayListu
                source2.add(slovicko);
                p++;
            }
        } catch (Exception e) {
            System.out.println("Chyba při čtení ze souboru.");
        }

    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.UP) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            return false;
        }

        //Po přetáhnutí přidáá tuto položku do vrchního seznamu
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            SlovickoSnake sl = source.get(position);
            source2.add(source.get(position));
            source.remove(position);
            recyclerView.getAdapter().notifyItemRemoved(position);
            source.add(position, sl);
            recyclerView.getAdapter().notifyItemInserted(position);
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


        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            SlovickoSnake slovicka = source2.get(position);
            //source.add(slovicka);
            source2.remove(position);
            recyclerView2.getAdapter().notifyItemRemoved(position);
            //recyclerView.getAdapter().notifyItemInserted(source.size());


        }
    };


}