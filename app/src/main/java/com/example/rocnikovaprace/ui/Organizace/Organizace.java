package com.example.rocnikovaprace.ui.Organizace;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
import com.example.rocnikovaprace.UserLogIn;
import com.example.rocnikovaprace.ui.SlovickoSnake;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    private FirebaseAuth mAuth;
    private DatabaseReference kartickyRef;

     SlovickoSnake s;
    String yamlStr;

    public static Organizace newInstance() {
        return new Organizace();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(userID + "rozvrh");

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

        kartickyRef = FirebaseDatabase.getInstance().getReference(userID + "rozvrh");


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            int i = 0;
            while (i < source2.size()){

                String kartickaId = kartickyRef.push().getKey();



                kartickyRef.child(kartickaId).setValue(source2.get(i))
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
                        String email ="";
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            email = user.getEmail();
                            // nyní mám e-mail uživatele
                        }
                        AuthCredential credential = EmailAuthProvider.getCredential(email, editText.getText().toString());

                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //Toast.makeText(getContext(), "Heslo je správné", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getContext(), "Heslo je nesprávné", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(getContext(), MainActivity.class);
                                    startActivity(i);
                                }
                            }
                        });
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        //Načte slovíčka z databáze
        mAuth = FirebaseAuth.getInstance();
        kartickyRef = FirebaseDatabase.getInstance().getReference(userID + "aktivity");
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


    public void AddItemsToRecyclerViewArrayList2() {
        source2 = new ArrayList<>();
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
                            String itemKey = bookSnapshot.getKey();
                            //mozna FIX if(jeToSlovicko == true){source2.add(sl);}
                            source2.add(sl);
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
    public static Bitmap convertStringToBitmap(String string) {
        byte[] byteArray1;
        byteArray1 = Base64.decode(string, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray1, 0,
                byteArray1.length);
        return bmp;
    }

}