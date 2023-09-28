package com.example.rocnikovaprace.ui.SpravujSlovicka;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rocnikovaprace.MainActivity;
import com.example.rocnikovaprace.R;
import com.example.rocnikovaprace.RegisterUser;
import com.example.rocnikovaprace.ui.Zacni.SignInUser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class VytvoreniHesla extends AppCompatActivity implements View.OnClickListener {

    private TextView register;

    //Tato aktivita se spustí jen jednou a to pouze na začátku prvního spuštění aplikace, aby uživatel vytvořil heslo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vytvoreni_hesla);

        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.register:
                startActivity(new Intent(this, RegisterUser.class));
                break;
            case R.id.signIn:
                startActivity(new Intent(this, SignInUser.class));
                break;
        }
    }
/*
    //Tato metoda se spustí po kliknutí na tlačítko. Uloží  zadané heslo do souboru
    public void UlozHeslo(View view) {
        EditText editText = findViewById(R.id.Heslo);
        //Pokud uživatel nezadal heslo, zobrazí se mu tento dialog s připomínkou
        if (editText.getText().toString().equals("")) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.zadejte_heslo))
                    .setPositiveButton("ok", null)
                    .show();
            return;
        }
        //Zapíše heslo do souboru
        File heslosoubor = new File(getApplicationContext().getFilesDir(), "heslo.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(heslosoubor, true))) {
            bw.write(editText.getText().toString());
            bw.newLine();
            bw.flush();

        } catch (Exception e) {
            editText.setText("Do souboru se nepovedlo zapsat.");
        }
// Spustí novou aktivitu
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);


    }*/


}