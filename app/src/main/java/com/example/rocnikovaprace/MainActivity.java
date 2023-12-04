package com.example.rocnikovaprace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;

import android.util.Base64;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.rocnikovaprace.databinding.ActivityMainBinding;
import com.example.rocnikovaprace.ui.SlovickoSnake;
import com.example.rocnikovaprace.ui.SpravujSlovicka.VytvoreniHesla;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    File file;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(binding.appBarMain.toolbar);/*
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
*/

        //Zjistí, jestli je uživatel přihlášen
        if (mAuth.getCurrentUser() == null) {
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

    //Metoda, která převádí bitmapu na string zdroj:http://www.java2s.com/example/android/graphics/convert-bitmap-to-string.html
    public static String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String result = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return result;
    }
    //Metoda, která převádí string na bitmapu zdroj:http://www.java2s.com/example/android/graphics/convert-bitmap-to-string.html
    public static Bitmap convertStringToBitmap(String string) {
        byte[] byteArray1;
        byteArray1 = Base64.decode(string, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray1, 0,
                byteArray1.length);
        return bmp;
    }

    // Tato metoda se spustí po kliknutí na tlačítko uložit a pomocí yamlu uloží slovíčko, nebo aktivitu
    public void Ulozit(View view) {
        ImageButton imageButton = findViewById(R.id.imageButton);
        EditText editText = findViewById(R.id.Slovo);
        CheckBox slovicko = findViewById(R.id.Slovicko);
        CheckBox aktivita = findViewById(R.id.Aktivita);
        String nazev = editText.getText().toString();
        boolean jeToSlovicko = true;
        String kategorie = null;
        String obrazek;

        //Vezme obrázek z tlačítka a převede ho do stringu
        BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        obrazek = convertBitmapToString(bitmap);

// Nastaví soubor podle toho, jestli je to slovicko neebo aktivita
        if (slovicko.isChecked() == true && aktivita.isChecked() == false) {
            jeToSlovicko = true;
            file = new File(getApplicationContext().getFilesDir(), "slovicka.txt");
        }

        if (aktivita.isChecked() == true && slovicko.isChecked() == false) {
            jeToSlovicko = false;
            file = new File(getApplicationContext().getFilesDir(), "aktivity.txt");
        }
//Ošetřuje chybu, nejde vytvořit slovíčko i aktivitu zároveň
        if (aktivita.isChecked() == true && slovicko.isChecked() == true) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.vyzva3))
                    .setPositiveButton("ok", null)
                    .show();
            return;
        }
// Ošetřuje chybu. Uživatel musí zadat, jestli je to slovíčko, nebo aktivita
        if (aktivita.isChecked() == false && slovicko.isChecked() == false) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.vyzva3))
                    .setPositiveButton("ok", null)
                    .show();
            return;
        }
       //Udělá z objektu yaml
        SlovickoSnake s = new SlovickoSnake(nazev, obrazek, jeToSlovicko, kategorie);
        Yaml yaml = new Yaml();
        String yamlStr = yaml.dump(s);

        //Uloží yaml string slovíčka nabo aktivity
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(yamlStr);
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
    /*// Tato metoda se spustí po kliknutí na tlačítko uložit
    public void Ulozit(View view) {
        ImageButton imageButton = findViewById(R.id.imageButton);
        EditText editText = findViewById(R.id.Slovo);
        CheckBox slovicko = findViewById(R.id.Slovicko);
        CheckBox aktivita = findViewById(R.id.Aktivita);
        String nazev = editText.getText().toString();

// Nastaví soubor podle toho, jestli je to slovicko neebo aktivita
        if (slovicko.isChecked() == true && aktivita.isChecked() == false) {
            file = new File(getApplicationContext().getFilesDir(), "slovicka.txt");
        }

        if (aktivita.isChecked() == true && slovicko.isChecked() == false) {
            file = new File(getApplicationContext().getFilesDir(), "aktivity.txt");
        }
//Ošetřuje chybu, nejde vytvořit slovíčko i aktivitu zároveň
        if (aktivita.isChecked() == true && slovicko.isChecked() == true) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.vyzva3))
                    .setPositiveButton("ok", null)
                    .show();
            return;
        }

// Ošetřuje chybu. Uživatel musí zadat, jestli je to slovíčko, nebo aktivita
        if (aktivita.isChecked() == false && slovicko.isChecked() == false) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.vyzva3))
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
                            .setMessage(getString(R.string.vyzva4))
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


    }*/
/*
    public void ZmenitHeslo(View view) {
        String zeSouboru = "";
        File heslosoubor = new File(getApplicationContext().getFilesDir(), "heslo.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(heslosoubor))) {
            zeSouboru = br.readLine();
        } catch (Exception e) {
            System.out.println("Chyba při čtení ze souboru.");
        }


        EditText editText2 = findViewById(R.id.editheslo);
        if (zeSouboru.equals(editText2.getText().toString())) {

            EditText editText = findViewById(R.id.editnoveheslo);
            //Zapíše heslo do souboru
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(heslosoubor, false))) {
                bw.write(editText.getText().toString());
                bw.newLine();
                bw.flush();
                editText.setText("");
                editText2.setText("");

                Snackbar.make(getCurrentFocus(), getString(R.string.heslosezmenilo), Snackbar.LENGTH_LONG).show();


            } catch (Exception e) {
                editText.setText("Do souboru se nepovedlo zapsat.");
            }
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.nespravne_heslo))
                    .setPositiveButton("ok", null)
                    .show();
            return;


        }

    }*/


    public void ZmenitUcet (View view){
        startActivity(new Intent(this, UserLogIn.class));
    }

    public void NovyUcet (View view){
        startActivity(new Intent(this, RegisterUser.class));
    }
}