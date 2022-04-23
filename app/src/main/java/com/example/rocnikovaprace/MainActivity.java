package com.example.rocnikovaprace;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocnikovaprace.databinding.ActivityMainBinding;
import com.example.rocnikovaprace.ui.gallery.VytvoreniHesla;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
//Zjistí, jestli je už vytvořené heslo, pokud ne, spustí novou aktivitu
        File heslosoubor = new File(getApplicationContext().getFilesDir(), "heslo.txt");
        String zeSouboru = "";
        try (BufferedReader br = new BufferedReader(new FileReader(heslosoubor))) {
            zeSouboru = br.readLine();
        } catch (Exception e) {
            System.out.println("Chyba při čtení ze souboru.");
        }

        if (zeSouboru.equals("")) {
            Intent i = new Intent(getApplicationContext(), VytvoreniHesla.class);
            startActivity(i);

        }


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_aktivity, R.id.nav_organizace, R.id.nav_nastaveni)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if (notifyFragmentExit()) return false;
        switch(item.getItemId()){
            case R.id.nav_nastaveni2:
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_content_main, NastaveniFragment.class, null).commit();

                break;
        }
        return super.onOptionsItemSelected(item);
    }*/






    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                ImageButton imageButton = findViewById(R.id.imageButton);
                Picasso.with(this).load(resultUri).into(imageButton);
            }
        }
    }

    // Tato metoda se spustí po kliknutí na tlačítko uložit
    public void Ulozit(View view) {
        ImageButton imageButton = findViewById(R.id.imageButton);
        EditText editText = findViewById(R.id.Slovo);
        CheckBox slovicko = findViewById(R.id.Slovicko);
        CheckBox aktivita = findViewById(R.id.Aktivita);
        String nazev = editText.getText().toString();

// Natsaví soubor podle toho, jestli je to slovicko neebo aktivita
        if (slovicko.isChecked() == true && aktivita.isChecked() == false) {
            file = new File(getApplicationContext().getFilesDir(), "slovicka.txt");
        }

        if (aktivita.isChecked() == true && slovicko.isChecked() == false) {
            file = new File(getApplicationContext().getFilesDir(), "aktivity.txt");
        }
//Ošetřuje chybu, nejde vytvořit slovíčko i aktivitu zároveň
        if (aktivita.isChecked() == true && slovicko.isChecked() == true) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage("Vyberte buď slovíčko nebo aktivitu")
                    .setPositiveButton("ok", null)
                    .show();
            return;
        }

// Ošetřuje chybu. Uživatel musí zadat, jestli je to slovíčko, nebo aktivita
        if (aktivita.isChecked() == false && slovicko.isChecked() == false) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage("Vyberte buď slovíčko nebo aktivitu")
                    .setPositiveButton("ok", null)
                    .show();
            return;
        }
//Prohlídne všechna slovíčka a pokud, už takové slovíčko existuje, upozorní na to uživatele
        String zeSouboru;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((zeSouboru = br.readLine()) != null) {
                if (nazev.equals(zeSouboru)) {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setMessage("Toto slovíčko, nebo aktivita již existuje. Změňte název.")
                            .setPositiveButton("ok", null)
                            .show();
                    return;
                }

            }
        } catch (Exception e) {
            System.out.println("Chyba při čtení ze souboru.");
        }


//Vezme obrázek z tlačítka a uložího ho
        BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        new ImageSaver(this).
                setFileName(nazev + ".png").
                setDirectoryName(file.getName()).
                save(bitmap);
//Uloží název slovíčka nabo aktivity
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(nazev);
            bw.newLine();
            bw.flush();
            imageButton.setBackgroundResource(R.drawable.kliknutimvloziteobrazek);
            imageButton.setImageResource(R.drawable.kliknutimvloziteobrazek);
            editText.setText("");
            aktivita.setChecked(false);
            slovicko.setChecked(false);

        } catch (Exception e) {
            editText.setText("Do souboru se nepovedlo zapsat.");
        }


    }

    public void ZmenitHeslo (View view) {
        String zeSouboru = "";
        File heslosoubor = new File(getApplicationContext().getFilesDir(), "heslo.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(heslosoubor))) {
            zeSouboru = br.readLine();
        } catch (Exception e) {
            System.out.println("Chyba při čtení ze souboru.");
        }



        EditText editText2 = findViewById(R.id.editheslo);
        if(zeSouboru.equals(editText2.getText().toString())){

            EditText editText = findViewById(R.id.editnoveheslo);
            //Zapíše heslo do souboru
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(heslosoubor, false))) {
                bw.write(editText.getText().toString());
                bw.newLine();
                bw.flush();
                editText.setText("");
                editText2.setText("");

                Snackbar.make(getCurrentFocus(),"Heslo bylo změněno", Snackbar.LENGTH_LONG).show();


            } catch (Exception e) {
                editText.setText("Do souboru se nepovedlo zapsat.");
            }
        }
        else {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setMessage("Nesprávné heslo")
                        .setPositiveButton("ok", null)
                        .show();
                return;


        }

    }




}