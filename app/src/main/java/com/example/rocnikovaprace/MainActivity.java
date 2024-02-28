package com.example.rocnikovaprace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;

import android.util.Base64;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private DatabaseReference kartickyRef;

    File file;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();


        setSupportActionBar(binding.appBarMain.toolbar);


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
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
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


    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e("TAG", "Error reading book data");
    }

    // Tato metoda se spustí po kliknutí na tlačítko uložit a uloží slovíčko, nebo aktivitu do Firebase databáze
    public void Ulozit(View view) throws InterruptedException {

                ImageButton imageButton = findViewById(R.id.imageButton);
        EditText editText = findViewById(R.id.Slovo);
        CheckBox slovicko = findViewById(R.id.Slovicko);
        CheckBox aktivita = findViewById(R.id.Aktivita);
        String nazev = editText.getText().toString();
        boolean jeToSlovicko = true;
        String kategorie = null;
        String obrazek;
        String strRef ="";

        //Vezme obrázek z tlačítka a převede ho do stringu
        BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        obrazek = convertBitmapToString(bitmap);



// Nastaví soubor podle toho, jestli je to slovicko neebo aktivita
        if (slovicko.isChecked() == true && aktivita.isChecked() == false) {
            jeToSlovicko = true;
            strRef = "karticky";

        }

        if (aktivita.isChecked() == true && slovicko.isChecked() == false) {
            jeToSlovicko = false;
            strRef = "aktivity";

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

        int poradi = 1;
        SlovickoSnake s = new SlovickoSnake(nazev, obrazek, jeToSlovicko, kategorie, poradi);
        /*   //Udělá z objektu yaml
        Yaml yaml1 = new Yaml(new Constructor(SlovickoSnake.class, new LoaderOptions()));
        String yamlStr = yaml1.dumpAs(s, Tag.MAP, null);*/

//ukládá objekt, neboli slovíčko do Firebase databáze



        kartickyRef = FirebaseDatabase.getInstance().getReference(strRef);


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String kartickaId = kartickyRef.push().getKey();



            kartickyRef.child(kartickaId).setValue(s)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "úspěšně zapsáno do databáze");
                        } else {
                            Log.e("TAG", "do databáze se nepovedlo zapsat");
                        }
                    });

        } else {
            // User is not signed in
        }

        editText.setText("");
        aktivita.setChecked(false);
        slovicko.setChecked(false);


        /*Yaml yaml2 = new Yaml(new Constructor(SlovickoSnake.class, new LoaderOptions()));
        SlovickoSnake sl2 = yaml2.load(yamlStr);*/



    }


    public void ZmenitUcet (View view){
        startActivity(new Intent(this, UserLogIn.class));
    }

    public void NovyUcet (View view){
        startActivity(new Intent(this, RegisterUser.class));
    }

    public void spocitejPolozky(DatabaseReference reference){


    }

}
